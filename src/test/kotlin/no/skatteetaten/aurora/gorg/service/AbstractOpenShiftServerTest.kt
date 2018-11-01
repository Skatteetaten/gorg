package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.DefaultOpenShiftClient
import io.fabric8.openshift.client.server.mock.OpenShiftServer
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

open class AbstractOpenShiftServerTest {

    protected val openShiftServer = OpenShiftServer(false, true)
    protected val mockServer = MockWebServer()
    protected var mockClient = DefaultOpenShiftClient(mockServer.url("/").toString())

    @BeforeEach
    fun setUpOpenShiftServer() {
        openShiftServer.before()
    }

    @AfterEach
    fun tearDownOpenShiftServer() {
        openShiftServer.after()
    }
}