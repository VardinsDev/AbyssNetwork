package me.totxy.events

import me.totxy.health.HealthManagement
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerTickEvent

class tickEvent(private val minecraftServer: MinecraftServer, private val eventHandler: GlobalEventHandler, private val healthManagement: HealthManagement) {
    fun register() {
        eventHandler.addListener(PlayerTickEvent::class.java) { event ->
            MinecraftServer.getConnectionManager().onlinePlayers.forEach { player ->
                healthManagement.updateBars(player)
            }
        }
    }
}
