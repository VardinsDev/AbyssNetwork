package me.totxy.database

import me.totxy.AbyssLogger
import java.sql.Connection
import java.sql.DriverManager

object DatabaseManager {
    private lateinit var connection: Connection

    fun connect(host: String, port: Int, database: String, user: String, password: String) {
        connection = DriverManager.getConnection(
            "jdbc:mysql://$host:$port/$database?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true",
            user,
            password
        )
        createTables()
        AbyssLogger.success("Database connected.")
    }

    fun disconnect() {
        if (!connection.isClosed) {
            connection.close()
            AbyssLogger.info("Database disconnected.")
        }
    }

    private fun createTables() {
        connection.createStatement().executeUpdate("""
            CREATE TABLE IF NOT EXISTS players (
                uuid VARCHAR(36) PRIMARY KEY,
                username VARCHAR(16) NOT NULL,
                kills INT DEFAULT 0,
                deaths INT DEFAULT 0,
                team INT DEFAULT -1,
                player_rank VARCHAR(32) DEFAULT 'default',
                is_opped BOOLEAN DEFAULT FALSE
            )
        """)
    }

    fun getConnection(): Connection = connection
}