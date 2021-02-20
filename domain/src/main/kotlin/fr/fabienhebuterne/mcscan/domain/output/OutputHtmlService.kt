package fr.fabienhebuterne.mcscan.domain.output

import fr.fabienhebuterne.mcscan.domain.CountItemService
import fr.fabienhebuterne.mcscan.domain.Item
import java.io.File
import java.io.InputStream

class OutputHtmlService(private val countItemService: CountItemService) : OutputService {
    override fun showResults() {
        val divList: List<DIV> = countItemService.getCounter()
            .toList()
            .sortedByDescending { (_, value) -> value }
            .toMap()
            .map { entry ->
                parseItemToHtml(entry.key, entry.value)
            }

        val reduce: String = divList.map { it.toString() }.reduce { acc, s -> acc + s }

        val inputStream: InputStream = javaClass.classLoader?.getResource("template.html")?.openStream()
            ?: throw IllegalAccessException("template html not found")

        val template = File("./template.html")
        template.writeBytes(inputStream.readAllBytes())
        val readText = template.readText()
        val replace = readText.replace("{{pattern}}", reduce)
        template.writeText(replace)
    }

    private fun parseItemToHtml(item: Item, count: Int): DIV {
        return div("item") {
            div("item-column-first") {
                div("name") {
                    item.name.getExtra(this, item.name)
                }
                div("enchantments") {
                    item.enchantments.forEach {
                        div {
                            span("color: ${MinecraftColor.GRAY.hexCode}") {
                                +(it.id + " " + it.level)
                            }
                        }
                    }
                }
                div("lores") {
                    item.lores.forEach {
                        div {
                            it.getExtra(this, it)
                        }
                    }
                }
                div("id") {
                    +item.id
                }
            }
            div("item-column-second item-count-center") {
                +count.toString()
            }
        }
    }
}

