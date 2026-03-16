package me.totxy.events

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.instance.InstanceContainer
import java.net.URI
import java.util.*

class playerConfiguration(private val eventHandler: GlobalEventHandler, private val instanceContainer: InstanceContainer) {
    companion object {
        private val ADMIN_UUIDS = setOf(
            UUID.fromString("93e00cfa-893d-46ba-8248-d8fcefc9327e"),
            UUID.fromString("4f61c127-a842-4eb3-9094-133fefd1a09b"),
            UUID.fromString("62c45d08-8e26-42b4-baa5-924428e72ce6")
        )

        private val PACK_REQUEST = ResourcePackRequest.resourcePackRequest()
            .packs(
                ResourcePackInfo.resourcePackInfo()
                    .id(UUID.nameUUIDFromBytes("alacrity".toByteArray()))
                    .uri(URI.create("https://cdn.modrinth.com/data/PUUpX2qq/versions/dF7VORpp/Alacrity.zip"))
                    .hash("6da1b9fa6f40f9a449c7f74a0cffd462c4b982aa")
                    .build()
            )
            .prompt(Component.text("Please accept the resource pack!"))
            .required(true)
            .build()
    }

    fun register() {
        eventHandler.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
            val player = event.player
            event.spawningInstance = instanceContainer
            player.respawnPoint = Pos(-18.5, 34.0, 13.5)
            player.sendResourcePacks(PACK_REQUEST)

            if (player.uuid in ADMIN_UUIDS) {
                player.permissionLevel = 4
                println("${player.username} has been auto opped")
            }
        }
    }
}