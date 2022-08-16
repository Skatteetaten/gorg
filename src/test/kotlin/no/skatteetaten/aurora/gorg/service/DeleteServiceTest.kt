package no.skatteetaten.aurora.gorg.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import no.skatteetaten.aurora.gorg.ApplicationDeploymentBuilder
import no.skatteetaten.aurora.gorg.BuildConfigDataBuilder
import no.skatteetaten.aurora.gorg.ProjectDataBuilder
import no.skatteetaten.aurora.gorg.StatusResourceBuilder
import no.skatteetaten.aurora.gorg.extensions.toResource
import no.skatteetaten.aurora.gorg.service.DeleteService.Companion.METRICS_DELETED_RESOURCES
import no.skatteetaten.aurora.mockmvc.extensions.mockwebserver.execute
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser
import java.time.Duration
import java.time.Instant

@WithMockUser
class DeleteServiceTest : AbstractOpenShiftServerTest() {

    private val meterRegistry = SimpleMeterRegistry()

    private lateinit var deleteService: DeleteService

    @BeforeEach
    fun setUp() {
        deleteService = DeleteService(mockClient, meterRegistry, true)
    }

    @Test
    fun `Delete existing project`() {
        val project = ProjectDataBuilder().build()
        val status = StatusResourceBuilder("Success").build()
        val request = mockServer.execute(status) {
            val deleted = deleteService.deleteProject(project.toResource(Instant.now())!!)
            val deletedCount = meterRegistry.deletedResourcesCount("status", "deleted")

            assertThat(deleted).isTrue()
            assertThat(deletedCount).isEqualTo(1.0)
        }
        assertThat(request.first()?.method).isEqualTo("DELETE")
        assertThat(request.first()?.path).isEqualTo(
            "/apis/project.openshift.io/v1/" +
                "projects/${project.metadata.name}"
        )
    }

    @Test
    fun `Return not deleted for non-existing project`() {
        val status = StatusResourceBuilder("Failure").build()
        mockServer.execute(
            404 to status
        ) {
            val deleted = deleteService.deleteProject(
                ProjectResource(
                    name = "non-existing-name",
                    affiliation = "non-existing-affiliation", ttl = Duration.ZERO, removalTime = Instant.now()
                )
            )

            assertThat(deleted).isFalse()
        }
    }

    @Test
    fun `Delete existing buildConfig`() {
        val buildConfig = BuildConfigDataBuilder().build()
        val status = StatusResourceBuilder("Success").build()
        val request = mockServer.execute(status) {
            val deleted = deleteService.deleteBuildConfig(buildConfig.toResource(Instant.now())!!)
            val deletedMetrics = meterRegistry.deletedResourcesCount("status", "deleted")

            assertThat(deleted).isTrue()
            assertThat(deletedMetrics).isEqualTo(1.0)
        }
        assertThat(request.first()?.method).isEqualTo("DELETE")
        assertThat(request.first()?.path).isEqualTo(
            "/apis/build.openshift.io/v1/" +
                "namespaces/${buildConfig.metadata.namespace}" +
                "/buildconfigs/${buildConfig.metadata.name}"
        )
    }

    @Test
    fun `Return not deleted for non-existing buildConfig`() {
        val status = StatusResourceBuilder("Failure").build()
        mockServer.execute(404 to status) {
            val deleted = deleteService.deleteBuildConfig(
                BuildConfigResource(
                    name = "non-existing-name",
                    affiliation = "non-existing-affiliation",
                    ttl = Duration.ZERO,
                    namespace = "namespace",
                    removalTime = Instant.now()
                )
            )
            assertThat(deleted).isFalse()
        }
    }

    @Test
    fun `delete expired applicationDeployments`() {
        val ad = ApplicationDeploymentBuilder().build()
        val status = StatusResourceBuilder("Success").build()
        val request = mockServer.execute(status) {
            val deleted = deleteService.deleteApplicationDeployment(ad.toResource(Instant.now())!!)
            val deletedCount = meterRegistry.deletedResourcesCount("status", "deleted")

            assertThat(deleted).isTrue()
            assertThat(deletedCount).isEqualTo(1.0)
        }
        assertThat(request.first()?.method).isEqualTo("DELETE")
        assertThat(request.first()?.path).isEqualTo(
            "/apis/skatteetaten.no/v1/" +
                "namespaces/${ad.metadata.namespace}/" +
                "applicationdeployments/${ad.metadata.name}"
        )
    }

    @Test
    fun `failed to delete expired applicationDeployments with 404 response`() {
        val ad = ApplicationDeploymentBuilder().build()
        val status = StatusResourceBuilder("Failure").build()
        val request = mockServer.execute(404 to status) {
            val deleted = deleteService.deleteApplicationDeployment(ad.toResource(Instant.now())!!)
            val errorCount = meterRegistry.deletedResourcesCount("status", "error")

            assertThat(deleted).isFalse()
            assertThat(errorCount).isEqualTo(1.0)
        }
        assertThat(request.first()?.method).isEqualTo("DELETE")
        assertThat(request.first()?.path).isEqualTo(
            "/apis/skatteetaten.no/v1/" +
                "namespaces/${ad.metadata.namespace}/" +
                "applicationdeployments/${ad.metadata.name}"
        )
    }

    @Test
    fun `failed to delete expired applicationDeployments with 500 response`() {
        val ad = ApplicationDeploymentBuilder().build()
        val status = StatusResourceBuilder("Failure").build()
        val request = mockServer.execute(500 to status) {
            val deleted = deleteService.deleteApplicationDeployment(ad.toResource(Instant.now())!!)
            val errorCount = meterRegistry.deletedResourcesCount("status", "error")

            assertThat(deleted).isFalse()
            assertThat(errorCount).isEqualTo(1.0)
        }
        assertThat(request.first()?.method).isEqualTo("DELETE")
        assertThat(request.first()?.path).isEqualTo(
            "/apis/skatteetaten.no/v1/" +
                "namespaces/${ad.metadata.namespace}/" +
                "applicationdeployments/${ad.metadata.name}"
        )
    }

    @Test
    fun `return not deleted for non-existing applicationDeployments`() {
        val ad = ApplicationDeploymentResource(
            name = "non-existing-name",
            affiliation = "non-existing-affiliation",
            ttl = Duration.ZERO,
            namespace = "namespace",
            removalTime = Instant.now()
        )
        val deleteService = DeleteService(mockClient, meterRegistry, true)
        val status = StatusResourceBuilder("Failure").build()
        val request = mockServer.execute(404 to status) {
            val deleted = deleteService.deleteApplicationDeployment(ad)
            val deletedCount = meterRegistry.deletedResourcesCount("status", "error")

            assertThat(deleted).isFalse()
            assertThat(deletedCount).isEqualTo(1.0)
        }
        assertThat(request.first()?.method).isEqualTo("DELETE")
        assertThat(request.first()?.path).isEqualTo(
            "/apis/skatteetaten.no/v1/" +
                "namespaces/${ad.namespace}/" +
                "applicationdeployments/${ad.name}"
        )
    }

    @Test
    fun `return skipped if deleteResource is false`() {
        val service = DeleteService(mockClient, meterRegistry, false)
        val buildConfig = BuildConfigDataBuilder().build()
        val deleted = service.deleteBuildConfig(buildConfig.toResource(Instant.now())!!)
        val deletedCount = meterRegistry.deletedResourcesCount("status", "skipped")

        assertThat(deleted).isFalse()
        assertThat(deletedCount).isEqualTo(1.0)
    }

    private fun SimpleMeterRegistry.deletedResourcesCount(tagKey: String, tagValue: String) =
        this.find(METRICS_DELETED_RESOURCES).tag(tagKey, tagValue).counter()?.count()
}
