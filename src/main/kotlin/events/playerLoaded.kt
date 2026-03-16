package me.totxy.events

import me.totxy.health.HealthManagement
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerLoadedEvent

class playerLoaded(private val eventHandler: GlobalEventHandler, private val healthManagement: HealthManagement) {
    fun register() {
        eventHandler.addListener(PlayerLoadedEvent::class.java) { event ->
            val player = event.player
            healthManagement.addHealthBar(player)
            healthManagement.getHealthBar(player)?.let { player.showBossBar(it) }
            healthManagement.getShieldBar(player)?.let { player.showBossBar(it) }
            val serverBar = BossBar.bossBar(
                Component.text("You are on BUILD SERVER!"),
                1.0f,
                BossBar.Color.PURPLE,
                BossBar.Overlay.PROGRESS
            )
            player.showBossBar(serverBar)
            MinecraftServer.getConnectionManager().onlinePlayers.forEach{ person ->
                person.sendMessage(Component.text(player.username + " has joined the server!").color(NamedTextColor.YELLOW))
            }
        }
    }
}