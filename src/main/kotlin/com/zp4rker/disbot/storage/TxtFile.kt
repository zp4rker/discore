package com.zp4rker.disbot.storage

import java.io.File

/**
 * @author zp4rker
 *
 * Class for handling TXT (*.txt) files. Used for storing a list of strings.
 */
class TxtFile(filename: String) {

    private val file: File = File(filename)
    val values: MutableList<String>

    init {
        if (!file.exists()) file.createNewFile()

        values = file.readLines().toMutableList()
    }

    fun save() {
        file.writeText(values.joinToString("\n"))
    }

    fun reload() {
        values.clear()
        values.addAll(file.readLines())
    }

}