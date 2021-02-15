package fr.fabienhebuterne.mcscan.storage

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton
import org.litote.kmongo.KMongo

class KodeinMongo(storageUrl: String) {
    private val client: MongoClient = KMongo.createClient(storageUrl)
    private val db: MongoDatabase = client.getDatabase("mcscan")

    val mongoModuleDi = DI.Module(name = "StorageMongo") {
        bind<ItemCounterRepository>() with singleton { ItemCounterRepositoryMongo(db) }
    }
}


