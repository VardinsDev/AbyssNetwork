package me.totxy

import me.totxy.commands.peaceTimeCommand
import me.totxy.database.DatabaseManager
import me.totxy.weapons.ar.ARHandler
import me.totxy.events.*
import me.totxy.health.HealthManagement
import me.totxy.weapons.rocketlauncher.rocketLauncherHandler
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.Auth.Online
import net.minestom.server.MinecraftServer
import net.minestom.server.instance.Instance
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.GenerationUnit
import net.minestom.server.instance.generator.Generator
import net.minestom.server.utils.chunk.ChunkSupplier
import java.util.Objects.hash
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
fun main() {
    //init
    AbyssLogger.printBanner()
    AbyssLogger.info("Starting Abyss Network...")

    DatabaseManager.connect(
        host = "0.tcp.ngrok.io",  // just the hostname, no mysql:// prefix
        port = 12108,                         // the public port goes here, not 3306
        database = "abyssnetwork",
        user = "root",
        password = "REDACTED!"
    )

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
    
    // Load/generate the chunk
    instanceContainer.loadChunk(0, 0).join()

    val teamManager = MinecraftServer.getTeamManager()
    val redTeam = teamManager.createBuilder("glow_red")
        .teamColor(NamedTextColor.RED)
        .build()
    val blueTeam = teamManager.createBuilder("glow_blue")
        .teamColor(NamedTextColor.BLUE)
        .build()

    val manager = MinecraftServer.getCommandManager()
    manager.register(peaceTimeCommand())

    AbyssLogger.success("Server started on port 25565")

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
    //Rocket Launcher
    rocketLauncherHandler().register(globalEventHandler, instanceContainer)
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
        DatabaseManager.disconnect()
        MinecraftServer.getConnectionManager().shutdown()
        try {
            instanceContainer.saveChunksToStorage()
            AbyssLogger.info("World Saved!")
            Thread.sleep(500)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        AbyssLogger.warn("The server is shutting down!")
    })

    minecraftServer.start("0.0.0.0", 25565)
}