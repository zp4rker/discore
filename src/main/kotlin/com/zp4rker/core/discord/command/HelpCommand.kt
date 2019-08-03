package com.zp4rker.core.discord.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

class HelpCommand(private val handler: CommandHandler) : Command(aliases = arrayOf("help"), description = "The main help command.") {
    override fun handle(message: Message, channel: TextChannel, guild: Guild, args: List<String>) {
        val prefix = handler.prefix
        val commands = handler.commands.values.filter { !it.hidden }

        EmbedBuilder().setColor(0x353940).apply {
            setAuthor("Command list", null, guild.jda.selfUser.effectiveAvatarUrl)
            setDescription(commands.joinToString("\n") { "**$prefix${it.usage}** ${it.description}" })
        }.build().apply { channel.sendMessage(this).queue() }
    }
}