package com.zp4rker.disbot.extenstions

import com.zp4rker.disbot.HIDDEN_EMBED_COLOUR
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import org.json.JSONObject
import java.awt.Color
import java.time.temporal.TemporalAccessor

/**
 * @author zp4rker
 */

class KEmbedBuilder(val builder: EmbedBuilder = EmbedBuilder()) {
    data class EmbedField(
            var name: String = EmbedBuilder.ZERO_WIDTH_SPACE,
            var value: String = EmbedBuilder.ZERO_WIDTH_SPACE,
            var inline: Boolean = true
    )

    data class EmbedAuthor(
            var name: String? = null,
            var url: String? = null,
            var iconUrl: String? = null
    )

    data class EmbedFooter(
            var text: String? = null,
            var iconUrl: String? = null
    )

    data class EmbedTitle(
            var text: String = "Title",
            var url: String? = null
    )

    inline fun field(name: String = EmbedBuilder.ZERO_WIDTH_SPACE, value: String = EmbedBuilder.ZERO_WIDTH_SPACE, inline: Boolean = true, fieldBuilder: EmbedField.() -> Unit) {
        val field = EmbedField(name, value, inline).also(fieldBuilder)
        builder.addField(field.name, field.value, field.inline)
    }

    inline fun author(name: String? = null, url: String? = null, iconUrl: String? = null, authorBuilder: EmbedAuthor.() -> Unit) {
        val author = EmbedAuthor(name, url, iconUrl).also(authorBuilder)
        builder.setAuthor(author.name, author.url, author.iconUrl)
    }

    inline fun footer(text: String? = null, iconUrl: String? = null, footerBuilder: EmbedFooter.() -> Unit) {
        val footer = EmbedFooter(text, iconUrl).also(footerBuilder)
        builder.setFooter(footer.text, footer.iconUrl)
    }

    inline fun title(text: String = "Title", url: String? = null, titleBuilder: EmbedTitle.() -> Unit) {
        val title = EmbedTitle(text, url).also(titleBuilder)
        title.url?.let { builder.setTitle(title.text, title.url) } ?: builder.setTitle(title.text)
    }

    var color: String = HIDDEN_EMBED_COLOUR
        set(value) {
            builder.setColor(Color.decode(value))
            field = value
        }

    var description: String? = null
        set(value) {
            builder.setDescription(value)
            field = value
        }

    var image: String? = null
        set(value) {
            builder.setImage(value)
            field = value
        }

    var thumbnail: String? = null
        set(value) {
            builder.setThumbnail(value)
            field = value
        }

    var timestamp: TemporalAccessor? = null
        set(value) {
            builder.setTimestamp(value)
            field = value
        }

    fun build() = builder.build()
}

fun field(fieldBuilder: KEmbedBuilder.EmbedField.() -> Unit) = KEmbedBuilder.EmbedField().also(fieldBuilder)
fun author(authorBuilder: KEmbedBuilder.EmbedAuthor.() -> Unit) = KEmbedBuilder.EmbedAuthor().also(authorBuilder)
fun footer(footerBuilder: KEmbedBuilder.EmbedFooter.() -> Unit) = KEmbedBuilder.EmbedFooter().also(footerBuilder)
fun title(titleBuilder: KEmbedBuilder.EmbedTitle.() -> Unit) = KEmbedBuilder.EmbedTitle().also(titleBuilder)

fun embed(
        author: KEmbedBuilder.EmbedAuthor? = null,
        color: String = HIDDEN_EMBED_COLOUR,
        description: String? = null,
        footer: KEmbedBuilder.EmbedFooter? = null,
        image: String? = null,
        thumbnail: String? = null,
        timestamp: TemporalAccessor? = null,
        title: KEmbedBuilder.EmbedTitle? = null,
        fields: List<KEmbedBuilder.EmbedField> = listOf(),
        builder: KEmbedBuilder.() -> Unit = {}
) = EmbedBuilder().run {
    author?.let { setAuthor(author.name, author.url, author.iconUrl) }
    setColor(Color.decode(color))
    setDescription(description)
    footer?.let { setFooter(footer.text, footer.iconUrl) }
    setImage(image)
    setThumbnail(thumbnail)
    setTimestamp(timestamp)
    title?.let { setTitle(title.text, title.url) }
    fields.forEach { addField(it.name, it.value, it.inline) }
    KEmbedBuilder(this).also(builder)
    build()
}

fun MessageEmbed.toJson() = JSONObject(toData().toString())

fun MessageEmbed.toWebhookContent() = """{ "embeds": [${toJson()}] }"""