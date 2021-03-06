package fr.fabienhebuterne.mcscan.domain

data class ItemCounter(
    val item: ItemStorage,
    val count: Long
)

data class ItemStorage(
    val id: String,
    val name: MutableList<ItemName> = mutableListOf(),
    val lores: MutableList<MutableList<ItemLore>> = mutableListOf(),
    val enchantments: List<ItemEnchantment> = listOf(),
    val locations: List<ItemLocationCounter> = listOf(),
    val uuidInventories: List<ItemUuidInventoryCounter> = listOf()
)

data class ItemLocationCounter(
    val itemLocation: ItemLocation,
    val count: Int
)

data class ItemUuidInventoryCounter(
    val uuid: String,
    val count: Int
)
