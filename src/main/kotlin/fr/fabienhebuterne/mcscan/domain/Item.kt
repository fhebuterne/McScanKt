package fr.fabienhebuterne.mcscan.domain

data class Item(
    val id: String,
    val name: String,
    val lores: List<String>,
    val enchantments: List<String>,
    val locations: List<ItemLocation>,
    val uuidInventories: List<String>
)
