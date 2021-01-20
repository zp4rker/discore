package com.zp4rker.discore.console.default

import com.zp4rker.discore.API
import com.zp4rker.discore.LOGGER
import com.zp4rker.discore.console.ConsoleCommand
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * @author zp4rker
 */
object GuildsCommand : ConsoleCommand {
    override fun handleCommand(command: String, args: Array<String>) {
        val guilds = API.guilds

        if (args.isEmpty()) {
            with(LOGGER) {
                info("Connected guilds:")
                for (guild in guilds) info("- ${guild.name} (${guild.id})")
            }
            return
        }

        if (args.size == 1) {
            val guild = if (args[0].all(Char::isDigit)) {
                API.getGuildById(args[0])
            } else {
                API.getGuildsByName(args[0], true).getOrNull(0)
            }

            if (guild == null) {
                LOGGER.warn("Could not find a guild with that ID or name!")
                return
            }

            with(LOGGER) {
                info("Guild Info")
                info("  Name: ${guild.name}")
                info("  ID: ${guild.id}")
                info("  Owner: ${guild.retrieveOwner().complete().user.asTag} (${guild.ownerId})")
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
        }
    }
}