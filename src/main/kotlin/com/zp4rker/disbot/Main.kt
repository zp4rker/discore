package com.zp4rker.disbot

import com.zp4rker.disbot.config.Configuration
import com.zp4rker.disbot.console.Console
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

val logger: Logger = LoggerFactory.getLogger("Disbot")

class Main {

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val inst = Main()
            logger.info("Starting Disbot v${inst.javaClass.`package`.implementationVersion} by zp4rker...")

            logger.info("Setting up...")

            val cfg = Configuration("bot.cfg")
            logger.info("${cfg.toml.size()}")

            logger.info("Setup complete!")
        }
    }

    val whitelist = File("whitelist.txt")
    val bannedServers = File("banned_servers.txt")
    val bannedUsers = File("banned_users.txt")

    init {
        Console.start()

        arrayOf(whitelist, bannedServers, bannedUsers).filter { !it.exists() }.forEach(File::createNewFile)
    }

}