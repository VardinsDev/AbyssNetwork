package me.totxy.database

object PlayerRepository {
    private val db get() = DatabaseManager.getConnection()

    fun loadPlayer(uuid: String, username: String): PlayerData {
        val stmt = db.prepareStatement("SELECT * FROM players WHERE uuid = ?")
        stmt.setString(1, uuid)
        val rs = stmt.executeQuery()

        return if (rs.next()) {
            PlayerData(
                uuid = rs.getString("uuid"),
                username = rs.getString("username"),
                kills = rs.getInt("kills"),
                deaths = rs.getInt("deaths"),
                team = rs.getInt("team"),
                rank = rs.getString("player_rank"),
                isOpped = rs.getBoolean("is_opped")
            )
        } else {
            val insert = db.prepareStatement(
                "INSERT INTO players (uuid, username) VALUES (?, ?)"
            )
            insert.setString(1, uuid)
            insert.setString(2, username)
            insert.executeUpdate()
            PlayerData(uuid = uuid, username = username)
        }
    }

    fun savePlayer(data: PlayerData) {
        val stmt = db.prepareStatement("""
            UPDATE players SET
                username = ?,
                kills = ?,
                deaths = ?,
                team = ?,
                player_rank = ?,
                is_opped = ?
            WHERE uuid = ?
        """)
        stmt.setString(1, data.username)
        stmt.setInt(2, data.kills)
        stmt.setInt(3, data.deaths)
        stmt.setInt(4, data.team)
        stmt.setString(5, data.rank)
        stmt.setBoolean(6, data.isOpped)
        stmt.setString(7, data.uuid)
        stmt.executeUpdate()
    }

    fun getPlayer(uuid: String): PlayerData? {
        val stmt = db.prepareStatement("SELECT * FROM players WHERE uuid = ?")
        stmt.setString(1, uuid)
        val rs = stmt.executeQuery()
        if (!rs.next()) return null
        return PlayerData(
            uuid = rs.getString("uuid"),
            username = rs.getString("username"),
            kills = rs.getInt("kills"),
            deaths = rs.getInt("deaths"),
            team = rs.getInt("team"),
            rank = rs.getString("player_rank"),
            isOpped = rs.getBoolean("is_opped")
        )
    }

    fun isOpped(uuid: String): Boolean {
        val stmt = db.prepareStatement("SELECT is_opped FROM players WHERE uuid = ?")
        stmt.setString(1, uuid)
        val rs = stmt.executeQuery()
        return rs.next() && rs.getBoolean("is_opped")
    }
}