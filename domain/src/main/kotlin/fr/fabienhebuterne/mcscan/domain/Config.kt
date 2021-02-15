package fr.fabienhebuterne.mcscan.domain

data class Config(
    val storageType: StorageType = StorageType.NONE,
    val storageUrl: String = ""
)

enum class StorageType {
    NONE,
    MONGO
}
