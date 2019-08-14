package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.DefaultOpenShiftClient
import io.fabric8.openshift.client.OpenShiftConfigBuilder
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
open class AbstractOpenShiftServerTest {

    protected val mockServer = MockWebServer()
    private val mockServerUrl = mockServer.url("/").toString()
    private val config = OpenShiftConfigBuilder()
        .withDisableApiGroupCheck(true)
        .withMasterUrl(mockServerUrl)
        .build()
    protected var mockClient = DefaultOpenShiftClient(config)

    @AfterEach
    fun tearDown() {
        mockServer.shutdown()
    }
}
