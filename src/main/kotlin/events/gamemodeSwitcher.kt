package me.totxy.events

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerGameModeRequestEvent

class gamemodeSwitcher {
    fun register(eventHandler: GlobalEventHandler) {
        eventHandler.addListener(PlayerGameModeRequestEvent::class.java) {event ->
            if (event.player.permissionLevel >= 3) {
                event.player.gameMode = event.requestedGameMode
                event.player.sendMessage(Component.text("Abyss | Your gamemode has been set to " + (event.requestedGameMode)).color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD))
            }
        }
    }
}