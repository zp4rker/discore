package com.zp4rker.disbot.module

import java.io.File
import java.net.URL
import java.net.URLClassLoader

/**
 * @author zp4rker
 */
class ModuleLoader {

    fun loadModules() {
        val dir = File("modules")

        if (!dir.exists()) {
            dir.mkdirs()
            return
        }

        val cl = ClassLoader.getSystemClassLoader() as URLClassLoader
        val method = cl::class.java.getDeclaredMethod("addURL", URL::class.java)
        method.isAccessible = true

        dir.listFiles()?.filter { it.extension.equals("jar", true) }?.forEach {
            method.invoke(cl, it.toURI().toURL())

            // call main class methods
        }
    }

}