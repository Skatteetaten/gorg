package no.skatteetaten.aurora.gorg.service

import no.skatteetaten.aurora.kubernetes.KubernetesCoroutinesClient
import no.skatteetaten.aurora.kubernetes.KubernetesReactorClient
import no.skatteetaten.aurora.kubernetes.KubernetesRetryConfiguration
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.TestInstance
import org.springframework.web.reactive.function.client.WebClient

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
open class AbstractOpenShiftServerTest() {

    protected val mockServer = MockWebServer()
    private val client = KubernetesReactorClient.create(
        WebClient.create(mockServer.url("/").toString()), "abc123", KubernetesRetryConfiguration(times = 0))
    protected var mockClient = KubernetesCoroutinesClient(client)

    @AfterEach
    fun tearDown() {
        mockServer.shutdown()
    }
}
