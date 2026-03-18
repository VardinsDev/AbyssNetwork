package me.totxy.events

import me.totxy.AbyssLogger
import me.totxy.database.PlayerRepository
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.tag.Tag
import java.net.URI
import java.util.*

class playerConfiguration(private val eventHandler: GlobalEventHandler, private val instanceContainer: InstanceContainer) {
    companion object {
        val TEAM_TAG: Tag<Boolean> = Tag.Boolean("teamTag")

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
            val data = PlayerRepository.loadPlayer(player.uuid.toString(), player.username)

            event.spawningInstance = instanceContainer
            player.sendResourcePacks(PACK_REQUEST)

            if ((1..100).random() % 2 == 1) {
                player.setTag(TEAM_TAG, false)
                player.respawnPoint = Pos(-77.5, 34.0, -35.5)
            } else {
                player.setTag(TEAM_TAG, true)
                player.respawnPoint = Pos(-18.5, 34.0, 13.5)
            }

            if (data.isOpped) {
                player.permissionLevel = 4
                AbyssLogger.success("${player.username} has been auto opped")
            }
        }
    }
}