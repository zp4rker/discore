package com.zp4rker.core.discord.database

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

class Database(val host: String, val username: String? = null, val password: String? = null)  {

    private var con: Connection? = null
    private val statements = mutableListOf<PreparedStatement?>()

    fun openConnection() {
        con ?: username?.let { con = DriverManager.getConnection("jdbc:mysql://$host", username, password) } ?: run {
            con = DriverManager.getConnection("jdbc:sqlite:$host")
        }
    }

    fun closeConnection() {
        statements.forEach { it?.close() }
        statements.clear()
        con?.let {
            it.close()
            con = null
        }
    }

    fun query(sql: String, vararg args: String): ResultSet? {
        var result: ResultSet? = null
        con?.prepareStatement(sql)?.let {
            for ((i, arg) in args.withIndex()) it.setObject(i, arg)
            result = it.executeQuery()
            statements.add(it)
        }
        return result
    }

    fun update(sql: String, vararg args: String): Int {
        var code = -1
        con?.prepareStatement(sql)?.let {
            for ((i, arg) in args.withIndex()) it.setObject(i, arg)
            code = it.executeUpdate()
            statements.add(it)
        }
        return code
    }

}