package no.skatteetaten.aurora.gorg.service

import assertk.assert
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotNull
import io.fabric8.openshift.client.DefaultOpenShiftClient
import no.skatteetaten.aurora.gorg.ApplicationDeploymentBuilder
import no.skatteetaten.aurora.gorg.BuildConfigDataBuilder

import no.skatteetaten.aurora.gorg.ProjectDataBuilder
import no.skatteetaten.aurora.gorg.extensions.createApplicationDeployment
import org.junit.jupiter.api.Test
import java.time.Instant

class CrawlerTest : AbstractOpenShiftServerTest() {

    @Test
    fun `Find temporary buildConfigs`() {
        val bc = BuildConfigDataBuilder().build()
        openShiftServer.openshiftClient.inNamespace("namespace").buildConfigs().create(bc)
        val crawlService = OpenShiftService(openShiftServer.openshiftClient)

        val applications = crawlService.findTemporaryBuildConfigs(Instant.now())
        assert(applications).hasSize(1)
        assert(applications[0].name).isEqualTo("name")
        assert(applications[0].namespace).isEqualTo("namespace")
        assert(applications[0].ttl.seconds).isGreaterThan(0)
        assert(applications[0].removalTime).isNotNull()
    }

    @Test
    fun `Find temporary projects`() {
        val project = ProjectDataBuilder().build()
        openShiftServer.openshiftClient.projects().create(project)
        val crawlService = OpenShiftService(openShiftServer.openshiftClient)

        val projects = crawlService.findTemporaryProjects(Instant.now())
        assert(projects).hasSize(1)
        assert(projects[0].name).isEqualTo("name")
        assert(projects[0].ttl.seconds).isGreaterThan(0)
        assert(projects[0].removalTime).isNotNull()
    }
}