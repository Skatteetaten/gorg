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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Duration

@ExtendWith(SpringExtension::class)
@WebMvcTest
@WithUserDetails
@DirtiesContext
class CrawlControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockBean
    private lateinit var openShiftService: OpenShiftService

    @MockBean
    private lateinit var deleteService: DeleteService

    @Test
    fun `List projects`() {
        given(openShiftService.findTemporaryProjects(anyOrNull()))
                .willReturn(listOf(ProjectResourceBuilder().build()))

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$[0].name").value("name"))
    }

    @Test
    fun `Delete projects with negative ttl`() {
        given(openShiftService.findTemporaryProjects(anyOrNull()))
                .willReturn(listOf(
                        ProjectResourceBuilder(ttl = Duration.ofSeconds(-10)).build(),
                        ProjectResourceBuilder(ttl = Duration.ofSeconds(10)).build()
                ))

        mockMvc.perform(delete("/api/projects").with(csrf()))
                .andExpect(status().isOk)
        then(deleteService).should(times(1)).deleteProject(anyOrNull())
    }

    @Test
    fun `Get build configs`() {
        given(openShiftService.findTemporaryBuildConfigs(anyOrNull()))
                .willReturn(listOf(BuildConfigResourceBuilder().build()))

        mockMvc.perform(get("/api/buildConfigs"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$[0].name").value("name"))
    }

    @Test
    fun `Delete build configs with negative ttl`() {
        given(openShiftService.findTemporaryBuildConfigs(anyOrNull()))
                .willReturn(listOf(
                        BuildConfigResourceBuilder(ttl = Duration.ofSeconds(-10)).build(),
                        BuildConfigResourceBuilder(ttl = Duration.ofSeconds(10)).build()
                ))

        mockMvc.perform(delete("/api/buildConfigs").with(csrf()))
                .andExpect(status().isOk)
        then(deleteService).should(times(1)).deleteBuildConfig(anyOrNull())
    }

    @Test
    fun `Get application deployments`() {
        given(openShiftService.findTemporaryApplicationDeployments(anyOrNull()))
                .willReturn(listOf(ApplicationDeploymentResourceBuilder().build()))

        mockMvc.perform(get("/api/applicationDeployments"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$[0].name").value("name"))
    }

    @Test
    fun `Delete application deployments with negative ttl`() {
        given(openShiftService.findTemporaryApplicationDeployments(anyOrNull()))
                .willReturn(listOf(
                        ApplicationDeploymentResourceBuilder(ttl = Duration.ofSeconds(-10)).build(),
                        ApplicationDeploymentResourceBuilder(ttl = Duration.ofSeconds(10)).build()
                ))

        mockMvc.perform(delete("/api/applicationDeployments").with(csrf()))
                .andExpect(status().isOk)
        then(deleteService).should(times(1)).deleteApplicationDeployment(anyOrNull())
    }
}