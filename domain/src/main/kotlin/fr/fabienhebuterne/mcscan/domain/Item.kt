package fr.fabienhebuterne.mcscan.domain

import fr.fabienhebuterne.mcscan.domain.output.DIV
import fr.fabienhebuterne.mcscan.domain.output.MinecraftColor
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

fun Item.toItemStorage(): ItemStorage = ItemStorage(
    id,
    name,
    lores,
    enchantments,
    locations.map { ItemLocationCounter(it.key, it.value) },
    uuidInventories.map { ItemUuidInventoryCounter(it.key, it.value) }
)

data class ItemEnchantment(
    val id: String,
    val level: Int
)

data class ItemLocation(
    val x: Int,
    val y: Int,
    val z: Int
)

// TODO FHE : Kotlin has a bug with @Serializable on inherited class need to use abstract to work
// Issue : https://youtrack.jetbrains.com/issue/KT-38958
@Serializable
abstract class ItemBase {
    abstract val text: String
    abstract val color: String?
    abstract val bold: Boolean
    abstract val italic: Boolean
    abstract val underlined: Boolean
    abstract val strikethrough: Boolean
    abstract val obfuscated: Boolean
    abstract val extra: List<ItemBase>

    fun getExtra(div: DIV, itemBase: ItemBase, first: Boolean = true): String {
        if (itemBase.extra.isNullOrEmpty() && first) {
            getExtraText(itemBase, div, itemBase.text)
        }

        return itemBase.extra.map {
            getExtraText(it, div, getExtra(div, it, false))
        }.fold(itemBase.text) { acc, s -> acc + s }
    }

    private fun getExtraText(
        itemBase: ItemBase,
        div: DIV,
        text: String
    ) {
        itemBase.color?.let {
            val minecraftColor = if (!it.contains("#")) {
                MinecraftColor.valueOf(it.toUpperCase()).hexCode
            } else {
                it
            }
            div.span("color: ${minecraftColor};") {
                +text
            }
        } ?: div.span {
            +text
        }
    }
}

fun ItemBase.getExtraBase(): String {
    if (this.extra.isNullOrEmpty()) {
        return text
    }

    return this.extra.map {
        it.getExtraBase()
    }.fold(text) { acc, s -> acc + s }
}

@Serializable
data class ItemName(
    override val text: String = "",
    override val color: String? = null,
    override val bold: Boolean = false,
    override val italic: Boolean = false,
    override val underlined: Boolean = false,
    override val strikethrough: Boolean = false,
    override val obfuscated: Boolean = false,
    override val extra: List<ItemName> = listOf(),
    val translate: String? = null
) : ItemBase()

@Serializable
data class ItemLore(
    override val text: String = "",
    override val color: String? = null,
    override val bold: Boolean = false,
    override val italic: Boolean = false,
    override val underlined: Boolean = false,
    override val strikethrough: Boolean = false,
    override val obfuscated: Boolean = false,
    override val extra: List<ItemLore> = listOf()
) : ItemBase()
