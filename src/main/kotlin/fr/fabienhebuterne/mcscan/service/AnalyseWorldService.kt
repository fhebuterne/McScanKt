package fr.fabienhebuterne.mcscan.service

import mu.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger {}

class AnalyseWorldService(private val countItemService: CountItemService) {

    fun analyseWorld(folder: File) {
        if (!folder.isDirectory || !folder.exists()) {
            throw IllegalAccessException("world path isn't directory or doesn't exist")
        }

        logger.info { "analyze world ${folder.name}..." }

        val regionFolder = File(folder.absolutePath + File.separator + "region")

        regionFolder.listFiles()?.forEach { file ->
            println(file.name)
        }
    }

    fun analyseRegion() {
        // TODO : Analyze region .mca
    }

    fun analysePlayerData() {
        // TODO : Analyze player data .dat
    }

}
