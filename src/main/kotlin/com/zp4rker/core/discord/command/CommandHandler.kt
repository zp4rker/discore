package com.zp4rker.core.discord.command

import emoji4j.EmojiUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import java.awt.Color
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.BiConsumer
import kotlin.collections.HashMap

class CommandHandler(private val prefix: String, packageName: String, var permErr: MessageEmbed) {

    class ChatCommand (val info: Command, val method: Method)

    private val async: ExecutorService

    private val commands = HashMap<String, ChatCommand>()

    private val errFunc: BiConsumer<TextChannel, MessageEmbed>

    init {
        if (prefix.contains(" ")) throw IllegalStateException("No spaces allowed in prefixes!")

        async = Executors.newCachedThreadPool()

        permErr = EmbedBuilder()
                .setTitle("Invalid Permissions")
                .setDescription("You don't have the permissions required to perform this action!")
                .setColor(Color(240, 71, 71)).build()

        errFunc = BiConsumer { t, e ->  t.sendMessage(e).queue {Timer().schedule(object: TimerTask() {
            override fun run() {
                it.delete().queue()
            }
        }, 1000)}}

        registerCommands(packageName)
    }

    @SubscribeEvent
    fun handle(e: GuildMessageReceivedEvent) {
        if (!e.message.contentRaw.startsWith(prefix)) return

        val content = EmojiUtils.shortCodify(e.message.contentRaw.substring(prefix.length))
        if (commands.keys.none { it.startsWith(content) }) return

        val cmd = commands.entries.filter { content.startsWith(it.key) }[0].value

        if (cmd.info.permission != Permission.MESSAGE_READ) {
            val perms = listOf(cmd.info.permission, Permission.ADMINISTRATOR)
            if (e.member!!.permissions.none { perms.contains(it) }) {
                errFunc.accept(e.channel, permErr)
                return
            }
        } else if (cmd.info.role > 0) {
            val m = e.member
            if (!m!!.isOwner && m.roles.none { it.idLong == cmd.info.role }) {
                errFunc.accept(e.channel, permErr)
                return
            }
        }

        val splitContent = content.split(" ").toTypedArray()
        val args = splitContent.drop(1)

        val argErr = EmbedBuilder()
        .setTitle("Invalid Arguments")
        .setDescription("Invalid arguments! Correct usage: `" + cmd.info.usage + "`")
        .setColor(Color(240, 71, 71)).build()

        if (cmd.info.args > 0 && args.size != cmd.info.args) {
            errFunc.accept(e.channel, argErr)
            return
        } else if (cmd.info.minArgs > 0 && args.size < cmd.info.minArgs) {
            errFunc.accept(e.channel, argErr)
            return
        } else if (cmd.info.mentionedMembers > 0 && e.message.mentionedMembers.size != cmd.info.mentionedMembers) {
            errFunc.accept(e.channel, argErr)
            return
        } else if (cmd.info.mentionedRoles > 0 && e.message.mentionedRoles.size != cmd.info.mentionedRoles) {
            errFunc.accept(e.channel, argErr)
            return
        } else if (cmd.info.mentionedChannels > 0 && e.message.mentionedChannels.size != cmd.info.mentionedChannels) {
            errFunc.accept(e.channel, argErr)
            return
        }

        if (cmd.info.autoDelete) e.message.delete().queue()

        async.submit { execute(cmd, getParams(splitContent, cmd, e.message)) }
    }

    private fun registerCommands(packageName: String) {
        val reflections = Reflections(packageName, SubTypesScanner(false))
        for (className in reflections.allTypes) {
            registerCommand(Class.forName(className))
        }
    }

    private fun registerCommand(c: Class<*>) {
        for (method in c.methods) {
            val info: Command? = method.getAnnotation(Command::class.java)

            if (info?.aliases == null) {
                throw IllegalArgumentException("No aliases have been defined for ${c.name}!")
            } else if (info.aliases.any { it.contains(" ") }) {
                throw IllegalArgumentException("Spaces are not allowed in aliases!")
            }

            val command = ChatCommand(info, method)
            for (alias in info.aliases) commands[alias] = command
        }
    }

    private fun getParams(splitMsg: Array<String>, cmd: ChatCommand, msg: Message): Array<Any?> {
        val paramTypes = cmd.method.parameterTypes
        val params = arrayOf<Any?>()

        for ((index, param) in paramTypes.withIndex()) {
            when (param) {
                is Message -> params[index] = msg
                is Member -> params[index] = msg.member!!
                is TextChannel -> params[index] = msg.textChannel
                is Guild -> params[index] = msg.guild
                is Array<*> -> params[index] = splitMsg.drop(1)
                else -> params[index] = null
            }
        }

        return params
    }

    private fun execute(cmd: ChatCommand, params: Array<Any?>) {
        cmd.method.invoke(cmd.method.declaringClass.newInstance(), params)
    }

}