package me.totxy

import me.totxy.commands.peaceTimeCommand
import me.totxy.database.DatabaseManager
import me.totxy.events.*
import me.totxy.health.HealthManagement
import me.totxy.weapons.ar.ARHandler
import me.totxy.weapons.rocketlauncher.rocketLauncherHandler
import net.kyori.adventure.key.Key
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
import rocks.minestom.placement.AxisPlacementRule
import rocks.minestom.placement.BannerPlacementRule
import rocks.minestom.placement.BedPlacementRule
import rocks.minestom.placement.ButtonPlacementRule
import rocks.minestom.placement.CactusFlowerPlacementRule
import rocks.minestom.placement.CactusPlacementRule
import rocks.minestom.placement.CeilingHangingSignPlacementRule
import rocks.minestom.placement.ChestPlacementRule
import rocks.minestom.placement.CropPlacementRule
import rocks.minestom.placement.DoorPlacementRule
import rocks.minestom.placement.FenceGatePlacementRule
import rocks.minestom.placement.FencePlacementRule
import rocks.minestom.placement.GlassPanePlacementRule
import rocks.minestom.placement.HorizontalFacingPlacementRule
import rocks.minestom.placement.MushroomPlacementRule
import rocks.minestom.placement.PlantPlacementRule
import rocks.minestom.placement.RailPlacementRule
import rocks.minestom.placement.SlabPlacementRule
import rocks.minestom.placement.StairPlacementRule
import rocks.minestom.placement.StandingSignPlacementRule
import rocks.minestom.placement.SugarCanePlacementRule
import rocks.minestom.placement.TallPlantPlacementRule
import rocks.minestom.placement.TrapdoorPlacementRule
import rocks.minestom.placement.Utility
import rocks.minestom.placement.WallHangingSignPlacementRule
import rocks.minestom.placement.WallPlacementRule
import rocks.minestom.placement.WallSignPlacementRule
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import io.github.cdimascio.dotenv.dotenv

private fun registerPlacementRules() {
    Utility.registerPlacementRules(
        { block: Block? -> AxisPlacementRule(block!!) },
        Block.CREAKING_HEART,
        Block.HAY_BLOCK,
        Block.DEEPSLATE,
        Block.INFESTED_DEEPSLATE,
        Block.MUDDY_MANGROVE_ROOTS,
        Block.BAMBOO_BLOCK,
        Block.STRIPPED_BAMBOO_BLOCK,
        Block.BASALT,
        Block.POLISHED_BASALT,
        Block.QUARTZ_PILLAR,
        Block.PURPUR_PILLAR,
        Block.BONE_BLOCK,
        Block.OCHRE_FROGLIGHT,
        Block.VERDANT_FROGLIGHT,
        Block.PEARLESCENT_FROGLIGHT
    )

    Utility.registerPlacementRules({ block: Block? -> AxisPlacementRule(block!!) }, Key.key("minecraft:logs"))
    Utility.registerPlacementRules({ block: Block? -> StairPlacementRule(block!!) }, StairPlacementRule.KEY)
    Utility.registerPlacementRules({ block: Block? -> SlabPlacementRule(block!!) }, SlabPlacementRule.KEY)
    Utility.registerPlacementRules({ block: Block? -> FencePlacementRule(block!!) }, FencePlacementRule.KEY)
    Utility.registerPlacementRules({ block: Block? -> FenceGatePlacementRule(block!!) }, FenceGatePlacementRule.KEY)
    Utility.registerPlacementRules({ block: Block? -> WallPlacementRule(block!!) }, WallPlacementRule.KEY)
    Utility.registerPlacementRules(
        { block: Block? -> GlassPanePlacementRule(block!!) },
        Block.GLASS_PANE,
        Block.IRON_BARS,
        Block.WHITE_STAINED_GLASS_PANE,
        Block.ORANGE_STAINED_GLASS_PANE,
        Block.MAGENTA_STAINED_GLASS_PANE,
        Block.LIGHT_BLUE_STAINED_GLASS_PANE,
        Block.YELLOW_STAINED_GLASS_PANE,
        Block.LIME_STAINED_GLASS_PANE,
        Block.PINK_STAINED_GLASS_PANE,
        Block.GRAY_STAINED_GLASS_PANE,
        Block.LIGHT_GRAY_STAINED_GLASS_PANE,
        Block.CYAN_STAINED_GLASS_PANE,
        Block.PURPLE_STAINED_GLASS_PANE,
        Block.BLUE_STAINED_GLASS_PANE,
        Block.BROWN_STAINED_GLASS_PANE,
        Block.GREEN_STAINED_GLASS_PANE,
        Block.RED_STAINED_GLASS_PANE,
        Block.BLACK_STAINED_GLASS_PANE
    )

    Utility.registerPlacementRules({ block: Block? -> DoorPlacementRule(block!!) }, DoorPlacementRule.KEY)
    Utility.registerPlacementRules({ block: Block? -> BedPlacementRule(block!!) }, BedPlacementRule.KEY)
    Utility.registerPlacementRules({ block: Block? -> ButtonPlacementRule(block!!) }, ButtonPlacementRule.KEY)
    Utility.registerPlacementRules({ block: Block? -> TrapdoorPlacementRule(block!!) }, TrapdoorPlacementRule.KEY)
    Utility.registerPlacementRules(
        { block: Block? -> StandingSignPlacementRule(block!!) },
        StandingSignPlacementRule.KEY
    )
    Utility.registerPlacementRules({ block: Block? -> WallSignPlacementRule(block!!) }, WallSignPlacementRule.KEY)
    Utility.registerPlacementRules(
        { block: Block? -> CeilingHangingSignPlacementRule(block!!) },
        CeilingHangingSignPlacementRule.KEY
    )
    Utility.registerPlacementRules(
        { block: Block? -> WallHangingSignPlacementRule(block!!) },
        WallHangingSignPlacementRule.KEY
    )
    Utility.registerPlacementRules({ block: Block? -> BannerPlacementRule(block!!) }, BannerPlacementRule.KEY)
    Utility.registerPlacementRules(
        { block: Block? -> HorizontalFacingPlacementRule(block!!) },
        Block.FURNACE,
        Block.BLAST_FURNACE,
        Block.SMOKER,
        Block.STONECUTTER
    )
    Utility.registerPlacementRules({ block: Block? -> ChestPlacementRule(block!!) }, Block.CHEST)
    Utility.registerPlacementRules({ block: Block? -> PlantPlacementRule(block!!) }, PlantPlacementRule.KEY)
    Utility.registerPlacementRules({ block: Block? -> PlantPlacementRule(block!!) }, Key.key("minecraft:saplings"))
    Utility.registerPlacementRules({ block: Block? -> CropPlacementRule(block!!) }, CropPlacementRule.KEY)
    Utility.registerPlacementRules(
        { block: Block? -> TallPlantPlacementRule(block!!) },
        Block.SUNFLOWER,
        Block.LILAC,
        Block.PEONY,
        Block.ROSE_BUSH,
        Block.TALL_GRASS,
        Block.LARGE_FERN,
        Block.TALL_SEAGRASS,
        Block.PITCHER_PLANT
    )

    Utility.registerPlacementRules(
        { block: Block? -> MushroomPlacementRule(block!!) },
        Block.BROWN_MUSHROOM,
        Block.RED_MUSHROOM
    )
    Utility.registerPlacementRules({ block: Block? -> SugarCanePlacementRule(block!!) }, Block.SUGAR_CANE)
    Utility.registerPlacementRules({ block: Block? -> CactusPlacementRule(block!!) }, Block.CACTUS)
    Utility.registerPlacementRules({ block: Block? -> CactusFlowerPlacementRule(block!!) }, Block.CACTUS_FLOWER)
    Utility.registerPlacementRules({ block: Block? -> RailPlacementRule(block!!) }, RailPlacementRule.KEY)
}

@OptIn(ExperimentalAtomicApi::class)
fun main() {
    //init
    AbyssLogger.printBanner()
    AbyssLogger.info("Starting Abyss Network...")


    val env = dotenv()

    DatabaseManager.connect(
        host = env["DB_HOST"],
        port = env["DB_PORT"].toInt(),
        database = env["DB_NAME"],
        user = env["DB_USER"],
        password = env["DB_PASSWORD"]
    )

    val minecraftServer = MinecraftServer.init(Online())
    AbyssLogger.success("Server Initiated!")
    registerPlacementRules()
    AbyssLogger.success("Placement Rules Registered!")

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
    AbyssLogger.success("Chunks Loaded!")

    val teamManager = MinecraftServer.getTeamManager()
    val redTeam = teamManager.createBuilder("glow_red")
        .teamColor(NamedTextColor.RED)
        .build()
    val blueTeam = teamManager.createBuilder("glow_blue")
        .teamColor(NamedTextColor.BLUE)
        .build()

    val manager = MinecraftServer.getCommandManager()
    manager.register(peaceTimeCommand())



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
    AbyssLogger.success("Server started on port 25565")
}