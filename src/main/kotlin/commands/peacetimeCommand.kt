package me.totxy.commands

import me.totxy.weapons.PeaceTime
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player

class peaceTimeCommand : Command("peacetime") {
    init {
        setDefaultExecutor { sender, _ ->
            if (sender !is Player) return@setDefaultExecutor
            if (sender.permissionLevel < 2) {
                sender.sendMessage(
                    Component.text("Abyss | You don't have permission to use this command.").color(NamedTextColor.RED)
                )
                return@setDefaultExecutor
            }

            PeaceTime.isActive = !PeaceTime.isActive

            val message = if (PeaceTime.isActive)
                Component.text("Abyss | Peace Time is Active!").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.BOLD, true)
            else
                Component.text("Abyss | Peace Time is Disabled!").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.BOLD, true)

            MinecraftServer.getConnectionManager().onlinePlayers.forEach { it.sendMessage(message) }
        }
    }
}