package no.skatteetaten.aurora.gorg.service

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import no.skatteetaten.aurora.gorg.ApplicationDeploymentBuilder
import no.skatteetaten.aurora.gorg.ProjectDataBuilder
import no.skatteetaten.aurora.gorg.extensions.execute
import no.skatteetaten.aurora.gorg.extensions.toResource
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

class DeleteServiceTest : AbstractOpenShiftServerTest() {

    @Test
    fun `Delete existing project`() {
        val project = ProjectDataBuilder().build()
        openShiftServer.openshiftClient.inNamespace("namespace").projects().create(project)

        val deleteService = DeleteService(openShiftServer.openshiftClient, true)
        val deleted = deleteService.deleteProject(project.toResource(Instant.now()))
        assert(deleted).isTrue()
    }

    @Test
    fun `Return not deleted for non-existing project`() {
        val deleteService = DeleteService(openShiftServer.openshiftClient, true)
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
        val deleteService = DeleteService(mockClient, true)
        val ad = ApplicationDeploymentBuilder().build()
        val request = mockServer.execute(
            true
        ) {
            assert(deleteService.deleteApplicationDeployment(ad.toResource(Instant.now()))).isTrue()
        }
        assert(request.method).isEqualTo("DELETE")
        assert(request.path).isEqualTo("/apis/skatteetaten.no/v1/namespaces/${ad.metadata.namespace}/applicationdeployments/${ad.metadata.name}")
    }
}