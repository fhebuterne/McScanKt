package fr.fabienhebuterne.mcscan.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import mu.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger {}

class ConfigService {

    lateinit var config: Config

    fun load() {
        val mapper = ObjectMapper(YAMLFactory())

        val inputStream = if (File("./config.yml").exists()) {
            logger.info { "External config file found using it" }
            File("./config.yml").inputStream()
        } else {
            logger.info { "External config file not found using default config from classLoader" }
            javaClass.classLoader.getResource("config.yml")?.openStream()
        }

        config = mapper.readValue(inputStream, Config::class.java)
    }

}
