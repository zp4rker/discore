package co.zpdev.core.discord.command;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The command handler.
 *
 * @author zpdev
 */
public class CommandHandler {
    
    private final ExecutorService async;

    private final String prefix;
    private final HashMap<String, ChatCommand> commands = new HashMap<>();

    public CommandHandler(String prefix, String packageName) {
        this.prefix = prefix;
        this.async = Executors.newCachedThreadPool();
        try {
            registerCommands(packageName);
        } catch (Exception e) {
            System.out.println("Error registering commands!");
        }
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

        String[] splitContent = event.getMessage().getContentRaw().substring(prefix.length()).split(" ");
        if (!commands.containsKey(splitContent[0].toLowerCase())) return;

        ChatCommand command = commands.get(splitContent[0].toLowerCase());

        async.submit(() -> invokeMethod(command, getParameters(splitContent, command, event.getMessage(),
                event.getJDA())));
    }

    private void registerCommands(String packageName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        for (String className : reflections.getAllTypes()) {
            Class c = Class.forName(className);
            for (Method method : c.getMethods()) {
                Command annotation = method.getAnnotation(Command.class);
                if (annotation == null) continue;

                if (annotation.aliases().length == 0) {
                    throw new IllegalArgumentException("No aliases have been defined!");
                }

                ChatCommand command = new ChatCommand(/*annotation,*/ method, c.newInstance());
                for (String alias : annotation.aliases()) {
                    commands.put(alias.toLowerCase(), command);
                }
            }
        }
    }

    private Object[] getParameters(String[] splitMessage, ChatCommand command, Message message, JDA jda) {
        String[] args = Arrays.copyOfRange(splitMessage, 1, splitMessage.length);
        Class<?>[] parameterTypes = command.getMethod().getParameterTypes();
        final Object[] parameters = new Object[parameterTypes.length];
        int stringCounter = 0;
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            if (type == String.class) {
                if (stringCounter++ == 0) {
                } else {
                    if (args.length + 2 > stringCounter) {
                        parameters[i] = args[stringCounter - 2];
                    }
                }
            } else if (type == String[].class) {
                parameters[i] = args;
            } else if (type == Message.class) {
                parameters[i] = message;
            } else if (type == JDA.class) {
                parameters[i] = jda;
            } else if (type == TextChannel.class) {
                parameters[i] = message.getTextChannel();
            } else if (type == User.class) {
                parameters[i] = message.getAuthor();
            } else if (type == MessageChannel.class) {
                parameters[i] = message.getChannel();
            } else if (type == Guild.class) {
                if (!message.getChannelType().equals(ChannelType.TEXT)) {
                    parameters[i] = message.getGuild();
                }
            } else {
                parameters[i] = null;
            }
        }
        return parameters;
    }

    private void invokeMethod(ChatCommand command, Object[] paramaters) {
        Method m = command.getMethod();
        try {
            m.invoke(command.getExecutor(), paramaters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, ChatCommand> getCommands() {
        return commands;
    }

    public class ChatCommand {

        /*private final Command annotation;*/
        private final Method method;
        private final Object executor;

        ChatCommand(/*Command annotation,*/ Method method, Object executor) {
            /*this.annotation = annotation;*/
            this.method = method;
            this.executor = executor;
        }

        /*Command getCommandAnnotation() {
            return annotation;
        }*/

        Method getMethod() {
            return method;
        }

        Object getExecutor() {
            return executor;
        }

    }

}
