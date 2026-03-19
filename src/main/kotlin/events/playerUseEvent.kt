package me.totxy.events

import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.item.PlayerBeginItemUseEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag

class playerUseEvent {
    fun register(eventHandler: GlobalEventHandler) {
        eventHandler.addListener(PlayerBeginItemUseEvent::class.java) {event ->
            val player = event.player
            val usingItem: Tag<Boolean?>? = Tag.Boolean("usingItem")
            if (event.itemStack.equals(ItemStack.of(Material.COOKED_BEEF))) {
                if (player.getTag(usingItem) == true) {
                    return@addListener
                } else if (player.getTag(usingItem) == null) {
                    player.setTag(usingItem, true)
                } else if (player.getTag(usingItem) == false) {
                    player.setTag(usingItem, true)
                }

            }
        }
    }
}