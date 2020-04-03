package com.zp4rker.discore.api

import com.zp4rker.discore.command.CommandHandler
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.security.auth.login.LoginException
import kotlin.system.exitProcess

interface Bot {

    private val info: BotInfo
        get() = BotInfo()
    private val config: Configuration
        get() = Configuration()
    private val logger: Logger
        get() = LoggerFactory.getLogger(config.name)

    companion object {
        lateinit var jda: JDA
        lateinit var handler: CommandHandler
    }

    fun onSetup() {}
    fun onLoad() {}

    fun run() {
        setup()
        if (!login()) exitProcess(0)
        load()
    }

    private fun load() {
        jda.addEventListener(handler)
        logger.info("Registered command handler")

        // load modules

        // call onload
        onLoad()
    }

    private fun setup() {
        logger.info("Starting ${info.name} version ${info.version}")
        logger.info("Made by ${info.author}")
        logger.info("Implementing JDA version ${info.jdaVersion}")

        handler = CommandHandler(config.prefix)

        // call onsetup
        onSetup()
    }

    private fun login(): Boolean {
        if (config.token == "notset") return false

        try {
            jda = JDABuilder.createDefault(config.token).build()
        } catch (e: LoginException) {
            return false
        }

        return true
    }
}