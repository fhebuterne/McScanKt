package fr.fabienhebuterne.mcscan.storage

import com.mongodb.client.MongoDatabase
import fr.fabienhebuterne.mcscan.domain.Item
import org.litote.kmongo.getCollection

class ItemRepositoryMongo(
    db: MongoDatabase
) : RepositoryMongo<Item>(
    db.getCollection<Item>(COLLECTION_NAME)
), ItemRepository {
    companion object {
        private const val COLLECTION_NAME = "item"
    }
}
