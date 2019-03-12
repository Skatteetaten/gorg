package no.skatteetaten.aurora.gorg.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import no.skatteetaten.aurora.gorg.ApplicationDeploymentBuilder
import no.skatteetaten.aurora.gorg.BuildConfigDataBuilder
import no.skatteetaten.aurora.gorg.ProjectDataBuilder
import no.skatteetaten.aurora.gorg.extensions.execute
import no.skatteetaten.aurora.gorg.extensions.toResource
import no.skatteetaten.aurora.gorg.service.DeleteService.Companion.METRICS_DELETED_RESOURCES
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

class DeleteServiceTest : AbstractOpenShiftServerTest() {

    val meterRegsitry = SimpleMeterRegistry()

    val deleteService = DeleteService(mockClient, meterRegsitry, true)

    @Test
    fun `Delete existing project`() {
        val project = ProjectDataBuilder().build()

        mockServer.execute(project) {
            val deleted = deleteService.deleteProject(project.toResource(Instant.now()))
            assertThat(deleted).isTrue()
            assertThat(
                meterRegsitry.find(METRICS_DELETED_RESOURCES).tag(
                    "status",
                    "deleted"
                ).counter()?.count()
            ).isEqualTo(
                1.0
            )
        }
    }

    @Test
    fun `Return not deleted for non-existing project`() {
        val deleted = deleteService.deleteProject(
            ProjectResource(
                name = "non-existing-name",
                ttl = Duration.ZERO,
                removalTime = Instant.now()
            )
        )
        assertThat(deleted).isFalse()
    }

    @Test
    fun `Delete existing buildConfig`() {
        val buildConfig = BuildConfigDataBuilder().build()
        mockServer.execute(buildConfig) {
            val deleted = deleteService.deleteBuildConfig(buildConfig.toResource(Instant.now()))
            assertThat(deleted).isTrue()
            assertThat(
                meterRegsitry.find(METRICS_DELETED_RESOURCES).tag(
                    "status",
                    "deleted"
                ).counter()?.count()
            ).isEqualTo(
                1.0
            )
        }
    }

    @Test
    fun `Return not deleted for non-existing buildConfig`() {
        val deleted = deleteService.deleteBuildConfig(
            BuildConfigResource(
                name = "non-existing-name",
                ttl = Duration.ZERO,
                namespace = "namespace",
                removalTime = Instant.now()
            )
        )
        assertThat(deleted).isFalse()
    }

    @Test
    fun `delete expired applicationDeployments`() {
        val ad = ApplicationDeploymentBuilder().build()
        val request = mockServer.execute(
            true
        ) {
            assertThat(deleteService.deleteApplicationDeployment(ad.toResource(Instant.now()))).isTrue()
        }
        assertThat(request.method).isEqualTo("DELETE")
        assertThat(request.path).isEqualTo("/apis/skatteetaten.no/v1/namespaces/${ad.metadata.namespace}/applicationdeployments/${ad.metadata.name}")
        assertThat(meterRegsitry.find(METRICS_DELETED_RESOURCES).tag("status", "deleted").counter()?.count()).isEqualTo(
            1.0
        )
    }

    @Test
    fun `return not deleted for non-existing applicationDeployments`() {
        val deleteService = DeleteService(mockClient, meterRegsitry, true)
        mockServer.execute(
            404, false
        ) {
            assertThat(
                deleteService.deleteApplicationDeployment(
                    ApplicationDeploymentResource(
                        name = "non-existing-name",
                        ttl = Duration.ZERO,
                        namespace = "namespace",
                        removalTime = Instant.now()
                    )
                )
            ).isFalse()
        }
        assertThat(meterRegsitry.find(METRICS_DELETED_RESOURCES).tag("status", "error").counter()?.count()).isEqualTo(
            1.0
        )
    }

    @Test
    fun `return skipped if deleteResource is false`() {
        val buildConfig = BuildConfigDataBuilder().build()
        val deleted = deleteService.deleteBuildConfig(buildConfig.toResource(Instant.now()))
        assertThat(deleted).isFalse()
        assertThat(meterRegsitry.find(METRICS_DELETED_RESOURCES).tag("status", "skipped").counter()?.count()).isEqualTo(
            1.0
        )
    }
}