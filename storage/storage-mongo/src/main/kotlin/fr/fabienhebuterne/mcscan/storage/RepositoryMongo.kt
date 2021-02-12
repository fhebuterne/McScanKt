package fr.fabienhebuterne.mcscan.storage

import com.mongodb.client.MongoCollection

open class RepositoryMongo<T>(
    val collection: MongoCollection<T>
) : Repository<T> {

    override fun findById(entityId: String): T {
        TODO("Not yet implemented")
    }

    override fun create(entity: T): String {
        return collection.insertOne(entity).insertedId?.toString() ?: ""
    }

    override fun update(entity: T): T {
        TODO("Not yet implemented")
    }

    override fun delete(entityId: String) {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        collection.drop()
    }
}
