package fr.fabienhebuterne.mcscan.domain

import br.com.gamemods.nbtmanipulator.NbtCompound
import br.com.gamemods.nbtmanipulator.NbtIO
import br.com.gamemods.nbtmanipulator.NbtList
import br.com.gamemods.regionmanipulator.Chunk
import br.com.gamemods.regionmanipulator.RegionIO
import mu.KotlinLogging
import java.io.File
import java.util.regex.Pattern

private val logger = KotlinLogging.logger {}

class AnalyseWorldService(private val countItemService: CountItemService) {

    fun analyseWorld(folder: File) {
        if (!folder.isDirectory || !folder.exists()) {
            throw IllegalAccessException("world path isn't directory or doesn't exist")
        }

        logger.info { "analyze ${folder.name} world..." }

        val regionFolder = File(folder.absolutePath + File.separator + "region")

        // TODO : add async
        // TODO : add progress counter
        regionFolder
            .listFiles { _, name -> name.endsWith(".mca") }
            ?.forEach { file ->
                logger.info { "analyze region ${file.name} from ${folder.name} world..." }
                analyseRegion(file)
            }

        val playerDataFolder = File(folder.absolutePath + File.separator + "playerdata")

        playerDataFolder
            .listFiles { _, name -> name.endsWith(".dat") }
            ?.forEach { file ->
                logger.info { "analyze playerData ${file.name} from ${folder.name} world..." }
                analysePlayerData(file)
            }

    }

    fun analyseRegion(regionFile: File) {
        if (regionFile.extension != "mca") {
            logger.warn { "can't analyze ${regionFile.name} because not have .mca extension" }
            return
        }

        val readRegion = RegionIO.readRegion(regionFile)
        readRegion.values.forEach {
            logger.debug { "analyze chunk x:${it.position.xPos} z:${it.position.zPos} ..." }
            analyseChunk(it)
        }
    }

    private fun analyseChunk(chunk: Chunk) {
        val tileEntities: NbtList<NbtCompound> = chunk.level.getCompoundList("TileEntities")
        countItemService.countTileEntities(tileEntities)
    }

    fun analysePlayerData(playerData: File) {
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

        val readPlayerData = NbtIO.readNbtFile(playerData)

        countItemService.countItemsFromPlayerData(uuid, readPlayerData.compound)
    }

}
