package com.zp4rker.discore

import com.zp4rker.discore.command.Command
import org.reflections.Reflections

/**
 * @author zp4rker
 */
fun getMainClass(): Class<*> = Class.forName(Thread.getAllStackTraces().entries.first { it.key.name == "main" }.value.last().className)

fun findCommands(): List<Command> {
    val pkg = getMainClass().`package`.name
    val classList = Reflections(pkg).getSubTypesOf(Command::class.java)
    val instanceList = mutableListOf<Command>()
    classList.forEach {
        runCatching { it.getDeclaredField("INSTANCE") }.getOrNull()?.let { f ->
            instanceList.add(f.get(null) as Command)
        } ?: instanceList.add(it.newInstance())
    }
    return instanceList
}