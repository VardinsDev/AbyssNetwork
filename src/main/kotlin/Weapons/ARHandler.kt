package me.totxy.Weapons

import me.totxy.health.HealthManagement
import net.kyori.adventure.sound.Sound
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.entity.damage.DamageType
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.block.Block
import net.minestom.server.item.Material
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import net.minestom.server.sound.SoundEvent
import net.minestom.server.tag.Tag
import net.minestom.server.timer.TaskSchedule
import kotlin.math.floor

class ARHandler {
    fun register(eventHandler: GlobalEventHandler, instanceContainer: InstanceContainer) {
        eventHandler.addListener(PlayerUseItemEvent::class.java) { event ->
            val player = event.player
            if (player.itemInMainHand.material() != Material.WOODEN_HOE) return@addListener

            val eyePos = player.position.add(0.0, player.eyeHeight, 0.0)
            val direction = player.position.direction()
            val health = Tag.Double("health")
            val shield = Tag.Double("shield")

            var i = 1.0
            while (i <= 100) {
                val point = eyePos.asVec().add(direction.mul(i))
                val exactPos = point.asPos()
                val blockPos = Pos(floor(point.x()), floor(point.y()), floor(point.z()))
                val hit = isPlayerAtPosition(instanceContainer, exactPos, player)

                if (instanceContainer.getBlock(blockPos) !== Block.AIR) {
                    break
                } else if (hit !== player) {
                    hit.playSound(Sound.sound(SoundEvent.ENTITY_FIREWORK_ROCKET_BLAST, Sound.Source.PLAYER, .25f, 1f))
                    HealthManagement().damage(hit, 12)
                    hit.damage(DamageType.ARROW, .0001f)
                    hit.heal()

                    MinecraftServer.getSchedulerManager().buildTask {
                        player.playSound(Sound.sound(SoundEvent.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.Source.PLAYER, 1f, 1f))
                        hit.playSound(Sound.sound(SoundEvent.ENTITY_GENERIC_HURT, Sound.Source.PLAYER, .25f, 1f))
                    }.delay(TaskSchedule.tick(3)).schedule()

                    break
                } else {
                    player.instance?.sendGroupedPacket(
                        ParticlePacket(
                            Particle.CRIT,
                            point.x(), point.y(), point.z(),
                            0f, 0f, 0f,
                            0f,
                            1
                        )
                    )
                }
                i += 0.5
            }
            player.playSound(Sound.sound(SoundEvent.ENTITY_FIREWORK_ROCKET_BLAST, Sound.Source.PLAYER, .25f, 1f))
        }
    }

    fun isPlayerAtPosition(instance: Instance, targetPos: Pos, shooter: Player): Player {
        for (player in instance.players) {
            if (player == shooter) continue
            val feetPos = player.position
            val height = if (player.isSneaking) 1.5 else 1.8
            var y = 0.0
            while (y <= height) {
                val checkPos = feetPos.add(0.0, y, 0.0)
                if (targetPos.distanceSquared(checkPos) <= 0.5 * 0.5) {
                    return player
                }
                y += 0.3
            }
        }
        return shooter
    }
}