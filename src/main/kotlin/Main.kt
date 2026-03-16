package me.totxy

import me.totxy.Weapons.ARHandler
import me.totxy.events.*
import me.totxy.health.HealthManagement
import net.minestom.server.Auth.Online
import net.minestom.server.MinecraftServer
import net.minestom.server.instance.Instance
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.GenerationUnit
import net.minestom.server.instance.generator.Generator
import net.minestom.server.utils.chunk.ChunkSupplier
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
fun main() {
    //init
    val minecraftServer = MinecraftServer.init(Online())

    //Instance
    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()

    instanceContainer.chunkSupplier = ChunkSupplier { instance: Instance?, chunkX: Int, chunkZ: Int ->
        LightingChunk(
            instance,
            chunkX,
            chunkZ
        )
    }


    //World defining
    instanceContainer.chunkLoader = AnvilLoader("worlds/world")
    instanceContainer.setGenerator(Generator setGenerator@{ unit: GenerationUnit? ->
        // World coordinates of this generation area
        val startX = unit!!.absoluteStart().blockX()
        val startZ = unit.absoluteStart().blockZ()
        val endX = unit.absoluteEnd().blockX() - 1 // inclusive
        val endZ = unit.absoluteEnd().blockZ() - 1 // inclusive

        // Only generate when this area overlaps chunk (0,0)
        // Chunk 0,0 world X range = 0 to 15
        // Chunk 0,0 world Z range = 0 to 15
        if (endX < 0 || startX > 15 || endZ < 0 || startZ > 15) {
            return@setGenerator  // this area is completely outside chunk 0,0
        }
        unit.modifier().fillHeight(33, 34, Block.STONE)
    })

    //Load the chunk
    //Todo: Get world built

    // Load/generate the chunk
    instanceContainer.loadChunk(0, 0).join()

    //Join
    val globalEventHandler = MinecraftServer.getGlobalEventHandler()
    //PlayerConfigurationEvent
    playerConfiguration(globalEventHandler, instanceContainer).register()
    //PlayerLoadedEvent
    val healthManagement = HealthManagement()
    playerLoaded(globalEventHandler, healthManagement).register()
    tickEvent(minecraftServer, globalEventHandler, healthManagement).register()
    //AR
    ARHandler().register(globalEventHandler, instanceContainer)
    //Gamemode Switcher (F3+F4)
    gamemodeSwitcher().register(globalEventHandler)
    //Leave Event
    playerDisconnect().register(globalEventHandler)
    //MPSTMonitor
    MSPTMonitor().register(globalEventHandler)
    //PickBLock
    pickBlock().register(globalEventHandler)

    /*
    Server off save
     */
    val scheduler = MinecraftServer.getSchedulerManager()
    scheduler.buildShutdownTask(Runnable {
        MinecraftServer.getConnectionManager().shutdown()
        try {
            instanceContainer.saveChunksToStorage()
            println("World Saved!")
            Thread.sleep(500)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        println("The server is shutting down!")
    })

    minecraftServer.start("0.0.0.0", 25565)
}