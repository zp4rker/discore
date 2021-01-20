package com.zp4rker.discore.console.default

import com.zp4rker.discore.API
import com.zp4rker.discore.LOGGER
import com.zp4rker.discore.console.ConsoleCommand
import net.dv8tion.jda.api.entities.Category
import net.dv8tion.jda.api.entities.Guild
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * @author zp4rker
 */
object GuildCommand : ConsoleCommand {
    override fun handleCommand(command: String, params: Array<String>) {
        val guilds = API.guilds
        val withIds = params.any { it == "-ids" }
        val args = params.filter { !it.startsWith("-") }
        val options = params.filter { it.startsWith("-") }

        if (args.isEmpty()) {
            listGuilds(guilds, withIds)
        } else {
            val guild = if (args[0].all(Char::isDigit)) {
                API.getGuildById(args[0])
            } else {
                API.getGuildsByName(args[0], true).getOrNull(0)
            }

            if (guild == null) {
                LOGGER.warn("Could not find a guild with that ID or name!")
                return
            }

            if (args.size == 1) {
                with(LOGGER) {
                    info("Guild Info")
                    info("  Name: ${guild.name}")
                    if (withIds) info("  ID: ${guild.id}")
                    info("  Owner: ${guild.retrieveOwner().complete().user.asTag}${if (withIds) " (${guild.ownerId})" else ""}")
                    info("  Birthdate: ${guild.timeCreated.atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm"))}")
                    info("  Boost tier: ${guild.boostTier.toString().replace("_", " ")}")
                    info("  Total boosts: ${guild.boostCount}")
                    info("  Categories: ${guild.categories.size}")
                    info("  Textchannels: ${guild.textChannels.size}")
                    info("  Voicechannels: ${guild.voiceChannels.size}")
                    info("  Roles: ${guild.roles.size}")
                    guild.members.let {
                        info("  Total members: ${it.size} (${it.count { m -> m.user.isBot }.let { c -> "$c bot${if (c > 1) "s" else ""}" }})")
                    }
                    info("  Emotes: ${guild.emotes.size}")
                }
            } else {
                when (args[1].toLowerCase()) {
                    "channels" -> listChannels(guild, withIds)
                    "roles" -> listRoles(guild, withIds)
                    "members" -> listMembers(guild, withIds)
                }
            }
        }
    }

    private fun listGuilds(guilds: List<Guild>, withIds: Boolean) {
        with(LOGGER) {
            info("Connected guilds:")
            for (guild in guilds) info("- ${guild.name}${if (withIds) " (${guild.id})" else ""}")
        }
    }

    private fun listChannels(guild: Guild, withIds: Boolean) {
        with(LOGGER) {
            info("Guild Channels")
            (guild.textChannels.filter { it.parent == null } + guild.categories).sortedBy { it.position }.forEach {
                info("- ${it.type.name.first()}:${it.name}${if (withIds) " (${it.id})" else ""}")
                if (it is Category) {
                    it.channels.sortedBy { c -> c.position }.forEach { c ->
                        info("  - ${c.type.name.first()}:${c.name}${if (withIds) " (${it.id})" else ""}")
                    }
                }
            }
        }
    }

    private fun listRoles(guild: Guild, withIds: Boolean) {
        with(LOGGER) {
            info("Guild Roles")
            guild.roles.sortedByDescending { it.position }.forEach {
                info("- ${it.name}${if (withIds) " (${it.id})" else ""}")
            }
        }

    }

    private fun listMembers(guild: Guild, withIds: Boolean) {
        with(LOGGER) {
            info("Guild Members")
            guild.members.forEach {
                info("- ${if (it.isOwner) "OWNER " else if (it.user.isBot) "BOT " else ""}${it.user.asTag}${if (withIds) " (${it.id})" else ""}")
            }
        }
    }
}