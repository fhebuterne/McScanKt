package fr.fabienhebuterne.mcscan.domain.output

import fr.fabienhebuterne.mcscan.domain.CountItemService
import fr.fabienhebuterne.mcscan.domain.Item
import mu.KotlinLogging
import java.io.File
import java.io.InputStream

private val logger = KotlinLogging.logger {}

class OutputHtmlService(private val countItemService: CountItemService) : OutputService {
    override fun showResults() {
        logger.info { "start parsing item into html" }

        val inputStream: InputStream = javaClass.classLoader?.getResource("template.html")?.openStream()
            ?: throw IllegalAccessException("template html not found")
        val template = File("./template.html")
        template.writeBytes(inputStream.readAllBytes())

        // TODO : Split into pages when have too many item in one page
        countItemService.getCounter()
            .toList()
            .sortedByDescending { (_, value) -> value }
            .forEach { (item, count) ->
                logger.info { "parsing current item into html and writing to file ..." }

                val parseItemToHtml = parseItemToHtml(item, count)
                template.appendText(parseItemToHtml.toString())
            }

        template.appendText("</body>")
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

