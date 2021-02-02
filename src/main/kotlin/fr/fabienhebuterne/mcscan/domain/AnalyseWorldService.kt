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

class AnalyseWorldService(private val countItemService: CountItemService) {

    suspend fun analyseWorld(folder: File) {
        if (!folder.isDirectory || !folder.exists()) {
            throw IllegalAccessException("world path isn't directory or doesn't exist")
        }

        logger.info { "analyze ${folder.name} world..." }

        val regionFolder = File(folder.absolutePath + File.separator + "region")

        val maxThreads = Runtime.getRuntime().availableProcessors()
        val fixedThreadPool = Executors.newFixedThreadPool(maxThreads)
        val asCoroutineDispatcher: ExecutorCoroutineDispatcher = fixedThreadPool.asCoroutineDispatcher()

        coroutineScope {
            // TODO : add progress counter
            regionFolder
                .listFiles { _, name -> name.endsWith(".mca") }
                ?.forEach { file ->
                    launch(asCoroutineDispatcher) {
                        analyseRegion(file, folder)
                    }
                }

            val playerDataFolder = File(folder.absolutePath + File.separator + "playerdata")

            playerDataFolder
                .listFiles { _, name -> name.endsWith(".dat") }
                ?.forEach { file ->
                    launch(asCoroutineDispatcher) {
                        analysePlayerData(file, folder)
                    }
                }
        }

        fixedThreadPool.shutdown()
    }

    fun analyseRegion(regionFile: File, currentFolder: File? = null) {
        if (regionFile.extension != "mca") {
            logger.warn { "can't analyze ${regionFile.name} because not have .mca extension" }
            return
        }

        logger.info { "analyze region ${regionFile.name} from ${currentFolder?.name ?: "unknown"} world..." }

        val readRegion = RegionIO.readRegion(regionFile)
        readRegion.values.forEach {
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

    fun analysePlayerData(playerData: File, currentFolder: File? = null) {
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

        logger.info { "analyze playerData ${playerData.name} from ${currentFolder?.name ?: "unknown"} world..." }

        val readPlayerData = NbtIO.readNbtFile(playerData)

        countItemService.countItemsFromPlayerData(uuid, readPlayerData.compound)
    }

}
