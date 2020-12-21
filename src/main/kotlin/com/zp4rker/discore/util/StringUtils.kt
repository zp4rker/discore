package com.zp4rker.discore.util

import com.vdurmont.emoji.EmojiParser

/**
 * @author zp4rker
 */

fun String.unicodify(): String = EmojiParser.parseToUnicode(this)

fun String.emotify(): String = EmojiParser.parseToAliases(this)