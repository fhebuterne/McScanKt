package fr.fabienhebuterne.mcscan

import fr.fabienhebuterne.mcscan.domain.AnalyseWorldService
import fr.fabienhebuterne.mcscan.domain.CountItemService
import fr.fabienhebuterne.mcscan.domain.ItemService
import fr.fabienhebuterne.mcscan.storage.mongoModuleDi
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

suspend fun main(args: Array<String>) {
    initKodein()

    val parser = ArgParser("mcscan")
    val worldFolder by parser.option(ArgType.String, shortName = "w", description = "World folder")
    val regionFile by parser.option(ArgType.String, shortName = "r", description = "Region file .mca")
    val playerData by parser.option(ArgType.String, shortName = "p", description = "Player data .dat")
    parser.parse(args)

    logger.info { "Starting McScan..." }

    val analyseWorldService: AnalyseWorldService by kodein.instance()

    worldFolder?.let {
        analyseWorldService.analyseWorld(File(it))
    }

    regionFile?.let {
        analyseWorldService.analyseRegion(File(it))
    }

    playerData?.let {
        analyseWorldService.analysePlayerData(File(it))
    }

    val itemService: ItemService by kodein.instance()

    itemService.resetAndSave()
}

fun initKodein() {
    kodein = DI {
        bind<CountItemService>() with singleton { CountItemService() }
        bind<AnalyseWorldService>() with singleton { AnalyseWorldService(instance()) }
        bind<ItemService>() with singleton { ItemService(instance(), instance()) }
        import(mongoModuleDi)
    }
}
