package me.totxy.health

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect
import net.minestom.server.tag.Tag
import java.time.Duration
import java.util.*
import kotlin.math.roundToInt

class HealthManagement {
    val healthBars: MutableMap<UUID?, BossBar> = HashMap<UUID?, BossBar>()
    val shieldBars: MutableMap<UUID?, BossBar> = HashMap<UUID?, BossBar>()
    val healthTag: Tag<Double?>? = Tag.Double("health")
    val shieldTag: Tag<Double?>? = Tag.Double("shield")

    fun addHealthBar(player: Player) {
        player.setTag<Double?>(healthTag, 100.0)
        player.setTag<Double?>(shieldTag, 100.0)

        val healthBar = BossBar.bossBar(
            Component.text("Health: " + player.getTag<Double?>(healthTag)?.roundToInt()?.toInt()),
            (player.getTag<Double?>(healthTag)?.div(100.0))?.toFloat() ?: 1f,
            BossBar.Color.RED,
            BossBar.Overlay.PROGRESS
        )
        val shieldBar = BossBar.bossBar(
            Component.text("Shield: " + player.getTag<Double?>(shieldTag)?.roundToInt()?.toInt()),
            (player.getTag<Double?>(shieldTag)?.div(100.0f))?.toFloat() ?: 1f,
            BossBar.Color.BLUE,
            BossBar.Overlay.PROGRESS
        )

        healthBars[player.uuid] = healthBar
        shieldBars[player.uuid] = shieldBar
    }
    fun damage(player: Player, damage: Int) {
        var health = player.getTag(healthTag)
        var shield = player.getTag(shieldTag)
        var overShield = 0.0
        if (shield != null) {
            if (shield >= damage) {
                shield -= damage
            } else if (shield < damage) {
                shield = shield.minus(damage)
                overShield = shield
                shield = 0.0
                health = health?.plus(overShield)
            }
        }
        if (health != null) {
            if (health <= 0) {
                MinecraftServer.getConnectionManager().onlinePlayers.forEach { person ->
                    person.sendMessage(Component.text(player.username + " has been shot to death!"))
                }
                player.respawn()
                player.showTitle(
                    Title.title(
                        Component.text("You DIED").color(NamedTextColor.RED),
                        Component.text(""),
                        Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
                    )
                )
                player.setTag(healthTag, 100.0)
                player.setTag(shieldTag, 100.0)
                player.addEffect(Potion(PotionEffect.BLINDNESS, 1, 100))
            }
        }
        player.setTag(healthTag, health)
        player.setTag(shieldTag, shield)
    }
    fun getHealthBar(player: Player): BossBar? {
        return healthBars[player.uuid]
    }
    fun getShieldBar(player: Player): BossBar? {
        return shieldBars[player.uuid]
    }
    fun updateBars(person: Player) {
        val healthBar = healthBars[person.uuid] ?: return
        val shieldBar = shieldBars[person.uuid] ?: return
        val teamTag: Tag<Boolean?>? = Tag.Boolean("teamTag")

        if (person.getTag(healthTag) == null) person.setTag(healthTag, 100.0)
        if (person.getTag(shieldTag) == null) person.setTag(shieldTag, 0.0)

        val playerHealth = person.getTag(healthTag) ?: 100.0
        val playerShield = person.getTag(shieldTag) ?: 0.0

        healthBar.progress((playerHealth / 100.0).toFloat().coerceIn(0f, 1f))
        shieldBar.progress((playerShield / 100.0).toFloat().coerceIn(0f, 1f))
        healthBar.name(Component.text("Health: ${playerHealth.roundToInt()}"))
        shieldBar.name(Component.text("Shield: ${playerShield.roundToInt()}"))

        if (playerHealth <= 0) {
            person.setTag(healthTag, 100.0)
            person.setTag(shieldTag, 0.0)
            person.teleport(person.respawnPoint)
            person.showTitle(
                Title.title(
                    Component.text("You DIED").color(NamedTextColor.RED),
                    Component.text(""),
                    Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
                )
            )
            person.addEffect(Potion(PotionEffect.BLINDNESS, 1, 100))
        }
    }
}