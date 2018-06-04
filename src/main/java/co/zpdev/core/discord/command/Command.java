package co.zpdev.core.discord.command;

import net.dv8tion.jda.core.Permission;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The command @interface.
 *
 * @author zpdev
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String[] aliases();

    String description() default "";

    String usage() default "";

    Permission permission() default Permission.MESSAGE_READ;

    long role() default 0;

    boolean autodelete() default false;

    boolean hidden() default false;

}
