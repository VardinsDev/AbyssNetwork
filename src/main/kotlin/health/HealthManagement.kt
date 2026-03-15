package me.totxy.health

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.tag.Tag
import java.util.*
import kotlin.math.roundToInt

class healthManagement {
    val healthBars: MutableMap<UUID?, BossBar> = HashMap<UUID?, BossBar>()
    val shieldBars: MutableMap<UUID?, BossBar> = HashMap<UUID?, BossBar>()

    fun addHealthBar(player: Player) {
        val health = Tag.Double("health")
        val shield = Tag.Double("shield")
        player.setTag<Double?>(health, 100.0)
        player.setTag<Double?>(shield, 100.0)
        // Create a boss bar using Adventure's static factory method
        val healthBar = BossBar.bossBar(
            Component.text("Health: " + player.getTag<Double?>(health)?.roundToInt()?.toInt()),
            (player.getTag<Double?>(health)?.div(100.0f))?.toFloat() ?:,  // float division
            BossBar.Color.RED,
            BossBar.Overlay.PROGRESS
        )
        val shieldBar = BossBar.bossBar(
            Component.text("Shield: " + player.getTag<Double?>(shield)?.roundToInt()?.toInt()),
            (player.getTag<Double?>(shield)?.div(100.0f)).toFloat(),
            BossBar.Color.BLUE,
            BossBar.Overlay.PROGRESS
        )
        healthBars[player.uuid] = healthBar
        shieldBars[player.uuid] = shieldBar
    }
    fun getHealth(player: Player) {

    }
}