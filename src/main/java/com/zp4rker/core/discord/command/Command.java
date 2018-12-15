package com.zp4rker.core.discord.command;

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
    //String description() default "";
    String usage() default "";

    Permission permission() default Permission.MESSAGE_READ;
    long role() default 0;

    int args() default 0;
    int minArgs() default 0;
    int mentionedMembers() default 0;
    int mentionedChannels() default 0;
    int mentionedRoles() default 0;

    boolean autodelete() default false;

    //boolean hidden() default false;

}
