package com.zp4rker.discore.util

import com.vdurmont.emoji.EmojiParser

/**
 * @author zp4rker
 */

fun String.unicodify(): String = EmojiParser.parseToUnicode(this)

fun String.emotify(): String = EmojiParser.parseToAliases(this)

fun linedName(original: String): String {
    fun components(original: String): List<String> {
        val list = mutableListOf<String>()
        for ((i, c) in original.toCharArray().withIndex()) {
            if ((c.isUpperCase() || c.isWhitespace())) {
                if (i == 0 || (original.elementAt(i - 1).isUpperCase() && original.elementAtOrElse(i + 1) { 'A' }.isUpperCase())) continue
                list.add(original.substring(0, i))
                list.addAll(components(original.substring(if (c.isWhitespace()) i + 1 else i, original.length)))
                break
            }
        }
        if (list.isEmpty()) list.add(original)
        return list
    }

    return components(original).joinToString("\n")
}