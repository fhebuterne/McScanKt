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
            .sortedBy { (_, value) -> value }
            .toMap()
            .forEach { entry ->
                // TODO : Do not show here and create an output service with HTML generation or Console colored output
                println(entry.key.toString() + " : " + entry.value)
                itemRepository.create(ItemCounter(entry.key.toItemStorage(), entry.value.toLong()))
            }
    }

}
