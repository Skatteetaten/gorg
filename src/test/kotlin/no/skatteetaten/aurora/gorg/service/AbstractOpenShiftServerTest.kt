package no.skatteetaten.aurora.gorg.service

import no.skatteetaten.aurora.kubernetes.KubernetesClient
import no.skatteetaten.aurora.kubernetes.testutils.kubernetesToken
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.TestInstance
import org.springframework.web.reactive.function.client.WebClient

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
open class AbstractOpenShiftServerTest {

    protected val mockServer = MockWebServer()
    protected var mockClient = KubernetesClient.create(WebClient.create(mockServer.url("/").toString()), kubernetesToken())

    @AfterEach
    fun tearDown() {
        mockServer.shutdown()
    }
}
