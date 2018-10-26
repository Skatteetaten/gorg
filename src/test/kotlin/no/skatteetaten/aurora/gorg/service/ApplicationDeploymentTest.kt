package no.skatteetaten.aurora.gorg.service

import assertk.assert
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import io.fabric8.openshift.client.DefaultOpenShiftClient
import no.skatteetaten.aurora.gorg.ApplicationDeploymentBuilder
import no.skatteetaten.aurora.gorg.extensions.execute
import no.skatteetaten.aurora.gorg.model.ApplicationDeploymentList
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test
import java.time.Instant

class ApplicationDeploymentTest {

    private val server = MockWebServer()
    private val url = server.url("/")
    private val openshiftClient = DefaultOpenShiftClient(url.uri().toString())

    private val service = OpenShiftService(openshiftClient)
    private val deleteService = DeleteService(openshiftClient, true)

    private val ad = ApplicationDeploymentBuilder().build()
    @Test
    fun `Should list applicationDeployments`() {
        server.execute(
            ApplicationDeploymentList(items = listOf(ad))
        ) {
            val applications = service.findTemporaryApplicationDeployments(Instant.now())
            assert(applications).hasSize(1)
            assert(applications[0].name).isEqualTo("name")
            assert(applications[0].namespace).isEqualTo("namespace")
            assert(applications[0].ttl.seconds).isGreaterThan(0)
            assert(applications[0].removalTime).isNotNull()
        }
    }

    @Test
    fun `delete expired applicationDeployments`() {
        val request = server.execute(
            true
        ) {
            assert(deleteService.deleteApplicationDeployment(ad.toResource(Instant.now()))).isTrue()
        }
        assert(request.method).isEqualTo("DELETE")
        assert(request.path).isEqualTo("/apis/skatteetaten.no/v1/namespaces/${ad.metadata.namespace}/applicationdeployments/${ad.metadata.name}")
    }
}
