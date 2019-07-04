package com.zp4rker.core.discord.command

import emoji4j.EmojiUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CommandHandler(private val prefix: String = "/") {

    private val commands = mutableMapOf<String, Command>()

    private val async = Executors.newCachedThreadPool()

    fun registerCommands(vararg commands: Command) {
        commands.forEach {
            if (it.aliases.isEmpty()) throw IllegalArgumentException("No aliases found!")
            else this.commands[it.aliases[0]] = it
        }
    }

    @SubscribeEvent
    fun handle(e: GuildMessageReceivedEvent) {
        if (!e.message.contentRaw.startsWith(prefix)) return

        val content = EmojiUtils.shortCodify(e.message.contentRaw.substring(prefix.length))
        if (commands.none { content.startsWith(it.key) }) return

        val command = commands.entries.find { content.startsWith(it.key) }?.value ?: return
        if (!e.member!!.hasPermission(command.permission)) {
            sendPermissionError(e.message)
            return
        } else if (command.role > 0) {
            if (!e.member!!.hasPermission(Permission.ADMINISTRATOR) && e.member!!.roles.none { it.idLong == command.role }) {
                sendPermissionError(e.message)
                return
            }
        }

        val args = content.split(" ").drop(1)
        if (command.args > 0 && command.args != args.size) {
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

        async.submit { execute(command, e.message) }
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

    private fun execute(command: Command, message: Message) {
        val content = EmojiUtils.shortCodify(message.contentRaw)
        command.handle(message, message.textChannel, message.guild, content.split(" ").drop(1))
    }

}