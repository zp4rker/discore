package com.zp4rker.discore.console.default

import com.zp4rker.discore.API
import com.zp4rker.discore.LOGGER
import com.zp4rker.discore.console.ConsoleCommand
import net.dv8tion.jda.api.entities.Role
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * @author zp4rker
 */
object RoleCommand : ConsoleCommand {
    override fun handleCommand(command: String, params: Array<String>) {
        val options = params.filter { it.startsWith("-") }.map { it.toLowerCase() }
        val args = params.filter { !it.startsWith("-") }

        val withIds = options.contains("-ids")
        val detailed = options.any { arrayOf("-detailed", "-details").contains(it) }

        if (args.isEmpty()) return

        val role = if (args[0].all(Char::isDigit)) {
            API.getRoleById(args[0])
        } else {
            null
        } ?: run {
            LOGGER.warn("Could not find a role with that ID!")
            return
        }

        if (args.size == 1) {
            roleInfo(role,  withIds, detailed)
        } else {
            when (args[1].toLowerCase()) {
                "members", "users" -> listMembers(role, withIds)
            }
        }
    }

    private fun roleInfo(role: Role, withIds: Boolean, detailed: Boolean) {
        with(LOGGER) {
            info("Role Info")
            info("  Name: ${role.name}")
            if (withIds) info("  ID: ${role.id}")
            info("  Birthdate: ${role.timeCreated.atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm"))}")
            role.color?.let { info("  Color: #${Integer.toHexString(it.rgb).substring(2)}") }
            info("  Mentionable: ${role.isMentionable}")
            info("  Permissions:")
            role.permissions.forEach {
                info("  - ${it.name}")
            }
            if (detailed) {
                info("  Members: ${role.guild.getMembersWithRoles(role).size}")
            }
        }
    }

    private fun listMembers(role: Role, withIds: Boolean) {
        with(LOGGER) {
            info("Role Members")
            role.guild.getMembersWithRoles(role).forEach {
                info("- ${if (it.user.isBot) "BOT " else ""}${it.user.asTag}${if (withIds) " (${it.id})" else ""}")
            }
        }
    }
}