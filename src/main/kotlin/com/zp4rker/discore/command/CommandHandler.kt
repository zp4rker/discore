package com.zp4rker.discore.command

import com.zp4rker.discore.API
import com.zp4rker.discore.extenstions.embed
import com.zp4rker.discore.extenstions.event.on
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */
class CommandHandler(val prefix: String, val commands: MutableList<Command> = mutableListOf(), val jda: JDA = API) {

    private val async = Executors.newCachedThreadPool()

    fun registerCommands(vararg commands: Command) {
        commands.forEach { registerCommand(it) }
    }

    fun registerHelpCommand() {
        registerCommand(HelpCommand(this))
    }

    private fun registerCommand(command: Command) {
        commands.add(command)
    }

    init {
        jda.on<MessageReceivedEvent> { e ->
            if (!e.isFromGuild) return@on // no need to handle DMs for now

            val member = e.member ?: return@on

            if (!e.message.contentRaw.startsWith(prefix)) return@on

            val content = e.message.contentRaw.substring(prefix.length).let { if (it.contains(" ")) it.substring(0, it.indexOf(" ")) else it }
            if (commands.none { it.aliases.any { a -> content.equals(a, true) } }) return@on

            val command = commands.find { it.aliases.any { a -> content.equals(a, true) } } ?: return@on
            val label = command.aliases.find { content.equals(it, true) }!!
            if (command.permission != Permission.MESSAGE_READ && !member.hasPermission(command.permission)) {
                sendPermissionError(e.message)
                return@on
            } else if (command.roles.isNotEmpty()) {
                if (!member.hasPermission(Permission.ADMINISTRATOR) && member.roles.none { command.roles.contains(it.idLong) }) {
                    sendPermissionError(e.message)
                    return@on
                }
            }

            val args = e.message.contentRaw.substring((prefix + label).length).trimStart().split(" ").dropWhile { it == "" }
            if (command.maxArgs > 0 && command.maxArgs < args.size) {
                sendArgumentError(e.message, command)
                return@on
            } else if (command.minArgs > 0 && command.minArgs > args.size) {
                sendArgumentError(e.message, command)
                return@on
            } else if (command.mentionedMembers > 0 && command.mentionedMembers != e.message.mentionedMembers.size) {
                sendArgumentError(e.message, command)
                return@on
            } else if (command.mentionedRoles > 0 && command.mentionedRoles != e.message.mentionedRoles.size) {
                sendArgumentError(e.message, command)
                return@on
            } else if (command.mentionedChannels > 0 && command.mentionedChannels != e.message.mentionedChannels.size) {
                sendArgumentError(e.message, command)
                return@on
            }
            command.args.forEachIndexed { i, regex ->
                if (regex != "") {
                    val arg = args.getOrElse(i) { "" }
                    if (!Regex(regex).matches(arg)) {
                        sendArgumentError(e.message, command)
                        return@on
                    }
                }
            }

            if (command.autoDelete) e.message.delete().queue()

            async.submit { command.handle(args.toTypedArray(), e.message, e.message.textChannel) }
        }
    }

    private fun sendArgumentError(message: Message, command: Command) {
        val embed = embed {
            title {
                text = "Invalid arguments"
            }

            description = "You didn't provide the correct arguments, please try again. Correct usage: `$prefix${command.usage}`"

            color = "#ec644b"
        }

        sendError(message, embed)
    }

    private fun sendPermissionError(message: Message) {
        val embed = embed {
            title {
                text = "Invalid permissions"
            }

            description = "Sorry, but you don't have permission to run that command."

            color = "#ec644b"
        }

        sendError(message, embed)
    }

    private fun sendError(message: Message, embed: MessageEmbed) {
        message.channel.sendMessage(embed).queue {
            message.textChannel.deleteMessages(mutableListOf(it, message)).queueAfter(8, TimeUnit.SECONDS)
        }
    }

}