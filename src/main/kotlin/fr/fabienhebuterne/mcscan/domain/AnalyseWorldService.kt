package fr.fabienhebuterne.mcscan.domain

import br.com.gamemods.nbtmanipulator.NbtCompound
import br.com.gamemods.nbtmanipulator.NbtList
import br.com.gamemods.regionmanipulator.Chunk
import br.com.gamemods.regionmanipulator.RegionIO
import mu.KotlinLogging
import java.io.File

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
            .listFiles { _, name -> name.contains(".mca") }
            ?.forEach { file ->
                logger.info { "analyze region ${file.name} from ${folder.name} world..." }
                analyseRegion(file)
            }
    }

    fun analyseRegion(regionFile: File) {
        if (!regionFile.name.contains(".mca")) {
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

    fun analysePlayerData() {
        // TODO : Analyze player data .dat
    }

}
