package me.totxy.events

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerDisconnectEvent
import java.util.UUID

class playerDisconnect {
    fun register(eventHandler: GlobalEventHandler) {
        eventHandler.addListener(PlayerDisconnectEvent::class.java) {event ->
            MinecraftServer.getConnectionManager().onlinePlayers.forEach { person ->
                person.sendMessage(Component.text(event.player.username + " has left the server!").color(NamedTextColor.YELLOW))
            }
        }
    }
}