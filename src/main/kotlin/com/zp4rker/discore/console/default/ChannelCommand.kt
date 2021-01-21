package com.zp4rker.discore.console.default

import com.zp4rker.discore.API
import com.zp4rker.discore.LOGGER
import com.zp4rker.discore.console.ConsoleCommand
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.TextChannel
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * @author zp4rker
 */
object ChannelCommand : ConsoleCommand {
    override fun handleCommand(command: String, params: Array<String>) {
        val options = params.filter { it.startsWith("-") }.map { it.toLowerCase() }
        val args = params.filter { !it.startsWith("-") }

        val withIds = options.any { it == "-ids" }
        val detailed = options.any { arrayOf("-detailed", "-details").contains(it) }

        if (args.isEmpty()) return

        val channel = if(args[0].all(Char::isDigit)) {
            API.getGuildChannelById(args[0])
        } else {
            null
        } ?: run {
            LOGGER.warn("Could not find a channel with that ID!")
            return
        }

        if (args.size == 1) {
            channelInfo(channel, withIds, detailed)
        } else {
            when (args[0].toLowerCase()) {
                "permissions", "overrides" -> listPermissions(channel, withIds)
                "members", "users" -> listMembers(channel, withIds)
            }
        }
    }

    private fun channelInfo(channel: GuildChannel, withIds: Boolean, detailed: Boolean) {
        with(LOGGER) {
            info("Channel Info")
            info("  Name: ${channel.name}")
            if (withIds) info("  ID: ${channel.id}")
            info("  Birthdate: ${channel.timeCreated.atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm"))}")
            info("  Type: ${channel.type.name}")
            if (channel.parent != null) info("  Category: ${channel.parent!!.name}")
            info("  Permission overrides: ${channel.permissionOverrides.size}")
            channel.members.let {
                info("  Total members: ${it.size} (${it.count { m -> m.user.isBot }.let { c -> "$c bot${if (c > 1) "s" else ""}" }})")
            }
            if (detailed && channel is TextChannel) {
                val history = channel.history
                var count = 0
                var messages = history.retrievePast(100).complete().also { count++ }
                while (messages.size == 100) {
                    messages = history.retrievePast(100).complete().also { count++ }
                }
                info("  Total messages: $count")
            }
        }
    }

    private fun listPermissions(channel: GuildChannel, withIds: Boolean) {
        with(LOGGER) {
            info("Channel Permission Overrides")
            channel.permissionOverrides.forEach {
                info("- ${if (it.isMemberOverride) "M:${it.member!!.user.asTag}" else "R:${it.role!!.name}"}${if (withIds) " (${it.id})" else ""}")
            }
        }
    }

    private fun listMembers(channel: GuildChannel, withIds: Boolean) {
        with(LOGGER) {
            info("Channel Members")
            channel.members.forEach {
                info("- ${if (it.user.isBot) "BOT " else ""}${it.user.asTag}${if (withIds) " (${it.id})" else ""}")
            }
        }
    }
}