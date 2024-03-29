package no.skatteetaten.aurora.gorg.service

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotNull
import io.fabric8.kubernetes.api.model.KubernetesList
import io.fabric8.kubernetes.internal.KubernetesDeserializer
import io.fabric8.openshift.api.model.BuildConfigList
import io.fabric8.openshift.api.model.ProjectList
import no.skatteetaten.aurora.gorg.ApplicationDeploymentBuilder
import no.skatteetaten.aurora.gorg.BuildConfigDataBuilder
import no.skatteetaten.aurora.gorg.ProjectDataBuilder
import no.skatteetaten.aurora.gorg.model.ApplicationDeployment
import no.skatteetaten.aurora.mockmvc.extensions.mockwebserver.execute
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser
import java.time.Instant
import io.micrometer.core.instrument.simple.SimpleMeterRegistry

@WithMockUser
class CrawlServiceTest : AbstractOpenShiftServerTest() {
    private val meterRegistry = SimpleMeterRegistry()

    @Test
    fun `Find temporary buildConfigs`() {
        val bc = BuildConfigDataBuilder().build()
        val list = BuildConfigList().apply {
            items = listOf(bc)
        }
        mockServer.execute(list) {
            val service = KubernetesService(mockClient, meterRegistry)
            val buildConfigs = service.findTemporaryBuildConfigs(Instant.now())
            assertThat(buildConfigs).hasSize(1)
            assertThat(buildConfigs[0].name).isEqualTo("name")
            assertThat(buildConfigs[0].namespace).isEqualTo("namespace")
            assertThat(buildConfigs[0].ttl.seconds).isGreaterThan(0)
            assertThat(buildConfigs[0].removalTime).isNotNull()
        }
    }

    @Test
    fun `Find temporary projects`() {
        val project = ProjectDataBuilder().build()
        val list = ProjectList().apply {
            items = listOf(project)
        }

        mockServer.execute(list) {
            val service = KubernetesService(mockClient, meterRegistry)
            val projects = service.findTemporaryProjects(Instant.now())
            assertThat(projects).hasSize(1)
            assertThat(projects[0].name).isEqualTo("name")
            assertThat(projects[0].ttl.seconds).isGreaterThan(0)
            assertThat(projects[0].removalTime).isNotNull()
        }
    }

    @Test
    fun `Find temporary applicationDeployments`() {
        KubernetesDeserializer.registerCustomKind(
            "skatteetaten.no/v1",
            "ApplicationDeploymentList",
            KubernetesList::class.java
        )

        KubernetesDeserializer.registerCustomKind(
            "skatteetaten.no/v1",
            "ApplicationDeployment",
            ApplicationDeployment::class.java
        )
        val ad = ApplicationDeploymentBuilder().build()
        val list = KubernetesList().apply {
            items = listOf(ad)
        }
        mockServer.execute(list) {
            val service = KubernetesService(mockClient, meterRegistry)
            val applications = service.findTemporaryApplicationDeployments(Instant.now())
            assertThat(applications).hasSize(1)
            assertThat(applications[0].name).isEqualTo("name")
            assertThat(applications[0].namespace).isEqualTo("namespace")
            assertThat(applications[0].ttl.seconds).isGreaterThan(0)
            assertThat(applications[0].removalTime).isNotNull()
        }
    }
}
