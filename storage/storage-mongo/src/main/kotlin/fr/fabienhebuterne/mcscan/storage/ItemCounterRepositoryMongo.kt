package fr.fabienhebuterne.mcscan.storage

import com.mongodb.client.MongoDatabase
import fr.fabienhebuterne.mcscan.domain.ItemCounter
import org.litote.kmongo.getCollection

class ItemCounterRepositoryMongo(
    db: MongoDatabase
) : RepositoryMongo<ItemCounter>(
    db.getCollection<ItemCounter>(COLLECTION_NAME)
), ItemCounterRepository {
    companion object {
        private const val COLLECTION_NAME = "itemCounter"
    }
}
