package fr.fabienhebuterne.mcscan.domain

import br.com.gamemods.nbtmanipulator.NbtCompound
import br.com.gamemods.nbtmanipulator.NbtDouble
import br.com.gamemods.nbtmanipulator.NbtInt
import br.com.gamemods.nbtmanipulator.NbtList
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.regex.Pattern

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
                patternContainers.matcher(nbtCompound.getString("id").toLowerCase()).find()
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
            .filter { nbtCompound -> patternContainers.matcher(nbtCompound.getString("id").toLowerCase()).find() }
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
                val name: ItemName = getItemName(nbtCompoundTag, kotlinx)
                val lores: List<ItemLore> = getItemLore(nbtCompoundTag, kotlinx)

                val enchantments: List<ItemEnchantment> = if (nbtCompoundTag.containsKey("Enchantments")) {
                    nbtCompoundTag.getCompoundList("Enchantments").map { it.toItemEnchantment() }
                } else {
                    listOf()
                }

                // We ignore shulker_box item that considered like special item because have lore
                if (id.contains("shulker_box") && lores.isEmpty()) {
                    return
                }

                if (name.text.isNotEmpty() || name.extra.isNotEmpty() || lores.isNotEmpty()) {
                    val item = Item(id, name, lores, enchantments)

                    if (!counter.containsKey(item)) {
                        initLocationOrUuid(item, location, uuid)
                    }

                    updateCounter(item, location, uuid)
                }
            }
    }

    private fun getItemName(
        nbtCompoundTag: NbtCompound,
        kotlinx: Json
    ): ItemName = when {
        nbtCompoundTag.getCompound("display")["Name"] == null -> {
            ItemName()
        }
        nbtCompoundTag.getCompound("display").getString("Name").startsWith("[{") -> {
            kotlinx.decodeFromString<List<ItemName>>(
                nbtCompoundTag.getCompound("display").getString("Name")
            )[0]
        }
        nbtCompoundTag.getCompound("display").getString("Name").startsWith("{") -> {
            try {
                kotlinx.decodeFromString(nbtCompoundTag.getCompound("display").getString("Name"))
            } catch (e: Exception) {
                ItemName(nbtCompoundTag.getCompound("display").getString("Name"))
            }
        }
        else -> {
            ItemName(nbtCompoundTag.getCompound("display").getString("Name"))
        }
    }

    private fun getItemLore(
        nbtCompoundTag: NbtCompound,
        kotlinx: Json
    ): List<ItemLore> {
        return if (nbtCompoundTag.getCompound("display").containsKey("Lore")) {
            nbtCompoundTag.getCompound("display").getStringList("Lore").flatMap { nbtString ->
                when {
                    nbtString.value.startsWith("[{") -> {
                        kotlinx.decodeFromString<List<ItemLore>>(nbtString.value)
                    }
                    nbtString.value.startsWith("{") -> {
                        listOf(kotlinx.decodeFromString<ItemLore>(nbtString.value))
                    }
                    else -> {
                        listOf(ItemLore(nbtString.value))
                    }
                }
            }
        } else {
            listOf()
        }
    }

    @Synchronized
    private fun updateCounter(
        item: Item,
        location: ItemLocation?,
        uuid: String?
    ) {
        counter.keys
            .filter { it == item }
            .forEach { currentItem ->
                computeLocationOrUuid(currentItem, location, uuid)
            }

        counter.computeIfPresent(item) { _, integer -> integer + 1 }
        counter.putIfAbsent(item, 1)
    }

    private fun initLocationOrUuid(
        item: Item,
        location: ItemLocation?,
        uuid: String?
    ) {
        location?.let {
            item.locations.putIfAbsent(it, 1)
        }

        uuid?.let {
            item.uuidInventories.putIfAbsent(it, 1)
        }
    }

    private fun computeLocationOrUuid(
        item: Item,
        location: ItemLocation?,
        uuid: String?
    ) {
        location?.let {
            item.locations.computeIfPresent(it) { _, integer -> integer + 1 }
            item.locations.putIfAbsent(it, 1)
        }

        uuid?.let {
            item.uuidInventories.computeIfPresent(it) { _, integer -> integer + 1 }
            item.uuidInventories.putIfAbsent(it, 1)
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