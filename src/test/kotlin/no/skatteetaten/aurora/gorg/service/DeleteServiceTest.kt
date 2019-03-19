package no.skatteetaten.aurora.gorg.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import io.fabric8.kubernetes.api.model.RootPaths
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import no.skatteetaten.aurora.gorg.ApplicationDeploymentBuilder
import no.skatteetaten.aurora.gorg.BuildConfigDataBuilder
import no.skatteetaten.aurora.gorg.ProjectDataBuilder
import no.skatteetaten.aurora.gorg.extensions.execute
import no.skatteetaten.aurora.gorg.extensions.executeWithStatus
import no.skatteetaten.aurora.gorg.extensions.toResource
import no.skatteetaten.aurora.gorg.service.DeleteService.Companion.METRICS_DELETED_RESOURCES
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

class DeleteServiceTest : AbstractOpenShiftServerTest() {

    private val meterRegsitry = SimpleMeterRegistry()

    private lateinit var deleteService: DeleteService

    private val root = RootPaths()

    @BeforeEach
    fun setUp() {
        deleteService = DeleteService(mockClient, meterRegsitry, true)
    }

    @Test
    fun `Delete existing project`() {
        val project = ProjectDataBuilder().build()

        mockServer.execute(root, project) {
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
        mockServer.executeWithStatus(
            200 to root,
            404 to false
        ) {
            val deleted = deleteService.deleteProject(
                ProjectResource(
                    name = "non-existing-name",
                    ttl = Duration.ZERO,
                    removalTime = Instant.now()
                )
            )
            assertThat(deleted).isFalse()
        }
    }

    @Test
    fun `Delete existing buildConfig`() {
        val buildConfig = BuildConfigDataBuilder().build()
        mockServer.execute(root, buildConfig, buildConfig) {
            val deleted = deleteService.deleteBuildConfig(buildConfig.toResource(Instant.now()))
            val deletedMetrics = meterRegsitry.find(METRICS_DELETED_RESOURCES)
                .tag("status", "deleted").counter()?.count()
            assertThat(deleted).isTrue()
            assertThat(deletedMetrics).isEqualTo(1.0)
        }
    }

    @Test
    fun `Return not deleted for non-existing buildConfig`() {
        mockServer.executeWithStatus(
            200 to root,
            404 to false
        ) {
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
        val service = DeleteService(mockClient, meterRegsitry, false)
        val buildConfig = BuildConfigDataBuilder().build()
        val deleted = service.deleteBuildConfig(buildConfig.toResource(Instant.now()))
        assertThat(deleted).isFalse()
        assertThat(meterRegsitry.find(METRICS_DELETED_RESOURCES).tag("status", "skipped").counter()?.count()).isEqualTo(
            1.0
        )
    }
}