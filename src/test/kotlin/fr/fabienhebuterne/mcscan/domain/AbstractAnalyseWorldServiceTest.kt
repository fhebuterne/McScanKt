package fr.fabienhebuterne.mcscan.domain

import fr.fabienhebuterne.mcscan.domain.tools.Zip
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal abstract class AbstractAnalyseWorldServiceTest(private val version: String) {

    protected val countItemServiceMock = mockk<CountItemService>()
    protected val analyseWorldService = AnalyseWorldService(countItemServiceMock)

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

    suspend fun `should analyze world`() {
        // given
        val world = File("src/test/resources/$version/world")
        every { countItemServiceMock.countTileEntities(any()) } just Runs
        every { countItemServiceMock.countItemsFromPlayerData(any(), any()) } just Runs

        // when
        analyseWorldService.analyseWorld(world)
    }
}
