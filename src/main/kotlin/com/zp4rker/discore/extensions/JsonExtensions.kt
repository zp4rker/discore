package com.zp4rker.discore.extensions

import org.json.JSONObject

/**
 * @author zp4rker
 */

fun JSONObject.getComplex(key: String): Any? {
    val components = key.split(":")
    var currentObj = this
    var lastKey = components[0]
    for (component in components) {
        lastKey = component
        if (currentObj.has(component) && currentObj[component] is JSONObject) {
            currentObj = currentObj.getJSONObject(component)
        }
    }
    return if (currentObj.has(lastKey)) currentObj[lastKey] else null
}