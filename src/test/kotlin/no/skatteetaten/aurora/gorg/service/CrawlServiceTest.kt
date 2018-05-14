package no.skatteetaten.aurora.gorg.service

import assertk.assertions.hasSize
import no.skatteetaten.aurora.gorg.DeploymentConfigDataBuilder
import no.skatteetaten.aurora.gorg.ProjectDataBuilder
import org.junit.jupiter.api.Test
import java.time.Instant

class CrawlServiceTest : AbstractOpenShiftServerTest() {

    @Test
    fun `Find temporary applications`() {
        val dc = DeploymentConfigDataBuilder().build()
        openShiftServer.openshiftClient.inNamespace("namespace").deploymentConfigs().create(dc)
        val crawlService = CrawlService(openShiftServer.openshiftClient)

        val applications = crawlService.findTemporaryApplications(Instant.now())
        assertk.assert(applications).hasSize(1)
    }


    @Test
    fun `Find temporary projects`() {
        val project = ProjectDataBuilder().build()
        openShiftServer.openshiftClient.inNamespace("namespace").projects().create(project)
        val crawlService = CrawlService(openShiftServer.openshiftClient)

        val projects = crawlService.findTemporaryProjects(Instant.now())
        assertk.assert(projects).hasSize(1)
    }
}