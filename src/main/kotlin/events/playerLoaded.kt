package me.totxy.events

import me.totxy.health.HealthManagement
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerLoadedEvent

class playerLoaded(private val eventHandler: GlobalEventHandler, private val healthManagement: HealthManagement) {
    fun register() {
        eventHandler.addListener(PlayerLoadedEvent::class.java) { event ->
            val player = event.player
            healthManagement.addHealthBar(player)
            healthManagement.getHealthBar(player)?.let { player.showBossBar(it) }
            healthManagement.getShieldBar(player)?.let { player.showBossBar(it) }
        }
    }
}