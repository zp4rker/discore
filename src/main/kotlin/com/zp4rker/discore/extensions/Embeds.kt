package com.zp4rker.discore.extensions

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
        var title: String = EmbedBuilder.ZERO_WIDTH_SPACE,
        var text: String = EmbedBuilder.ZERO_WIDTH_SPACE,
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

    inline fun field(title: String = EmbedBuilder.ZERO_WIDTH_SPACE, text: String = EmbedBuilder.ZERO_WIDTH_SPACE, inline: Boolean = true, fieldBuilder: EmbedField.() -> Unit) {
        val field = EmbedField(title, text, inline).also(fieldBuilder)
        builder.addField(field.title, field.text, field.inline)
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

    var color: String = EmbedColor.HIDDEN
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

@Suppress("unused")
object EmbedColor {
    const val HIDDEN = "#2f3136"

    const val GREENBLUE = "#1abc9c"
    const val DARK_GREENBLUE = "#16a085"
    const val GREEN = "#2ecc71"
    const val DARK_GREEN = "#27ae60"
    const val BLUE = "#3498db"
    const val DARK_BLUE = "#2980b9"
    const val PURPLE = "#9b59b6"
    const val DARK_PURPLE = "#8e44ad"
    const val NAVY = "#34495e"
    const val DARK_NAVY = "#2c3e50"
    const val YELLOW = "#f1c40f"
    const val LIGHT_ORANGE = "#f39c12"
    const val ORANGE = "#e67e22"
    const val DARK_ORANGE = "#d35400"
    const val RED = "#e74c3c"
    const val DARK_RED = "#c0392b"
    const val LIGHT_GREY = "#ecf0f1"
    const val GREY = "#bdc3c7"
    const val DARK_GREY = "#95a5a6"
    const val DARKER_GREY = "#7f8c8d"

    const val WHITE = "#ffffff"
    const val BLACK = "#000000"
}

fun field(fieldBuilder: KEmbedBuilder.EmbedField.() -> Unit) = KEmbedBuilder.EmbedField().also(fieldBuilder)
fun author(authorBuilder: KEmbedBuilder.EmbedAuthor.() -> Unit) = KEmbedBuilder.EmbedAuthor().also(authorBuilder)
fun footer(footerBuilder: KEmbedBuilder.EmbedFooter.() -> Unit) = KEmbedBuilder.EmbedFooter().also(footerBuilder)
fun title(titleBuilder: KEmbedBuilder.EmbedTitle.() -> Unit) = KEmbedBuilder.EmbedTitle().also(titleBuilder)

fun embed(
    author: KEmbedBuilder.EmbedAuthor? = null,
    color: String = EmbedColor.HIDDEN,
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
    fields.forEach { addField(it.title, it.text, it.inline) }
    KEmbedBuilder(this).also(builder)
    build()
}

fun MessageEmbed.toJson() = JSONObject(toData().toString())

fun MessageEmbed.toWebhookContent() = """{ "embeds": [${toJson()}] }"""