package no.skatteetaten.aurora.gorg.controller

import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.then
import com.nhaarman.mockito_kotlin.times
import no.skatteetaten.aurora.gorg.ApplicationDeploymentResourceBuilder
import no.skatteetaten.aurora.gorg.BuildConfigResourceBuilder
import no.skatteetaten.aurora.gorg.ProjectResourceBuilder
import no.skatteetaten.aurora.gorg.service.DeleteService
import no.skatteetaten.aurora.gorg.service.OpenShiftService
import no.skatteetaten.aurora.mockmvc.extensions.Path
import no.skatteetaten.aurora.mockmvc.extensions.delete
import no.skatteetaten.aurora.mockmvc.extensions.get
import no.skatteetaten.aurora.mockmvc.extensions.responseJsonPath
import no.skatteetaten.aurora.mockmvc.extensions.statusIsOk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import java.time.Duration

@ExtendWith(SpringExtension::class)
@WebMvcTest(secure = false)
@DirtiesContext
@AutoConfigureRestDocs
class CrawlControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockBean
    private lateinit var openShiftService: OpenShiftService

    @MockBean
    private lateinit var deleteService: DeleteService

    @Test
    fun `List projects`() {
        given(openShiftService.findTemporaryProjects(anyOrNull()))
            .willReturn(listOf(ProjectResourceBuilder().build()))

        mockMvc.get(Path("/api/projects")) {
            statusIsOk().responseJsonPath("$[0].name").equalsValue("name")
        }
    }

    @Test
    fun `Delete projects with negative ttl`() {
        given(openShiftService.findTemporaryProjects(anyOrNull()))
            .willReturn(
                listOf(
                    ProjectResourceBuilder(ttl = Duration.ofSeconds(-10)).build(),
                    ProjectResourceBuilder(ttl = Duration.ofSeconds(10)).build()
                )
            )

        mockMvc.delete(Path("/api/projects")) {
            statusIsOk()
        }
        then(deleteService).should(times(1)).deleteProject(anyOrNull())
    }

    @Test
    fun `Get build configs`() {
        given(openShiftService.findTemporaryBuildConfigs(anyOrNull()))
            .willReturn(listOf(BuildConfigResourceBuilder().build()))

        mockMvc.get(Path("/api/buildConfigs")) {
            statusIsOk().responseJsonPath("$[0].name").equalsValue("name")
        }
    }

    @Test
    fun `Delete build configs with negative ttl`() {
        given(openShiftService.findTemporaryBuildConfigs(anyOrNull()))
            .willReturn(
                listOf(
                    BuildConfigResourceBuilder(ttl = Duration.ofSeconds(-10)).build(),
                    BuildConfigResourceBuilder(ttl = Duration.ofSeconds(10)).build()
                )
            )

        mockMvc.delete(Path("/api/buildConfigs")) {
            statusIsOk()
        }
        then(deleteService).should(times(1)).deleteBuildConfig(anyOrNull())
    }

    @Test
    fun `Get application deployments`() {
        given(openShiftService.findTemporaryApplicationDeployments(anyOrNull()))
            .willReturn(listOf(ApplicationDeploymentResourceBuilder().build()))

        mockMvc.get(Path("/api/applicationDeployments")) {
            statusIsOk().responseJsonPath("$[0].name").equalsValue("name")
        }
    }

    @Test
    fun `Delete application deployments with negative ttl`() {
        given(openShiftService.findTemporaryApplicationDeployments(anyOrNull()))
            .willReturn(
                listOf(
                    ApplicationDeploymentResourceBuilder(ttl = Duration.ofSeconds(-10)).build(),
                    ApplicationDeploymentResourceBuilder(ttl = Duration.ofSeconds(10)).build()
                )
            )

        mockMvc.delete(Path("/api/applicationDeployments")) {
            statusIsOk()
        }
        then(deleteService).should(times(1)).deleteApplicationDeployment(anyOrNull())
    }
}