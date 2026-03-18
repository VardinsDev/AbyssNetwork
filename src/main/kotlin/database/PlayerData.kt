package me.totxy.database

data class PlayerData(
    val uuid: String,
    val username: String,
    var kills: Int = 0,
    var deaths: Int = 0,
    var team: Int = -1,
    var rank: String = "default",
    var isOpped: Boolean = false
)