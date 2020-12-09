package com.zp4rker.dsc.core.command

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
abstract class Command(
        val aliases: Array<String>,
        val description: String = "",
        val usage: String = aliases[0],
        val hidden: Boolean = false,

        val permission: Permission = Permission.MESSAGE_READ,
        val roles: Array<Long> = emptyArray(),

        val maxArgs: Int = 0,
        val minArgs: Int = 0,
        val mentionedMembers: Int = 0,
        val mentionedRoles: Int = 0,
        val mentionedChannels: Int = 0,

        val autoDelete: Boolean = false
) {

    abstract fun handle(args: Array<String>, message: Message, channel: TextChannel)

}