package me.totxy.events

import net.minestom.server.entity.GameMode
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerPickBlockEvent

class pickBlock {
    fun register(eventHandler: GlobalEventHandler) {
        eventHandler.addListener(PlayerPickBlockEvent::class.java) { event ->
            if (event.player.gameMode.equals(GameMode.CREATIVE)) {
                //Todo: Finish this
            }
        }
    }
}