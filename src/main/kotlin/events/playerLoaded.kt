package me.totxy.events

import me.totxy.health.HealthManagement
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerLoadedEvent
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect
import net.minestom.server.tag.Tag

class playerLoaded(private val eventHandler: GlobalEventHandler, private val healthManagement: HealthManagement) {
    fun register() {
        eventHandler.addListener(PlayerLoadedEvent::class.java) { event ->
            val player = event.player
            val teamManager = MinecraftServer.getTeamManager()
            healthManagement.addHealthBar(player)
            healthManagement.getHealthBar(player)?.let { player.showBossBar(it) }
            healthManagement.getShieldBar(player)?.let { player.showBossBar(it) }
            val teamTag: Tag<Boolean?>? = Tag.Boolean("teamTag")
            if (player.getTag(teamTag) == true) {
                player.addEffect(Potion(PotionEffect.GLOWING, 1, Integer.MAX_VALUE, Potion.INFINITE_DURATION))
                var playerTeam = teamManager.getTeam("glow_blue")
                playerTeam?.addMember(player.username)
                player.isGlowing = true
            } else if (player.getTag(teamTag) == false) {
                player.addEffect(Potion(PotionEffect.GLOWING, 1, Integer.MAX_VALUE, Potion.INFINITE_DURATION))
                var playerTeam = teamManager.getTeam("glow_red")
                playerTeam?.addMember(player.username)
                player.isGlowing = true
            }
            val serverBar = BossBar.bossBar(
                Component.text("You are on DEV SERVER!"),
                1.0f,
                BossBar.Color.GREEN,
                BossBar.Overlay.PROGRESS
            )
            player.showBossBar(serverBar)
            MinecraftServer.getConnectionManager().onlinePlayers.forEach{ person ->
                person.sendMessage(Component.text(player.username + " has joined the server!").color(NamedTextColor.YELLOW))
            }
        }
    }
}