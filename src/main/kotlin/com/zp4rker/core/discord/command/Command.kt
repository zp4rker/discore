package com.zp4rker.core.discord.command

import net.dv8tion.jda.api.Permission

abstract class Command(
        val aliases: Array<String>,
        val description: String = "",
        val usage: String = aliases[0],

        val permission: Permission = Permission.MESSAGE_READ,
        val role: Long = 0,

        val args: Int = 0,
        val minArgs: Int = 0,
        val mentionedMembers: Int = 0,
        val mentionedRoles: Int = 0,
        val mentionedChannels: Int = 0,

        val autoDelete: Boolean = false
)