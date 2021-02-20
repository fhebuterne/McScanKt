package fr.fabienhebuterne.mcscan.domain

import br.com.gamemods.nbtmanipulator.NbtIO
import br.com.gamemods.regionmanipulator.Chunk
import br.com.gamemods.regionmanipulator.RegionIO
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.io.File
import java.util.concurrent.Executors
import java.util.regex.Pattern

private val logger = KotlinLogging.logger {}

class AnalyseWorldService(
    private val countItemService: CountItemService,
    private val configService: ConfigService
) {

    private var regionComputed = 1
    private var playerDataComputed = 1

    @Synchronized
    fun incrementRegion(regionFile: File, currentFolder: File? = null, count: Int) {
        logger.info { "analyze region ${regionFile.name} from ${currentFolder?.name ?: "unknown"} world... - $regionComputed/$count analyzed" }
        regionComputed++
    }

    @Synchronized
    fun incrementPlayerData(playerData: File, currentFolder: File? = null, count: Int) {
        logger.info { "analyze playerData ${playerData.name} from ${currentFolder?.name ?: "unknown"} world... - $playerDataComputed/$count analyzed" }
        playerDataComputed++
    }

    suspend fun analyseWorld(folder: File) {
        if (!folder.isDirectory || !folder.exists()) {
            throw IllegalAccessException("world path isn't directory or doesn't exist")
        }

        logger.info { "analyze ${folder.name} world..." }

        val regionFolder = File(folder.absolutePath + File.separator + "region")

        val maxThreads = when {
            configService.config.maxThreads <= 0 -> {
                getAvailableProcessor()
            }
            configService.config.maxThreads > Runtime.getRuntime().availableProcessors() -> {
                getAvailableProcessor()
            }
            else -> {
                configService.config.maxThreads
            }
        }

        val fixedThreadPool = Executors.newFixedThreadPool(maxThreads)
        val asCoroutineDispatcher: ExecutorCoroutineDispatcher = fixedThreadPool.asCoroutineDispatcher()

        coroutineScope {
            val regions = regionFolder.listFiles { _, name -> name.endsWith(".mca") }
            val regionTotal = regions?.count() ?: 0
            regions
                ?.forEach { file ->
                    launch(asCoroutineDispatcher) {
                        analyseRegion(file, folder, regionTotal)
                    }
                }

            val playerDataFolder = File(folder.absolutePath + File.separator + "playerdata")
            val playerDatas = playerDataFolder.listFiles { _, name -> name.endsWith(".dat") }
            val playerDataTotal = playerDatas?.count() ?: 0
            playerDatas?.forEach { file ->
                launch(asCoroutineDispatcher) {
                    analysePlayerData(file, folder, playerDataTotal)
                }
            }
        }

        fixedThreadPool.shutdown()
    }

    fun analyseRegion(regionFile: File, currentFolder: File? = null, count: Int = 1) {
        if (regionFile.extension != "mca") {
            logger.warn { "can't analyze ${regionFile.name} because not have .mca extension" }
            return
        }

        incrementRegion(regionFile, currentFolder, count)
        RegionIO.readRegion(regionFile).values.forEach {
            logger.debug { "analyze chunk x:${it.position.xPos} z:${it.position.zPos} ..." }
            analyseChunk(it)
        }
    }

    private fun analyseChunk(chunk: Chunk) {
        chunk.level.getNullableCompoundList("TileEntities")?.let {
            countItemService.countTileEntities(it)
        }
        chunk.level.getNullableCompoundList("Entities")?.let {
            countItemService.countEntities(it)
        }
    }

    fun analysePlayerData(playerData: File, currentFolder: File? = null, count: Int = 1) {
        if (playerData.extension != "dat") {
            logger.warn { "can't analyze ${playerData.name} because not have .dat extension" }
            return
        }

        val patternUuid = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
        val uuid = playerData.nameWithoutExtension

        if (!patternUuid.matcher(uuid).find()) {
            logger.warn { "$uuid is a file with bad uuid format, skipped" }
            return
        }

        incrementPlayerData(playerData, currentFolder, count)
        countItemService.countItemsFromPlayerData(uuid, NbtIO.readNbtFile(playerData).compound)
    }

}
