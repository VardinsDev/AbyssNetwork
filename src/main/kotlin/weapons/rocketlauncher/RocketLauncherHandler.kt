package me.totxy.weapons.rocketlauncher

import me.totxy.health.HealthManagement
import me.totxy.weapons.PeaceTime.Companion.isActive
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.damage.DamageType
import net.minestom.server.entity.metadata.display.BlockDisplayMeta
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.block.Block
import net.minestom.server.item.Material
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import net.minestom.server.sound.SoundEvent
import net.minestom.server.timer.TaskSchedule
import kotlin.math.floor
import kotlin.math.sqrt

class RocketLauncherHandler {
    fun register(eventHandler: GlobalEventHandler, instanceContainer: InstanceContainer) {
        var lastShotTime = 0L
        val cooldownSeconds = 10
        eventHandler.addListener(PlayerUseItemEvent::class.java) { event ->
            val player = event.player
            if (player.itemInMainHand.material() != Material.WOODEN_AXE) return@addListener

            val now = System.currentTimeMillis()
            val elapsed = (now - lastShotTime) / 1000.0
            val remaining = cooldownSeconds - elapsed

            if (remaining > 0) {
                val filledDots = ((elapsed / cooldownSeconds) * 10).toInt().coerceIn(0, 10)
                val emptyDots = 10 - filledDots
                val bar = "●".repeat(filledDots) + "○".repeat(emptyDots)
                player.sendActionBar(
                    Component.text(bar)
                        .color(TextColor.color(255, 80, 80))
                )
                return@addListener
            }

            lastShotTime = System.currentTimeMillis()

            var cooldownTask: net.minestom.server.timer.Task? = null
            cooldownTask = MinecraftServer.getSchedulerManager().buildTask {
                val elapsed = (System.currentTimeMillis() - lastShotTime) / 1000.0
                if (elapsed >= cooldownSeconds) {
                    player.sendActionBar(Component.text("●●●●●●●●●●").color(TextColor.color(80, 255, 80)))
                    cooldownTask?.cancel()
                    return@buildTask
                }
                val filledDots = ((elapsed / cooldownSeconds) * 10).toInt().coerceIn(0, 10)
                val emptyDots = 10 - filledDots
                val bar = "●".repeat(filledDots) + "○".repeat(emptyDots)
                player.sendActionBar(
                    Component.text(bar)
                        .color(TextColor.color(255, 80, 80))
                )
            }.repeat(TaskSchedule.tick(2)).schedule()

            val eyePos = player.position.add(0.0, player.eyeHeight, 0.0)
            val direction = player.position.direction()

            // Create block display entity
            val rocket = Entity(EntityType.BLOCK_DISPLAY)
            rocket.editEntityMeta(BlockDisplayMeta::class.java) { meta ->
                meta.setBlockState(Block.RED_WOOL)
                meta.scale = Vec(0.4, 0.4, 0.4)
            }
            rocket.setNoGravity(true) // called directly on the entity
            rocket.setInstance(instanceContainer, Pos(eyePos.x, eyePos.y, eyePos.z))
            rocket.velocity = Vec(direction.x(), direction.y(), direction.z()).mul(40.0)

            player.playSound(
                Sound.sound(SoundEvent.ENTITY_FIREWORK_ROCKET_LAUNCH, Sound.Source.PLAYER, 1f, 0.8f)
            )

            var task: net.minestom.server.timer.Task? = null
            var aliveTicks = 0

            task = MinecraftServer.getSchedulerManager().buildTask {
                if (rocket.isRemoved) {
                    task?.cancel()
                    return@buildTask
                }

                // Kill rocket after 10 seconds (200 ticks) no matter what
                aliveTicks++
                if (aliveTicks > 200) {
                    explode(rocket, rocket.position, player, instanceContainer)
                    task?.cancel()
                    return@buildTask
                }

                val pos = rocket.position
                val velocity = rocket.velocity
                val steps = (velocity.length() / 20.0 / 0.2).toInt().coerceAtLeast(6)

                val offsets = listOf(-0.3, 0.0, 0.3)
                var exploded = false

                outerLoop@ for (step in 0..steps) {
                    val fraction = step.toDouble() / steps
                    val checkPos = Pos(
                        pos.x + velocity.x() / 20.0 * fraction,
                        pos.y + velocity.y() / 20.0 * fraction,
                        pos.z + velocity.z() / 20.0 * fraction
                    )

                    for (dx in offsets) {
                        for (dy in offsets) {
                            for (dz in offsets) {
                                val samplePos = checkPos.add(dx, dy, dz)
                                val blockPos = Pos(floor(samplePos.x), floor(samplePos.y), floor(samplePos.z))
                                val block = instanceContainer.getBlock(blockPos)

                                if (block != Block.AIR && block != Block.CAVE_AIR && block != Block.VOID_AIR) {
                                    explode(rocket, checkPos, player, instanceContainer)
                                    task?.cancel()
                                    exploded = true
                                    break@outerLoop
                                }
                            }
                        }
                    }

                    for (target in instanceContainer.players) {
                        if (target == player) continue
                        if (checkPos.distanceSquared(target.position.add(0.0, 0.9, 0.0)) <= 1.2) {
                            explode(rocket, checkPos, player, instanceContainer, target)
                            task?.cancel()
                            exploded = true
                            break@outerLoop
                        }
                    }
                }

                if (!exploded) {
                    rocket.teleport(Pos(
                        pos.x + velocity.x() / 20.0,
                        pos.y + velocity.y() / 20.0,
                        pos.z + velocity.z() / 20.0
                    ))

                    player.instance?.sendGroupedPacket(
                        ParticlePacket(Particle.FLAME, pos.x, pos.y, pos.z, 0.1f, 0.1f, 0.1f, 0.02f, 3)
                    )
                }
            }.repeat(TaskSchedule.tick(1)).schedule()
        }
    }

    private fun explode(
        rocket: Entity,
        pos: Pos,
        shooter: Player,
        instanceContainer: InstanceContainer,
        directHit: Player? = null
    ) {
        rocket.remove()

        val explosionRadius = 4.0

        instanceContainer.players.firstOrNull()?.instance?.sendGroupedPacket(
            ParticlePacket(Particle.EXPLOSION, pos.x, pos.y, pos.z, 0.5f, 0.5f, 0.5f, 0.1f, 20)
        )

        instanceContainer.players.forEach { p ->
            p.playSound(Sound.sound(SoundEvent.ENTITY_GENERIC_EXPLODE, Sound.Source.MASTER, 1f, 1f))
        }

        for (target in instanceContainer.players) {
            if (target == shooter) continue
            val dist = sqrt(pos.distanceSquared(target.position))
            if (dist <= explosionRadius) {
                if (isActive) {
                    shooter.sendMessage(Component.text("Abyss | Peacetime is active! You cannot hurt others!").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.BOLD, true))
                } else {
                    val damage = if (target == directHit) {
                        80
                    } else {
                        (40 * (1.0 - dist / explosionRadius)).toInt()
                    }
                    if (damage > 0) {
                        HealthManagement().damage(target, damage)
                        target.damage(DamageType.ARROW, 0.0001f)
                        target.heal()
                        target.playSound(
                            Sound.sound(SoundEvent.ENTITY_GENERIC_HURT, Sound.Source.PLAYER, 0.5f, 1f)
                        )
                        target.playSound(
                            Sound.sound(SoundEvent.ENTITY_GENERIC_EXPLODE, Sound.Source.PLAYER, 0.5f, 1f)
                        )

                        val knockbackDir = Vec(
                            target.position.x - pos.x,
                            target.position.y - pos.y + 0.5,
                            target.position.z - pos.z
                        ).normalize().mul(20.0 * (1.0 - dist / explosionRadius))
                        target.velocity = knockbackDir
                    }
                }
            }
        }

        MinecraftServer.getSchedulerManager().buildTask {
            shooter.playSound(
                Sound.sound(SoundEvent.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.Source.PLAYER, 1f, 1f)
            )
        }.delay(TaskSchedule.tick(3)).schedule()
    }
}