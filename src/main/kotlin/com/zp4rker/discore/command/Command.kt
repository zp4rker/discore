package com.zp4rker.discore.command

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import kotlin.math.max

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

    @Deprecated("Will be removed in a later update,  replaced by 'args'.") val maxArgs: Int = 0,
    @Deprecated("Will be removed in a later update, replaced by 'args'.") val minArgs: Int = 0,
    val mentionedMembers: Int = 0,
    val mentionedRoles: Int = 0,
    val mentionedChannels: Int = 0,
    val args: Array<String> = Array(max(maxArgs, minArgs)) { "" },

    val autoDelete: Boolean = false
) {

    abstract fun handle(args: Array<String>, message: Message, channel: TextChannel)

}