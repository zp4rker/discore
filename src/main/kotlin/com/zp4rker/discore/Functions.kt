package com.zp4rker.discore

import com.zp4rker.discore.command.Command
import org.reflections.Reflections
import java.net.JarURLConnection
import java.net.URL
import java.util.jar.Attributes

/**
 * @author zp4rker
 */
fun getManifest(url: URL): Attributes = with(URL("jar:${url.toExternalForm()}!/").openConnection() as JarURLConnection) { mainAttributes }

fun findCommands(): List<Command> {
    val pkg = Class.forName(MANIFEST.getValue("Main-Class")).`package`.name
    val classList = Reflections(pkg).getSubTypesOf(Command::class.java)
    val instanceList = mutableListOf<Command>()
    classList.forEach {
        runCatching { it.getDeclaredField("INSTANCE") }.getOrNull()?.let { f ->
            instanceList.add(f.get(null) as Command)
        } ?: instanceList.add(it.newInstance())
    }
    return instanceList
}