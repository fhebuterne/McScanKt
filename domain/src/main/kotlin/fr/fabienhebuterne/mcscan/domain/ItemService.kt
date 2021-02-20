package fr.fabienhebuterne.mcscan.domain

import fr.fabienhebuterne.mcscan.storage.ItemCounterRepository

class ItemService(
    private val countItemService: CountItemService,
    private val itemRepository: ItemCounterRepository
) {

    fun resetAndSave() {
        itemRepository.deleteAll()

        countItemService.getCounter()
            .toList()
            .sortedByDescending { (_, value) -> value }
            .toMap()
            .forEach { entry ->
                itemRepository.create(ItemCounter(entry.key.toItemStorage(), entry.value.toLong()))
            }
    }

}
