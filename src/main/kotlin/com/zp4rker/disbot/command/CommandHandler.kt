package com.zp4rker.disbot.command

import com.zp4rker.disbot.API
import com.zp4rker.disbot.Bot
import com.zp4rker.disbot.HIDDEN_EMBED_COLOUR
import com.zp4rker.disbot.extenstions.embed
import com.zp4rker.disbot.extenstions.event.on
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */
class CommandHandler(private val bot: Bot, val prefix: String, val commands: MutableMap<String, Command> = mutableMapOf()) {

    private val async = Executors.newCachedThreadPool()

    fun registerCommands(vararg commands: Command) {
        commands.forEach { registerCommand(it) }
    }

    fun registerHelpCommand() {
        registerCommand(HelpCommand(this))
    }

    private fun registerCommand(command: Command) {
        commands[command.aliases[0]] = command
    }

    init {
        API.on<MessageReceivedEvent> {
            if (!it.isFromGuild) return@on // no need to handle DMs for now

            val member = it.member ?: return@on

            if (!it.message.contentRaw.startsWith(prefix)) return@on

            val content = it.message.contentRaw.substring(prefix.length)
            if (commands.none { content.startsWith(content) }) return@on

            val command = commands.entries.find { content.startsWith(it.key) }?.value ?: return@on
            val label = command.aliases.find { content.startsWith(it) }!!
            if (command.permission != Permission.MESSAGE_READ && !member.hasPermission(command.permission)) {
                sendPermissionError(it.message)
                return@on
            } else if (command.roles.isNotEmpty()) {
                if (!member.hasPermission(Permission.ADMINISTRATOR) && member.roles.none { command.roles.contains(it.idLong) }) {
                    sendPermissionError(it.message)
                    return@on
                }
            }

            val args = content.substring(label.length).trimStart().split(" ").dropWhile { it == "" }
            if (command.maxArgs > 0 && command.maxArgs < args.size) {
                sendArgumentError(it.message, command)
                return@on
            } else if (command.minArgs > 0 && command.minArgs > args.size) {
                sendArgumentError(it.message, command)
                return@on
            } else if (command.mentionedMembers > 0 && command.mentionedMembers != it.message.mentionedMembers.size) {
                sendArgumentError(it.message, command)
                return@on
            } else if (command.mentionedRoles > 0 && command.mentionedRoles != it.message.mentionedRoles.size) {
                sendArgumentError(it.message, command)
                return@on
            } else if (command.mentionedChannels > 0 && command.mentionedChannels != it.message.mentionedChannels.size) {
                sendArgumentError(it.message, command)
                return@on
            }

            if (command.autoDelete) it.message.delete().queue()

            async.submit { command.handle(args.toTypedArray(), it.message, it.message.textChannel) }
        }
    }

    private fun sendArgumentError(message: Message, command: Command) {
        val embed = embed {
            title {
                text = "Invalid arguments"
            }
            description = "You didn't provide the correct arguments, please try again. Correct usage: `${command.usage}`"
        }

        sendError(message, embed)
    }

    private fun sendPermissionError(message: Message) {
        val embed = embed {
            title {
                text = "Invalid permissions"
                description = "Sorry, but you don't have permission to run that command."
            }
        }

        sendError(message, embed)
    }

    private fun sendError(message: Message, embed: MessageEmbed) {
        message.channel.sendMessage(embed).queue {
            message.textChannel.deleteMessages(mutableListOf(it, message)).queueAfter(8, TimeUnit.SECONDS)
        }
    }

}