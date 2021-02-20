package fr.fabienhebuterne.mcscan.domain

data class Config(
    val storageType: StorageType = StorageType.NONE,
    val storageUrl: String = "",
    val maxThreads: Int = -1
)

enum class StorageType {
    NONE,
    MONGO
}
