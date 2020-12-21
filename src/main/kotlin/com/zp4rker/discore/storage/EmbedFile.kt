package com.zp4rker.discore.storage

import com.zp4rker.discore.extenstions.embed
import com.zp4rker.discore.util.unicodify
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

/**
 * @author zp4rker
 */
@Serializable
data class EmbedFile(
    val author: Author? = null,
    val color: String = "#2f3136",
    val description: String? = null,
    val fields: List<Field>? = null,
    val footer: Footer? = null,
    val image: String? = null,
    val thumbnail: String? = null,
    val title: Title? = null,
    @Serializable(with = InstantSerializer::class)
    val timestamp: Instant? = null
) {

    @Serializable
    data class Author(
        val name: String? = null,
        val url: String? = null,
        val iconUrl: String? = null
    )

    @Serializable
    data class Field(
        val title: String = "\u200e",
        val text: String = "\u200e",
        val inline: Boolean = true
    )

    @Serializable
    data class Footer(
        val text: String? = null,
        val iconUrl: String? = null
    )

    @Serializable
    data class Title(
        val text: String = "Title",
        val url: String? = null
    )

    object InstantSerializer : KSerializer<Instant> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): Instant = Instant.parse(decoder.decodeString())

        override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeString(value.toString())
    }

    fun construct() = embed {
        author?.let {
            author {
                name = it.name?.unicodify()
                url = it.url
                iconUrl = it.iconUrl
            }
        }

        color = this@EmbedFile.color

        description = this@EmbedFile.description?.unicodify()

        fields?.let {
            for (field in it) {
                field {
                    title = field.title.unicodify()
                    text = field.text.unicodify()
                    inline = field.inline
                }
            }
        }

        footer?.let {
            footer {
                text = it.text?.unicodify()
                iconUrl = it.iconUrl
            }
        }

        image = this@EmbedFile.image

        thumbnail = this@EmbedFile.thumbnail

        title?.let {
            title {
                text = it.text.unicodify()
                url = it.url
            }
        }

        timestamp = this@EmbedFile.timestamp
    }

}
