package com.zp4rker.disbot.command

import com.zp4rker.disbot.Bot
import com.zp4rker.disbot.extenstions.on
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
        bot.on<MessageReceivedEvent> {
            if (!isFromGuild) return@on // no need to handle DMs for now

            val member = member ?: return@on

            if (!message.contentRaw.startsWith(prefix)) return@on

            val content = message.contentRaw.substring(prefix.length)
            if (commands.none { content.startsWith(content) }) return@on

            val command = commands.entries.find { content.startsWith(it.key) }?.value ?: return@on
            val label = command.aliases.find { content.startsWith(it) }!!
            if (command.permission != Permission.MESSAGE_READ && !member.hasPermission(command.permission)) {
                sendPermissionError(message)
                return@on
            } else if (command.roles.isNotEmpty()) {
                if (!member.hasPermission(Permission.ADMINISTRATOR) && member.roles.none { command.roles.contains(it.idLong) }) {
                    sendPermissionError(message)
                    return@on
                }
            }

            val args = content.substring(label.length).trimStart().split(" ").dropWhile { it == "" }
            if (command.maxArgs > 0 && command.maxArgs < args.size) {
                sendArgumentError(message, command)
                return@on
            } else if (command.minArgs > 0 && command.minArgs > args.size) {
                sendArgumentError(message, command)
                return@on
            } else if (command.mentionedMembers > 0 && command.mentionedMembers != message.mentionedMembers.size) {
                sendArgumentError(message, command)
                return@on
            } else if (command.mentionedRoles > 0 && command.mentionedRoles != message.mentionedRoles.size) {
                sendArgumentError(message, command)
                return@on
            } else if (command.mentionedChannels > 0 && command.mentionedChannels != message.mentionedChannels.size) {
                sendArgumentError(message, command)
                return@on
            }

            if (command.autoDelete) message.delete().queue()

            async.submit { command.handle(args.toTypedArray(), message, message.textChannel) }
        }
    }

    private fun sendArgumentError(message: Message, command: Command) {
        val embed = EmbedBuilder().run {
            setTitle("Invalid arguments")
            setDescription("You didn't provide the correct arguments, please try again. Correct usage: `${command.usage}`")
            setColor(0x353940)
            build()
        }

        sendError(message, embed)
    }

    private fun sendPermissionError(message: Message) {
        val embed = EmbedBuilder().run {
            setTitle("Invalid permissions")
            setDescription("Sorry, but you dont have permission to run that command.")
            setColor(0x353940)
            build()
        }

        sendError(message, embed)
    }

    private fun sendError(message: Message, embed: MessageEmbed) {
        message.channel.sendMessage(embed).queue {
            message.textChannel.deleteMessages(mutableListOf(it, message)).queueAfter(8, TimeUnit.SECONDS)
        }
    }

}