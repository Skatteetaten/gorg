package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.DefaultOpenShiftClient
import okhttp3.mockwebserver.MockWebServer

open class AbstractOpenShiftServerTest {

    protected val mockServer = MockWebServer()
    protected var mockClient = DefaultOpenShiftClient(mockServer.url("/").toString())
}