package fr.fabienhebuterne.mcscan.domain

import br.com.gamemods.nbtmanipulator.NbtCompound
import br.com.gamemods.nbtmanipulator.NbtDouble
import br.com.gamemods.nbtmanipulator.NbtInt
import br.com.gamemods.nbtmanipulator.NbtList
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap

class CountItemService {

    private val counter: HashMap<Item, Int> = HashMap()

    fun getCounter(): HashMap<Item, Int> {
        return counter
    }

    fun countTileEntities(tileEntities: NbtList<NbtCompound>) {
        val items = """
                    ^((minecraft:)?([Cc]hest))$|
                    ^((minecraft:)?([Tt]rapped_chest))$|
                    ^((minecraft:)?([Ff]urnace))$|
                    ^((minecraft:)?([Dd]ropper))$|
                    ^((minecraft:)?([Dd]ispenser))$|
                    ^((minecraft:)?([Tt]rap))$|
                    ^((minecraft:)?([Hh]opper))$|
                    ^((minecraft:)?([Cc]hest_minecart))$|
                    ^((minecraft:)?([Ff]urnace_minecart))$|
                    ^((minecraft:)?([Hh]opper_minecart))$|
                    ^minecraft:(.*)shulker_box$
                    """.multilinePattern()

        val patternContainers = Pattern.compile(items)

        tileEntities
            .filter { nbtCompound ->
                patternContainers.matcher(nbtCompound.getString("id").lowercase()).find()
            }
            .forEach {
                val itemLocation = ItemLocation(
                    it.getInt("x"),
                    it.getInt("y"),
                    it.getInt("z")
                )

                countItems(it, itemLocation, null, "Items")
            }
    }

    fun countEntities(entities: NbtList<NbtCompound>) {
        val patternContainers = Pattern.compile("^(minecraft:)?itemframe$|^(minecraft:)?item_frame$")

        entities
            .filter { nbtCompound -> patternContainers.matcher(nbtCompound.getString("id").lowercase()).find() }
            .forEach { entity ->
                val itemLocation = ItemLocation(
                    entity.getInt("TileX"),
                    entity.getInt("TileY"),
                    entity.getInt("TileZ")
                )

                countItems(entity, itemLocation, null, "Item")
            }
    }

    fun countItemsFromPlayerData(uuid: String, nbtCompound: NbtCompound) {
        this.countItems(nbtCompound, null, uuid, "Inventory")
        this.countItems(nbtCompound, null, uuid, "EnderItems")
    }

    // TODO : Only for 1.16.5 now -> need to do an Interface with DataVersion -> Enchantments tag not same in older version
    private fun countItems(
        specificTag: NbtCompound,
        location: ItemLocation?,
        uuid: String?,
        baseTag: String
    ) {
        val items: List<NbtCompound> = if (baseTag == "Item") {
            listOfNotNull(specificTag.getNullableCompound(baseTag))
        } else {
            specificTag.getNullableCompoundList(baseTag)?.toList() ?: listOf()
        }

        // Items have only a display or a lore has been computed
        // TODO : Add config boolean to choose only items have display / lore OR all items
        items
            .filter { it.containsKey("tag") }
            .filter { it.getCompound("tag").containsKey("display") }
            .filter {
                it.getCompound("tag").getCompound("display").containsKey("Lore") ||
                        it.getCompound("tag").getCompound("display").containsKey("Name") ||
                        it.getCompound("tag").containsKey("BlockEntityTag")
            }
            .forEach { nbt ->
                val nbtCompoundTag = nbt.getCompound("tag")

                if (nbtCompoundTag.containsKey("BlockEntityTag")) {
                    countItems(
                        nbtCompoundTag.getCompound("BlockEntityTag"),
                        location,
                        uuid,
                        "Items"
                    )
                }

                val kotlinx = Json { isLenient = true; ignoreUnknownKeys = true }

                val id: String = nbt.getString("id")
                val name: MutableList<ItemName> = getItemName(nbtCompoundTag, kotlinx)
                val lores: MutableList<MutableList<ItemLore>> = getItemLore(nbtCompoundTag, kotlinx)
                val count: Int = nbt.getByte("Count").toInt()

                val enchantments: List<ItemEnchantment> = if (nbtCompoundTag.containsKey("Enchantments")) {
                    nbtCompoundTag.getCompoundList("Enchantments")
                        .filter { it.getString("id") != "null" }
                        .map { it.toItemEnchantment() }
                } else {
                    listOf()
                }

                // We ignore shulker_box item that considered like special item because have lore
                if (id.contains("shulker_box") && lores.isEmpty()) {
                    return
                }

                if (name.isNotEmpty() || lores.isNotEmpty()) {
                    val item = Item(id, name, lores, enchantments)

                    if (!counter.containsKey(item)) {
                        initLocationOrUuid(item, location, uuid, count)
                    }

                    updateCounter(item, location, uuid, count)
                }
            }
    }

    // TODO .copy don't work with abstract how to refactor with BaseItem ?
    private fun deserializeExtras(currentItem: ItemName, itemName: MutableList<ItemName>): MutableList<ItemName> {
        currentItem.extra.forEach {
            itemName.add(it.copy(extra = mutableListOf()))
            deserializeExtras(it, itemName)
        }
        return itemName
    }

    private fun deserializeLoresExtras(currentItem: ItemLore, itemLore: MutableList<ItemLore>): MutableList<ItemLore> {
        currentItem.extra.forEach {
            itemLore.add(it.copy(extra = mutableListOf()))
            deserializeLoresExtras(it, itemLore)
        }
        return itemLore
    }

    private fun getItemName(
        nbtCompoundTag: NbtCompound,
        kotlinx: Json
    ): MutableList<ItemName> = when {
        nbtCompoundTag.getCompound("display")["Name"] == null -> {
            mutableListOf()
        }
        nbtCompoundTag.getCompound("display").getString("Name").startsWith("[{") -> {
            kotlinx.decodeFromString(
                nbtCompoundTag.getCompound("display").getString("Name")
            )
        }
        nbtCompoundTag.getCompound("display").getString("Name").startsWith("{") -> {
            try {
                val mutableList = mutableListOf<ItemName>()
                val itemNames = kotlinx.decodeFromString<ItemName>(nbtCompoundTag.getCompound("display").getString("Name"))
                mutableList.add(itemNames.copy(extra = mutableListOf()))
                deserializeExtras(itemNames, mutableList)
            } catch (e: Exception) {
                mutableListOf(ItemName(nbtCompoundTag.getCompound("display").getString("Name")))
            }
        }
        else -> {
            mutableListOf(ItemName(nbtCompoundTag.getCompound("display").getString("Name")))
        }
    }

    private fun getItemLore(
        nbtCompoundTag: NbtCompound,
        kotlinx: Json
    ): MutableList<MutableList<ItemLore>> {
        return if (nbtCompoundTag.getCompound("display").containsKey("Lore")) {
            nbtCompoundTag.getCompound("display").getStringList("Lore").map { nbtString ->
                when {
                    nbtString.value.startsWith("[{") -> {
                        kotlinx.decodeFromString(nbtString.value)
                    }
                    nbtString.value.startsWith("{") -> {
                        val mutableList = mutableListOf<ItemLore>()
                        val itemLores = kotlinx.decodeFromString<ItemLore>(nbtString.value)
                        mutableList.add(itemLores.copy(extra = mutableListOf()))
                        deserializeLoresExtras(itemLores, mutableList)
                    }
                    else -> {
                        mutableListOf(ItemLore(nbtString.value))
                    }
                }
            }.toMutableList()
        } else {
            mutableListOf()
        }
    }

    @Synchronized
    private fun updateCounter(
        item: Item,
        location: ItemLocation?,
        uuid: String?,
        count: Int
    ) {
        counter.keys
            .filter { it == item }
            .forEach { currentItem ->
                computeLocationOrUuid(currentItem, location, uuid, count)
            }

        counter.computeIfPresent(item) { _, integer -> integer + count }
        counter.putIfAbsent(item, count)
    }

    private fun initLocationOrUuid(
        item: Item,
        location: ItemLocation?,
        uuid: String?,
        count: Int
    ) {
        location?.let {
            item.locations.putIfAbsent(it, count)
        }

        uuid?.let {
            item.uuidInventories.putIfAbsent(it, count)
        }
    }

    private fun computeLocationOrUuid(
        item: Item,
        location: ItemLocation?,
        uuid: String?,
        count: Int
    ) {
        location?.let {
            item.locations.computeIfPresent(it) { _, integer -> integer + count }
            item.locations.putIfAbsent(it, count)
        }

        uuid?.let {
            item.uuidInventories.computeIfPresent(it) { _, integer -> integer + count }
            item.uuidInventories.putIfAbsent(it, count)
        }
    }

    private fun NbtCompound.toItemEnchantment(): ItemEnchantment {
        val level = when {
            this["lvl"] is NbtInt -> {
                this.getInt("lvl")
            }
            this["lvl"] is NbtDouble -> {
                this.getDouble("lvl").toInt()
            }
            else -> {
                this.getShort("lvl").toInt()
            }
        }

        return ItemEnchantment(
            this.getString("id"),
            level
        )
    }

}
