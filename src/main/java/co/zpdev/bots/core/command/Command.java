package co.zpdev.bots.core.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author ZP4RKER
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String[] aliases();

    String description() default "";

    String usage() default "";

    boolean directMessages() default false;

    boolean channelMessages() default true;

}
