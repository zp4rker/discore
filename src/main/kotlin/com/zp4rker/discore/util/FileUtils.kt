package com.zp4rker.discore.util

import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * @author zp4rker
 */

fun datedArchive(file: File, deleteOriginal: Boolean = true) {
    if (!file.parentFile.exists()) file.parentFile.mkdir()
    if (!file.exists()) return

    val bytes = file.readBytes()

    val outFile = file.name.let {
        val date = Instant.ofEpochMilli(file.lastModified()).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE)
        var name = "$date-${it}.zip"

        if (File(file.parentFile, name).exists()) {
            var i = 2
            name = "$date-$it.$i.zip"
            while (File(file.parentFile, name).exists()) {
                name = "$date-$it.${++i}.zip"
            }
        }

        File(file.parentFile, name)
    }

    ZipOutputStream(outFile.outputStream()).use {
        it.putNextEntry(ZipEntry(file.name))
        it.write(bytes)
    }

    if (deleteOriginal) file.delete()
}