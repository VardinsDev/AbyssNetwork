package me.totxy.events

import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.server.ServerTickMonitorEvent
import java.util.UUID

class MSPTMonitor {
    fun register(eventHandler: GlobalEventHandler) {
        eventHandler.addListener(ServerTickMonitorEvent::class.java) { event ->
            val mspt: Double = event.tickMonitor.tickTime
            MinecraftServer.getConnectionManager().onlinePlayers.forEach { person: Player ->
                if (person.uuid == UUID.fromString("93e00cfa-893d-46ba-8248-d8fcefc9327e") || person.uuid == UUID.fromString("4f61c127-a842-4eb3-9094-133fefd1a09b") || person.uuid == UUID.fromString("62c45d08-8e26-42b4-baa5-924428e72ce6")) {
                    person.sendActionBar(Component.text("MSPT: ${"%.2f".format(mspt)}ms"))
                }
            }
        }
    }
}