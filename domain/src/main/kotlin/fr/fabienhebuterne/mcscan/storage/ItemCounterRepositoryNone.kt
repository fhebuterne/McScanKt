package fr.fabienhebuterne.mcscan.storage

import fr.fabienhebuterne.mcscan.domain.ItemCounter

class ItemCounterRepositoryNone : ItemCounterRepository {
    override fun findById(entityId: String): ItemCounter {
        throw IllegalAccessException("Storage not configured")
    }

    override fun create(entity: ItemCounter): String {
        throw IllegalAccessException("Storage not configured")
    }

    override fun update(entity: ItemCounter): ItemCounter {
        throw IllegalAccessException("Storage not configured")
    }

    override fun delete(entityId: String) {
        throw IllegalAccessException("Storage not configured")
    }

    override fun deleteAll() {
        throw IllegalAccessException("Storage not configured")
    }
}
