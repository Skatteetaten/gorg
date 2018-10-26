package no.skatteetaten.aurora.gorg.service

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import no.skatteetaten.aurora.gorg.ApplicationDeploymentBuilder
import no.skatteetaten.aurora.gorg.ProjectDataBuilder
import no.skatteetaten.aurora.gorg.extensions.execute
import no.skatteetaten.aurora.gorg.extensions.toResource
import no.skatteetaten.aurora.gorg.service.DeleteService.Companion.METRICS_DELETED_RESOURCES
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

class DeleteServiceTest : AbstractOpenShiftServerTest() {

    val meterRegsitry = SimpleMeterRegistry()

    @Test
    fun `Delete existing project`() {
        val project = ProjectDataBuilder().build()
        openShiftServer.openshiftClient.inNamespace("namespace").projects().create(project)

        val deleteService = DeleteService(openShiftServer.openshiftClient, meterRegsitry, true)
        val deleted = deleteService.deleteProject(project.toResource(Instant.now()))
        assert(deleted).isTrue()
        assert(meterRegsitry.find(METRICS_DELETED_RESOURCES).counter()?.count()).isEqualTo(1.0)
    }

    @Test
    fun `Return not deleted for non-existing project`() {
        val deleteService = DeleteService(openShiftServer.openshiftClient, meterRegsitry, true)
        val deleted = deleteService.deleteProject(
            ProjectResource(
                name = "non-existing-name",
                ttl = Duration.ZERO,
                removalTime = Instant.now()
            )
        )
        assert(deleted).isFalse()
    }

    @Test
    fun `delete expired applicationDeployments`() {
        val deleteService = DeleteService(mockClient, meterRegsitry, true)
        val ad = ApplicationDeploymentBuilder().build()
        val request = mockServer.execute(
            true
        ) {
            assert(deleteService.deleteApplicationDeployment(ad.toResource(Instant.now()))).isTrue()
        }
        assert(request.method).isEqualTo("DELETE")
        assert(request.path).isEqualTo("/apis/skatteetaten.no/v1/namespaces/${ad.metadata.namespace}/applicationdeployments/${ad.metadata.name}")
        assert(meterRegsitry.find(METRICS_DELETED_RESOURCES).counter()?.count()).isEqualTo(1.0)
    }
}