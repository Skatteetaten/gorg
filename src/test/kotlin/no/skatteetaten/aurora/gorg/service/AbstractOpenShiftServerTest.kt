package no.skatteetaten.aurora.gorg.service

import no.skatteetaten.aurora.kubernetes.HttpClientTimeoutConfiguration
import no.skatteetaten.aurora.kubernetes.KubernetesCoroutinesClient
import no.skatteetaten.aurora.kubernetes.KubernetesConfiguration
import no.skatteetaten.aurora.kubernetes.RetryConfiguration
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
open class AbstractOpenShiftServerTest {

    protected val mockServer = MockWebServer()
    private val url = mockServer.url("/")
    private val config = KubernetesConfiguration(
        retry = RetryConfiguration(0),
        timeout = HttpClientTimeoutConfiguration(),
        url = url.toString()
    )
    private val client = config.createTestClient("test-token")

    protected var mockClient = KubernetesCoroutinesClient(client, null)

    @AfterEach
    fun tearDown() {
        mockServer.shutdown()
    }
}
