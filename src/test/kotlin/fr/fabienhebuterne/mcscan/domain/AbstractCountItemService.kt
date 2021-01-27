package fr.fabienhebuterne.mcscan.domain

import br.com.gamemods.nbtmanipulator.NbtIO
import br.com.gamemods.regionmanipulator.Chunk
import br.com.gamemods.regionmanipulator.ChunkPos
import br.com.gamemods.regionmanipulator.RegionIO
import fr.fabienhebuterne.mcscan.domain.tools.Zip
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal abstract class AbstractCountItemService(private val version: String) {

    protected lateinit var countItemService: CountItemService

    @BeforeAll
    fun start() {
        val fileZip = "src/test/resources/$version/world.zip"
        val destDir = File("src/test/resources/$version/world")
        Zip().unzip(fileZip, destDir)
    }

    @AfterAll
    fun end() {
        val file = File("src/test/resources/$version/world")
        file.deleteRecursively()
    }

    fun `should count tile entities from region 0 0 on specific chunk`(xPos: Int, zPos: Int) {
        // given
        countItemService = CountItemService()
        val regionFile = File("src/test/resources/$version/world/region/r.0.0.mca")
        val firstRegion = RegionIO.readRegion(regionFile)
        val chunk: Chunk = firstRegion.values.firstOrNull { it.position == ChunkPos(xPos, zPos) }
            ?: throw IllegalAccessException("Error chunk not found")
        val tileEntities = chunk.level.getCompoundList("TileEntities")

        // when
        countItemService.countTileEntities(tileEntities)
    }

    fun `should count tile entities from player data`(uuid: String) {
        // given
        countItemService = CountItemService()
        val playerData = File("src/test/resources/$version/world/playerdata/$uuid.dat")
        val readNbtFile = NbtIO.readNbtFile(playerData)

        // when
        countItemService.countItemsFromPlayerData(uuid, readNbtFile.compound)
    }
}
