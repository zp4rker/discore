package co.zpdev.bots.core.command;

import net.dv8tion.jda.core.Permission;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The command @interface.
 *
 * @author zpdev
 * @version 0.9_BETA
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String[] aliases();

    //String description() default "";

    //String usage() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @interface MainCommand {

        Permission[] perms() default {};

    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface SubCommand {

        int args() default 0;

    }

}
