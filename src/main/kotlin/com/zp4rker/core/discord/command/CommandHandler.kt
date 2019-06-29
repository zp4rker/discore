package com.zp4rker.core.discord.command

import emoji4j.EmojiUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.Executors
import kotlin.concurrent.schedule

class CommandHandler(private val prefix: String = "/") {

    private class ChatCommand(val info: Command, val method: Method)

    private val commands = mutableMapOf<String, ChatCommand>()

    private val async = Executors.newCachedThreadPool()

    fun registerCommands(vararg commands: Command) {
        commands.forEach {
            if (it.aliases.isEmpty()) throw IllegalArgumentException("No aliases found!")
            it.javaClass.declaredMethods.find { c -> c.name == "handle" }?.let { method ->
                this.commands[it.aliases[0]] = ChatCommand(it, method)
            } ?: run {
                throw IllegalArgumentException("No handle method found!")
            }
        }
    }

    @SubscribeEvent
    fun handle(e: GuildMessageReceivedEvent) {
        if (!e.message.contentRaw.startsWith(prefix)) return

        val content = EmojiUtils.shortCodify(e.message.contentRaw.substring(prefix.length))
        if (commands.none { content.startsWith(it.key) }) return

        val command = commands.entries.find { content.startsWith(it.key) }?.value ?: return
        if (command.info.permission != Permission.MESSAGE_READ) {
            val perms = mutableListOf(command.info.permission, Permission.ADMINISTRATOR)
            if (e.member!!.permissions.none { perms.contains(it) }) {
                sendPermissionError(e.channel)
                return
            }
        } else if (command.info.role > 0) {
            if (!e.member!!.isOwner && e.member!!.roles.none { it.idLong == command.info.role }) {
                sendPermissionError(e.channel)
                return
            }
        }

        val args = content.split(" ").drop(1)

        if (command.info.args > 0 && command.info.args != args.size) {
            sendArgumentError(e.channel, command)
            return
        } else if (command.info.minArgs > 0 && command.info.minArgs > args.size) {
            sendArgumentError(e.channel, command)
            return
        } else if (command.info.mentionedMembers > 0 && command.info.mentionedMembers != e.message.mentionedMembers.size) {
            sendArgumentError(e.channel, command)
            return
        } else if (command.info.mentionedRoles > 0 && command.info.mentionedRoles != e.message.mentionedRoles.size) {
            sendArgumentError(e.channel, command)
            return
        } else if (command.info.mentionedChannels > 0 && command.info.mentionedChannels != e.message.mentionedChannels.size) {
            sendArgumentError(e.channel, command)
            return
        }

        if (command.info.autoDelete) e.message.delete().queue()

        async.submit { command.method.invoke(command.info, getParameters(command, e.message)) }
    }

    private fun sendArgumentError(channel: TextChannel, command: ChatCommand) {
        val embed = EmbedBuilder().run {
            setTitle("Invalid arguments")
            setDescription("You didn't provide the correct arguments, please try again. Correct usage: ${command.info.usage}")
            setColor(0x353940)
            build()
        }

        sendError(channel, embed)
    }

    private fun sendPermissionError(channel: TextChannel) {
        val embed = EmbedBuilder().run {
            setTitle("Invalid permissions")
            setDescription("Sorry, but you dont have permission to run that command.")
            setColor(0x353940)
            build()
        }

        sendError(channel, embed)
    }

    private fun sendError(channel: TextChannel, embed: MessageEmbed) {
        channel.sendMessage(embed).queue {
            Timer().schedule(8000) {
                it.delete().queue()
            }
        }
    }

    private fun getParameters(command: ChatCommand, message: Message): Array<Any?> {
        val parameters = emptyArray<Any?>()

        for ((index, parameter) in command.method.parameterTypes.withIndex()) {
            when (parameter) {
                is Message -> parameters[index] = message
                is Member -> parameters[index] = message.member
                is TextChannel -> parameters[index] = message.textChannel
                is Guild -> parameters[index] = message.guild
                is Array<*> -> parameters[index] = message.contentRaw.split(" ").drop(1)
                else -> parameters[index] = null
            }
        }

        return parameters
    }

}