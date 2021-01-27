package fr.fabienhebuterne.mcscan.domain.v1_16_5

import fr.fabienhebuterne.mcscan.domain.AbstractCountItemService
import fr.fabienhebuterne.mcscan.domain.Item
import fr.fabienhebuterne.mcscan.domain.ItemEnchantment
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CountItemServiceTest: AbstractCountItemService("1.16.5") {

    @Test
    fun `should count tile entities on 1_16_5 from region 0 0 on one chunk`() {
        super.`should count tile entities from region 0 0 on specific chunk`(9, 12)

        // then
        val firstExceptedItem = Item(
            "minecraft:diamond_axe",
            "[{\"text\":\"aaaa\",\"bold\":true}]",
            listOf(),
            listOf(ItemEnchantment(id="minecraft:flame", level=2))
        )

        val secondExceptedItem = Item(
            "minecraft:diamond_axe",
            "[{\"text\":\"test\",\"color\":\"red\",\"bold\":true},{\"text\":\"\",\"color\":\"red\",\"bold\":false}]",
            listOf("[{\"text\":\"test lore ok\",\"bold\":true}]", "[{\"text\":\"aaa\",\"bold\":true,\"color\":\"green\"}]"),
            listOf()
        )

        val counter = countItemService.getCounter()

        expect {
            that(counter[firstExceptedItem]).isEqualTo(6)
            that(counter[secondExceptedItem]).isEqualTo(2)
        }
    }

    @Test
    fun `should count tile entities on 1_16_5 from player data`() {
        val uuid = "522841e6-a3b6-48dd-b67c-0b0f06ec1aa6"
        super.`should count tile entities from player data`(uuid)

        // then
        val firstExceptedItem = Item(
            "minecraft:diamond_axe",
            "[{\"text\":\"aaaa\",\"bold\":true}]",
            listOf(),
            listOf(ItemEnchantment(id="minecraft:flame", level=2))
        )

        expectThat(countItemService.getCounter()[firstExceptedItem]).isEqualTo(3)
    }
}
