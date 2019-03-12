package no.skatteetaten.aurora.gorg.service

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotNull
import io.fabric8.kubernetes.api.model.RootPaths
import io.fabric8.openshift.api.model.BuildConfigList
import io.fabric8.openshift.api.model.ProjectList
import no.skatteetaten.aurora.gorg.ApplicationDeploymentBuilder
import no.skatteetaten.aurora.gorg.BuildConfigDataBuilder
import no.skatteetaten.aurora.gorg.ProjectDataBuilder
import no.skatteetaten.aurora.gorg.extensions.execute
import no.skatteetaten.aurora.gorg.model.ApplicationDeploymentList
import org.junit.jupiter.api.Test
import java.time.Instant

class CrawlServiceTest : AbstractOpenShiftServerTest() {

    val root=RootPaths()
    @Test
    fun `Find temporary buildConfigs`() {

        val bc = BuildConfigDataBuilder().build()
        val list = BuildConfigList().apply {
            items = listOf(bc)
        }
        mockServer.execute(root, list) {
            val service = OpenShiftService(mockClient)
            val applications = service.findTemporaryBuildConfigs(Instant.now())
            assertThat(applications).hasSize(1)
            assertThat(applications[0].name).isEqualTo("name")
            assertThat(applications[0].namespace).isEqualTo("namespace")
            assertThat(applications[0].ttl.seconds).isGreaterThan(0)
            assertThat(applications[0].removalTime).isNotNull()
        }
    }

    @Test
    fun `Find temporary projects`() {
        val project = ProjectDataBuilder().build()
        val list = ProjectList().apply {
            items = listOf(project)
        }

        mockServer.execute(root, list) {
            val service = OpenShiftService(mockClient)
            val projects = service.findTemporaryProjects(Instant.now())
            assertThat(projects).hasSize(1)
            assertThat(projects[0].name).isEqualTo("name")
            assertThat(projects[0].ttl.seconds).isGreaterThan(0)
            assertThat(projects[0].removalTime).isNotNull()
        }
    }

    @Test
    fun `Find temporary applicationDeployments`() {

        val ad = ApplicationDeploymentBuilder().build()
        mockServer.execute(ApplicationDeploymentList(items = listOf(ad))) {
            val service = OpenShiftService(mockClient)
            val applications = service.findTemporaryApplicationDeployments(Instant.now())
            assertThat(applications).hasSize(1)
            assertThat(applications[0].name).isEqualTo("name")
            assertThat(applications[0].namespace).isEqualTo("namespace")
            assertThat(applications[0].ttl.seconds).isGreaterThan(0)
            assertThat(applications[0].removalTime).isNotNull()
        }
    }
}