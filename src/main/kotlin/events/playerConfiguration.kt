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
            val respawnPosition = Pos(8.5, 34.0, 8.5)
            player.respawnPoint = respawnPosition
            if (player.uuid == UUID.fromString("93e00cfa-893d-46ba-8248-d8fcefc9327e") || player.uuid == UUID.fromString("4f61c127-a842-4eb3-9094-133fefd1a09b") || player.uuid == UUID.fromString("62c45d08-8e26-42b4-baa5-924428e72ce6")) {
                player.permissionLevel = 4
                println(player.username + " has been auto opped")
            }
        }
    }
}