package com.zp4rker.disbot.module

import com.zp4rker.disbot.Main.Companion.logger
import com.zp4rker.disbot.config.TomlFile
import net.dv8tion.jda.api.requests.GatewayIntent
import java.io.File
import java.net.URLClassLoader

/**
 * @author zp4rker
 */
class ModuleLoader {

    private val modules = arrayListOf<Module>()

    fun loadModules() {
        val dir = File("modules")

        if (!dir.exists()) {
            dir.mkdirs()
            return
        }

        dir.listFiles()?.filter { it.extension.equals("jar", true) }?.forEach {
            val cl = URLClassLoader(arrayOf(it.toURI().toURL()))

            val botInfo = cl.getResourceAsStream("/mod.toml")?.run { TomlFile(this) }
                    ?: return logger.error("Unable to find or read mod.tml file, ignoring module!")

            try {
                val mainClass = cl.loadClass(botInfo.get<String>("main"))
                val mod = mainClass.getDeclaredConstructor(TomlFile::class.java).newInstance(botInfo) as Module

                mod.load()
                modules.add(mod)
                mod.enable()
            } catch (cnfe: ClassNotFoundException) {
                logger.error("Unable to load main class for ${botInfo.get<String>("name")} v${botInfo.get<String>("version")}! Skipping...")
            }
        }
    }

    fun isEnabled(moduleName: String):Boolean {
        return modules.find { it.name == moduleName }?.isEnabled ?: false
    }

}