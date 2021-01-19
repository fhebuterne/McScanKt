package fr.fabienhebuterne.mcscan.domain

import br.com.gamemods.nbtmanipulator.NbtCompound
import br.com.gamemods.nbtmanipulator.NbtList
import java.util.regex.Pattern

class CountItemService {

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
                    """.trimIndent()

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
            }
    }

    private fun countItem() {

    }

}
