package me.totxy.events

import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerPickBlockEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import java.util.*
import java.util.function.Consumer

class pickBlock {
    fun register(eventHandler: GlobalEventHandler) {
        eventHandler.addListener(PlayerPickBlockEvent::class.java) {event ->
            if (event.player.gameMode == GameMode.CREATIVE) {
                val block = event.block
                val item = blockToItemStack(block, 1)
                event.player.setItemInMainHand(item)
            }
        }
    }
}

fun blockToItemStack(block: Block, amount: Int): ItemStack {
    return ItemStack.of(block.registry()!!.material(), amount)
}

fun blockToItemStack(block: Block): ItemStack {
    return blockToItemStack(block, 1)
}