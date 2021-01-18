package fr.fabienhebuterne.mcscan

import fr.fabienhebuterne.mcscan.service.AnalyseWorldService
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

lateinit var kodein: DI

fun main(args: Array<String>) {
    initKodein()

    val parser = ArgParser("mcscan")
    val worldFolder by parser.option(ArgType.String, shortName = "world", description = "World folder")
    val regionFile by parser.option(ArgType.String, shortName = "region", description = "Region file .mca")
    val playerData by parser.option(ArgType.String, shortName = "player", description = "Player data .dat")
    parser.parse(args)

    println("Starting McScan...")

    val analyseWorldService: AnalyseWorldService by kodein.instance()

    when {
        worldFolder != null -> {
            analyseWorldService.analyseWorld()
        }
        regionFile != null -> {

        }
        playerData != null -> {

        }
        else -> {
            // TODO : throw exception
        }
    }
}

fun initKodein() {
    kodein = DI {
        bind<AnalyseWorldService>() with singleton { AnalyseWorldService(instance()) }
    }
}
