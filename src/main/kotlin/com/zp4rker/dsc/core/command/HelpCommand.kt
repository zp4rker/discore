package com.zp4rker.dsc.core.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
class HelpCommand(private val handler: CommandHandler): Command(aliases = arrayOf("help", "?"), description = "Default help command.") {
    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val prefix = handler.prefix
        val commands = handler.commands.filter { !it.hidden }

        EmbedBuilder().setColor(0x353940)
                .setAuthor("Command list", null, message.jda.selfUser.effectiveAvatarUrl)
                .setDescription(commands.joinToString("\n") { "**$prefix${it.usage}** - ${it.description}" })
                .build().also { channel.sendMessage(it).queue() }
    }
}