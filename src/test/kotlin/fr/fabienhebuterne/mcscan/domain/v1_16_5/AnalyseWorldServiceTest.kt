package fr.fabienhebuterne.mcscan.domain.v1_16_5

import fr.fabienhebuterne.mcscan.domain.AbstractAnalyseWorldServiceTest
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AnalyseWorldServiceTest : AbstractAnalyseWorldServiceTest("1.16.5") {

    @Test
    fun `should analyze 1_16_5 world`() {
        super.`should analyze world`()
        // then
        verify(exactly = 680) { countItemServiceMock.countTileEntities(any()) }
    }
}
