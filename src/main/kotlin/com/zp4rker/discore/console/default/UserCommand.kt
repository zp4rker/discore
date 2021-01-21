package com.zp4rker.discore.console.default

import com.zp4rker.discore.API
import com.zp4rker.discore.LOGGER
import com.zp4rker.discore.console.ConsoleCommand
import net.dv8tion.jda.api.entities.User
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * @author zp4rker
 */
object UserCommand : ConsoleCommand {
    override fun handleCommand(command: String, params: Array<String>) {
        val options = params.filter { it.startsWith("-") }.map(String::toUpperCase)
        val args = params.filter { !it.startsWith("-") }

        val withIds = args.contains("-ids")

        if (args.isEmpty()) return

        val user = if (args[0].all(Char::isDigit)) {
            API.getUserById(args[0])
        } else {
            if (args[0].matches(Regex(".*#\\d{4}"))) {
                API.getUserByTag(args[0])
            } else {
                API.getUsersByName(args[0], false).getOrNull(0)
            }
        } ?: run {
            LOGGER.warn("Could not find a user with that username or ID!")
            return
        }

        if (args.size == 1) {
            userInfo(user, withIds)
        }
    }

    private fun userInfo(user: User, withIds: Boolean) {
        with(LOGGER) {
            info("${if (user.isBot) "Bot" else "User"} Info")
            info("  Username: ${user.asTag}")
            if (withIds) info("  ID: ${user.id}")
            info("  Birthdata: ${user.timeCreated.atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm"))}")
            user.flags.let { flags ->
                if (flags.isNotEmpty()) {
                    info("  Badges:")
                    flags.forEach {
                        info("  - ${it.name}")
                    }
                }
            }
            info("  Mutual guilds: ${user.mutualGuilds.size}")
        }
    }
}