package com.zp4rker.discore.util

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.encodeToString

/**
 * @author zp4rker
 */

inline fun <reified T> Yaml.encode(value: T): String {
    return this.encodeToString(value).replace(Regex(".*: null[\\n]?"), "")
}