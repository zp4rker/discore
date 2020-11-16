package com.zp4rker.disbot.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */
class CommandHandler(val prefix: String, helpCommand: Boolean = true, val commands: MutableMap<String, Command> = mutableMapOf()) : ListenerAdapter() {

    init {
        if (helpCommand) registerCommand(HelpCommand(this))
    }

    private val async = Executors.newCachedThreadPool()

    fun registerCommands(vararg commands: Command) {
        commands.forEach { registerCommand(it) }
    }

    private fun registerCommand(command: Command) {
        commands[command.aliases[0]] = command
    }

    override fun onMessageReceived(e: MessageReceivedEvent) {
        if (!e.isFromGuild) return // no need to handle DMs for now

        val member = e.member ?: return

        if (!e.message.contentRaw.startsWith(prefix)) return

        val content = e.message.contentRaw.substring(prefix.length)
        if (commands.none { content.startsWith(content) }) return

        val command = commands.entries.find { content.startsWith(it.key) }?.value ?: return
        val label = command.aliases.find { content.startsWith(it) }!!
        if (command.permission != Permission.MESSAGE_READ && !member.hasPermission(command.permission)) {
            sendPermissionError(e.message)
            return
        } else if (command.roles.isNotEmpty()) {
            if (!member.hasPermission(Permission.ADMINISTRATOR) && member.roles.none { command.roles.contains(it.idLong) }) {
                sendPermissionError(e.message)
                return
            }
        }

        val args = content.substring(label.length).trimStart().split(" ").dropWhile { it == "" }
        if (command.maxArgs > 0 && command.maxArgs < args.size) {
            sendArgumentError(e.message, command)
            return
        } else if (command.minArgs > 0 && command.minArgs > args.size) {
            sendArgumentError(e.message, command)
            return
        } else if (command.mentionedMembers > 0 && command.mentionedMembers != e.message.mentionedMembers.size) {
            sendArgumentError(e.message, command)
            return
        } else if (command.mentionedRoles > 0 && command.mentionedRoles != e.message.mentionedRoles.size) {
            sendArgumentError(e.message, command)
            return
        } else if (command.mentionedChannels > 0 && command.mentionedChannels != e.message.mentionedChannels.size) {
            sendArgumentError(e.message, command)
            return
        }

        if (command.autoDelete) e.message.delete().queue()

        async.submit { command.handle(args.toTypedArray(), e.message, e.message.textChannel) }
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