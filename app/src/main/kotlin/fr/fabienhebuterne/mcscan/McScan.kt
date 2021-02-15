package fr.fabienhebuterne.mcscan

import fr.fabienhebuterne.mcscan.domain.*
import fr.fabienhebuterne.mcscan.storage.ItemCounterRepository
import fr.fabienhebuterne.mcscan.storage.ItemCounterRepositoryNone
import fr.fabienhebuterne.mcscan.storage.KodeinMongo
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

    val configService: ConfigService by kodein.instance()
    configService.load()

    initStorageWithKodein(configService)

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

    if (configService.config.storageType != StorageType.NONE) {
        itemService.resetAndSave()
    }

    itemService.showItems()
}

private fun initStorageWithKodein(configService: ConfigService) {
    kodein = when (configService.config.storageType) {
        StorageType.MONGO -> {
            logger.info { "mongo storage configured" }
            val kodeinInit = DI {
                extend(kodein)
                bind<KodeinMongo>() with singleton { KodeinMongo(configService.config.storageUrl) }
            }

            val kodeinMongo: KodeinMongo by kodeinInit.instance()

            DI {
                extend(kodeinInit)
                import(kodeinMongo.mongoModuleDi)
                bind<ItemService>() with singleton { ItemService(instance(), instance()) }
            }
        }
        StorageType.NONE -> {
            logger.info { "no storage configured" }
            DI {
                extend(kodein)
                bind<ItemCounterRepository>() with singleton { ItemCounterRepositoryNone() }
                bind<ItemService>() with singleton { ItemService(instance(), instance()) }
            }
        }
    }
}

private fun initKodein() {
    kodein = DI {
        bind<ConfigService>() with singleton { ConfigService() }
        bind<CountItemService>() with singleton { CountItemService() }
        bind<AnalyseWorldService>() with singleton { AnalyseWorldService(instance()) }
    }
}
