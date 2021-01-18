package fr.fabienhebuterne.mcscan

import fr.fabienhebuterne.mcscan.service.AnalyseWorldService
import fr.fabienhebuterne.mcscan.service.CountItemService
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import mu.KotlinLogging
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import java.io.File

private val logger = KotlinLogging.logger {}
lateinit var kodein: DI

fun main(args: Array<String>) {
    initKodein()

    val parser = ArgParser("mcscan")
    val worldFolder by parser.option(ArgType.String, shortName = "world", description = "World folder")
    val regionFile by parser.option(ArgType.String, shortName = "region", description = "Region file .mca")
    val playerData by parser.option(ArgType.String, shortName = "player", description = "Player data .dat")
    parser.parse(args)

    logger.info { "Starting McScan..." }

    val analyseWorldService: AnalyseWorldService by kodein.instance()

    worldFolder?.let {
        val folder = File(it)
        analyseWorldService.analyseWorld(folder)
    }

    regionFile?.let {

    }

    playerData?.let {

    }
}

fun initKodein() {
    kodein = DI {
        bind<AnalyseWorldService>() with singleton { AnalyseWorldService(instance()) }
        bind<CountItemService>() with singleton { CountItemService() }
    }
}
