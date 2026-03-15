package me.totxy.events

import net.minestom.server.coordinate.Pos
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.instance.InstanceContainer
import java.util.*

class playerConfiguration (private val eventHandler: GlobalEventHandler, private val instanceContainer: InstanceContainer){
    fun register() {
        eventHandler.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
            val player = event.player
            event.spawningInstance = instanceContainer
            val respawnPosition = Pos(8.5, 39.0, 8.5)
            player.respawnPoint = respawnPosition
            if ((player.uuid == UUID.fromString("93e00cfa-893d-46ba-8248-d8fcefc9327e"))) {
                player.permissionLevel = 4
                print(player.username + " has been auto opped")
            }
        }
    }
}