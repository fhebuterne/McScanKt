package fr.fabienhebuterne.mcscan.domain

import kotlinx.serialization.Serializable

data class Item(
    val id: String,
    val name: ItemName,
    val lores: List<ItemLore> = listOf(),
    val enchantments: List<ItemEnchantment> = listOf(),
    val locations: HashMap<ItemLocation, Int> = hashMapOf(),
    val uuidInventories: HashMap<String, Int> = hashMapOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        if (id != other.id) return false
        if (name != other.name) return false
        if (lores != other.lores) return false
        if (enchantments != other.enchantments) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + lores.hashCode()
        result = 31 * result + enchantments.hashCode()
        return result
    }
}

data class ItemEnchantment(
    val id: String,
    val level: Int
)

data class ItemLocation(
    val x: Int,
    val y: Int,
    val z: Int
)

@Serializable
data class ItemName(
    val text: String = "",
    val color: String? = null,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underlined: Boolean = false,
    val strikethrough: Boolean = false,
    val obfuscated: Boolean = false,
    val translate: String? = null,
    val extra: List<ItemName> = listOf()
)

@Serializable
data class ItemLore(
    val text: String,
    val color: String? = null,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underlined: Boolean = false,
    val strikethrough: Boolean = false,
    val obfuscated: Boolean = false,
    val extra: List<ItemLore> = listOf()
)
