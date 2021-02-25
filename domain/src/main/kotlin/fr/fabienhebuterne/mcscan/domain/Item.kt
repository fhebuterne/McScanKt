package fr.fabienhebuterne.mcscan.domain

import fr.fabienhebuterne.mcscan.domain.output.DIV
import fr.fabienhebuterne.mcscan.domain.output.MinecraftColor
import kotlinx.serialization.Serializable

data class Item(
    val id: String,
    val name: MutableList<ItemName> = mutableListOf(),
    val lores: MutableList<MutableList<ItemLore>> = mutableListOf(),
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

    fun getFormattedText(
        div: DIV,
        firstColor: String?
    ) {
        color?.let {
            return getColoredText(it, div)
        } ?: run {
            if (firstColor != null) {
                return getColoredText(firstColor, div)
            }

            return div.span {
                +text
            }
        }
    }

    private fun getColoredText(color: String, div: DIV) {
        val minecraftColor = if (!color.contains("#")) {
            MinecraftColor.valueOf(color.toUpperCase()).hexCode
        } else {
            color
        }
        return div.span("color: ${minecraftColor};") {
            +text
        }
    }
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
    override val extra: MutableList<ItemName> = mutableListOf(),
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
