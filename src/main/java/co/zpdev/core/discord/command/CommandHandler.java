package co.zpdev.core.discord.command;

import co.zpdev.core.discord.exception.ExceptionHandler;
import emoji4j.EmojiUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The command handler.
 *
 * @author zpdev
 */
public class CommandHandler {
    
    private final ExecutorService async;
    private final String prefix;
    private final Map<String, ChatCommand> commands = new HashMap<>();
    private MessageEmbed permError;
    private final BiConsumer<TextChannel, MessageEmbed> errFunc;

    /**
     * Constructor
     *
     * @param prefix command prefix
     * @param packageName package which commands are located in
     */
    public CommandHandler(String prefix, String packageName) {
        this.prefix = prefix;
        this.async = Executors.newCachedThreadPool();

        permError = new EmbedBuilder()
                .setTitle("Invalid Permissions")
                .setDescription("You don't have the permissions required to perform this action!")
                .setColor(new Color(240, 71, 71)).build();

        errFunc = (t, e) -> t.sendMessage(e).queue(m -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                m.delete().queue();
            }
        }, 15000));

        try {
            registerCommands(packageName);
        } catch (Exception e) {
            ExceptionHandler.handleException("registering commands in package", e);
        }
    }

    /**
     * Sets the permission error embed.
     *
     * @param embed embed to set to
     */
    public void setPermError(MessageEmbed embed) {
        permError = embed;
    }

    /**
     * Gets all commands mapped by alias.
     *
     * @return the commands map
     */
    public Map<String, ChatCommand> getCommands() {
        return commands;
    }

    /**
     * The whole command handling method.
     *
     * @param event The event to handle.
     */
    @SubscribeEvent
    public void handle(MessageReceivedEvent event) {
        if (event.getGuild() == null) return;
        if (!event.getMessage().getContentRaw().startsWith(prefix)) return;

        String content = EmojiUtils.shortCodify(event.getMessage().getContentRaw().substring(prefix.length()));
        String[] splitContent = content.split(" ");
        if (commands.keySet().stream().noneMatch(content.toLowerCase()::startsWith)) return;

        ChatCommand command = commands.entrySet().stream()
                .filter(entry -> content.toLowerCase().startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList()).get(0);

        // Permission-based checks
        if (command.info.permission() != Permission.MESSAGE_READ) {
            List<Permission> perms = Arrays.asList(command.info.permission(), Permission.ADMINISTRATOR);
            if (event.getMember().getPermissions().stream().noneMatch(perms::contains)) {
                errFunc.accept(event.getTextChannel(), permError);
                return;
            }
        } else if (command.info.role() > 0) {
            if (!event.getMember().isOwner() && event.getMember().getRoles().stream().noneMatch(r -> r.getIdLong() == command.info.role())) {
                errFunc.accept(event.getTextChannel(), permError);
                return;
            }
        }

        // Argument-based checks
        String[] args = Arrays.copyOfRange(splitContent, 1, splitContent.length);
        MessageEmbed argError = new EmbedBuilder()
        .setTitle("Invalid Arguments")
        .setDescription("Invalid arguments! Correct usage: `" + command.info.usage() + "`")
        .setColor(new Color(240, 71, 71)).build();

        if (command.info.args() > 0) {
            if (args.length != command.info.args()) {
                errFunc.accept(event.getTextChannel(), argError);
                return;
            }
        } else if (command.info.minArgs() > 0) {
            if (args.length < command.info.minArgs()) {
                errFunc.accept(event.getTextChannel(), argError);
                return;
            }
        } else if (command.info.mentionedMembers() > 0) {
            if (event.getMessage().getMentionedMembers().size() != command.info.mentionedMembers()) {
                errFunc.accept(event.getTextChannel(), argError);
                return;
            }
        } else if (command.info.mentionedChannels() > 0) {
            if (event.getMessage().getMentionedChannels().size() != command.info.mentionedChannels()) {
                errFunc.accept(event.getTextChannel(), argError);
                return;
            }
        } else if (command.info.mentionedRoles() > 0) {
            if (event.getMessage().getMentionedRoles().size() != command.info.mentionedRoles()) {
                errFunc.accept(event.getTextChannel(), argError);
                return;
            }
        }


        if (command.info.autodelete()) event.getMessage().delete().queue();

        async.submit(() -> execute(command, getParameters(splitContent, command, event.getMessage())));
    }

    /**
     * Registers the commands located in the specified package.
     *
     * @param packageName package to search
     * @throws ClassNotFoundException when a class can't be found
     */
    private void registerCommands(String packageName) throws ClassNotFoundException {
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        for (String className : reflections.getAllTypes()) {
            Class c = Class.forName(className);
            for (Method method : c.getMethods()) {
                Command annotation = method.getAnnotation(Command.class);
                if (annotation == null) continue;

                if (annotation.aliases().length == 0) {
                    throw new IllegalArgumentException("No aliases have been defined for " + className + "!");
                } else if (Stream.of(annotation.aliases()).anyMatch(s -> s.contains(" "))) {
                    throw new IllegalArgumentException("Spaces are not allowed in command aliases!");
                }

                ChatCommand command = new ChatCommand(annotation, method);
                for (String alias : annotation.aliases()) {
                    commands.put(alias.toLowerCase(), command);
                }
            }
        }
    }

    /**
     * Fetches paramaters for the command method.
     *
     * @param splitMessage the message content split by " "
     * @param command the command data
     * @param message the message
     * @return the fetched paramaters
     */
    private Object[] getParameters(String[] splitMessage, ChatCommand command, Message message) {
        String[] args = Arrays.copyOfRange(splitMessage, 1, splitMessage.length);
        Class<?>[] parameterTypes = command.method.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];

            if (type == Message.class) parameters[i] = message;
            else if (type == String[].class) parameters[i] = args;
            else if (type == Member.class) parameters[i] = message.getMember();
            else if (type == TextChannel.class) parameters[i] = message.getTextChannel();
            else if (type == Guild.class) parameters[i] = message.getGuild();
            else parameters[i] = null;
        }
        return parameters;
    }

    /**
     * Executes the command method.
     *
     * @param command the command to run
     * @param paramaters the paramaters the command method needs
     */
    private void execute(ChatCommand command, Object[] paramaters) {
        try {
            command.method.invoke(command.method.getDeclaringClass().newInstance(), paramaters);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            ExceptionHandler.handleException("executing a command", e);
        }
    }

    public class ChatCommand {

        private final Command info;
        private final Method method;

        ChatCommand(Command annotation, Method method) {
            this.info = annotation;
            this.method = method;
        }

    }

}
