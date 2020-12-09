package com.zp4rker.discore.storage

import org.json.JSONObject
import java.io.File

/**
 * @author zp4rker
 */
class JsonFile(content: String, private val file: File? = null) : JSONObject(content) {

    fun save() {
        file?.writeText(this.toString(2))
    }

    companion object {
        fun load(filename: String, sameDir: Boolean = false): JsonFile {
            val file = getFile(filename, sameDir)
            return load(file)
        }

        fun loadOrDefault(filename: String, sameDir: Boolean = false, default: String = filename): JsonFile {
            val file = getFile(filename, sameDir)

            if (file.exists()) return load(file)

            file.createNewFile()
            file.writeText(readDefaultFromResource(default))
            return load(file)
        }

        fun load(file: File): JsonFile {
            val content = file.readText()

            return if (content.isEmpty()) JsonFile("{}", file) else JsonFile(content, file)
        }

        private fun getFile(filename: String, sameDir: Boolean = false): File {
            val dir = File(JsonFile::class.java.protectionDomain.codeSource.location.toURI()).parentFile
            return if (sameDir) File(dir, filename) else File(filename)
        }

        private fun readDefaultFromResource(filename: String) = JsonFile::class.java.getResource("/$filename").readText()
    }

}