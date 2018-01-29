package co.zpdev.bots.core.command.handler;

import co.zpdev.bots.core.exception.ExceptionHandler;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

/**
 * The command handler class.
 *
 * @author ZP4RKER
 */
public class CommandHandler {
    
    private final ExecutorService async;

    private final String prefix;
    private final HashMap<String, Command> commands = new HashMap<>();

    public CommandHandler(String prefix, ExecutorService async) {
        this.prefix = prefix;
        this.async = async;
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

        String[] splitContent = event.getMessage().getContentRaw().replace(prefix, "").split(" ");
        if (!commands.containsKey(splitContent[0].toLowerCase())) return;

        Command command = commands.get(splitContent[0].toLowerCase());
        co.zpdev.bots.core.command.Command annotation = command.getCommandAnnotation();

        if (event.getChannelType().equals(ChannelType.PRIVATE) && !annotation.directMessages()) return;
        if (event.getChannelType().equals(ChannelType.TEXT) && !annotation.channelMessages()) return;

        async.submit(() -> invokeMethod(command, getParameters(splitContent, command, event.getMessage(),
                event.getJDA())));
    }

    /**
     * Registers the specified command.
     *
     * @param command The command to register.
     */
    public void registerCommand(Object command) {
        for (Method method : command.getClass().getMethods()) {
            co.zpdev.bots.core.command.Command annotation = method.getAnnotation(co.zpdev.bots.core.command.Command.class);
            if (annotation == null) continue;

            if (annotation.aliases().length == 0) {
                throw new IllegalArgumentException("No aliases have been defined!");
            }

            Command simpleCommand = new Command(annotation, method, command);
            for (String alias : annotation.aliases()) {
                commands.put(alias.toLowerCase(), simpleCommand);
            }
        }
    }

    private Object[] getParameters(String[] splitMessage, Command command, Message message, JDA jda) {
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
            } else if (type == Object[].class) {
                parameters[i] = getObjectsFromString(jda, args);
            } else {
                parameters[i] = null;
            }
        }
        return parameters;
    }

    private Object[] getObjectsFromString(JDA jda, String[] args) {
        Object[] objects = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            objects[i] = getObjectFromString(jda, args[i]);
        }
        return objects;
    }

    private Object getObjectFromString(JDA jda, String arg) {
        try {
            return Integer.valueOf(arg);
        } catch (NumberFormatException e) {
        }
        if (arg.matches("<@([0-9]*)>")) {
            String id = arg.substring(2, arg.length() - 1);
            User user = jda.getUserById(id);
            if (user != null) {
                return user;
            }
        }
        if (arg.matches("<#([0-9]*)>")) {
            String id = arg.substring(2, arg.length() - 1);
            Channel channel = jda.getTextChannelById(id);
            if (channel != null) {
                return channel;
            }
        }
        return arg;
    }

    private void invokeMethod(Command command, Object[] paramaters) {
        Method m = command.getMethod();
        try {
            m.invoke(command.getExecutor(), paramaters);
        } catch (Exception e) {
            ExceptionHandler.handleException("invoking method [command handler]", e);
        }
    }

    public HashMap<String, Command> getCommands() {
        return commands;
    }

    public class Command {

        private final co.zpdev.bots.core.command.Command annotation;
        private final Method method;
        private final Object executor;

        Command(co.zpdev.bots.core.command.Command annotation, Method method, Object executor) {
            this.annotation = annotation;
            this.method = method;
            this.executor = executor;
        }

        co.zpdev.bots.core.command.Command getCommandAnnotation() {
            return annotation;
        }

        Method getMethod() {
            return method;
        }

        Object getExecutor() {
            return executor;
        }

    }

}
