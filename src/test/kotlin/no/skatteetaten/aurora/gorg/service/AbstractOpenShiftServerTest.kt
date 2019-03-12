package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.DefaultOpenShiftClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

open class AbstractOpenShiftServerTest {

    protected val mockServer = MockWebServer()
    protected var mockClient = DefaultOpenShiftClient(mockServer.url("/").toString())

    @BeforeEach
    fun setup() {
       mockServer.start()
    }
    @AfterEach
    fun tearDown() {
        mockServer.shutdown()
    }
}
