package co.zpdev.core.discord.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The command @interface.
 *
 * TODO: Add more functionality
 *
 * @author zpdev
 * @version 0.8_BETA
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String[] aliases();

    /*String description() default "";

    String usage() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @interface MainCommand {

        Permission[] perms() default {};

    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface SubCommand {

        int args() default 0;

    }*/

}
