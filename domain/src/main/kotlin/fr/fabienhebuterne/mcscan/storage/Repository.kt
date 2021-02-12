package fr.fabienhebuterne.mcscan.storage

interface Repository<T> {
    fun findById(entityId: String): T
    fun create(entity: T): String
    fun update(entity: T): T
    fun delete(entityId: String)
    fun deleteAll()
}
