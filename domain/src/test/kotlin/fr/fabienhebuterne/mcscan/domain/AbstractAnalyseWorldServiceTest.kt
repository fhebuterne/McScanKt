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
    private val configServiceMock = mockk<ConfigService>()
    private val analyseWorldService = AnalyseWorldService(countItemServiceMock, configServiceMock)

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
        every { countItemServiceMock.countEntities(any()) } just Runs
        every { countItemServiceMock.countItemsFromPlayerData(any(), any()) } just Runs
        every { configServiceMock.config } returns Config()

        // when
        analyseWorldService.analyseWorld(world)
    }
}
