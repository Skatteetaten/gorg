package no.skatteetaten.aurora.gorg.controller

import io.fabric8.openshift.client.DefaultOpenShiftClient
import no.skatteetaten.aurora.gorg.extensions.deleteApplicationDeployment
import no.skatteetaten.aurora.gorg.service.DeleteService
import no.skatteetaten.aurora.gorg.service.OpenShiftService
import no.skatteetaten.aurora.gorg.service.TemporaryResource
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api")
class CrawlController(
    val openShiftService: OpenShiftService,
    val deleteService: DeleteService,
    val client: DefaultOpenShiftClient
) {

    @GetMapping("/projects")
    fun listProjects() = openShiftService.findTemporaryProjects(Instant.now())

    @DeleteMapping("/projects")
    fun deleteProjects() = openShiftService.findTemporaryProjects(Instant.now())
        .filter { it.ttl.isNegative }
        .forEach {
            deleteService.deleteResource(client, it) {
                client -> client.projects().withName(it.name).delete()
            }

        }

    @GetMapping("/buildConfigs")
    fun listBuildConfig() = openShiftService.findTemporaryBuildConfigs(Instant.now())

    @DeleteMapping("/buildConfigs")
    fun deleteBuildConfigs() = openShiftService.findTemporaryBuildConfigs(Instant.now())
        .filter { it.ttl.isNegative }
        .forEach {
            deleteService.deleteResource(client, it) { client ->
                client.buildConfigs().inNamespace(it.namespace).withName(it.name).delete()
            }
        }

    @GetMapping("/apps")
    fun listApplicationDeployments(): List<TemporaryResource> = openShiftService.findTemporaryApplicationDeployments(Instant.now())

    @DeleteMapping("/apps")
    fun deleteApplicationDeployments() = openShiftService.findTemporaryApplicationDeployments(Instant.now())
        .filter { it.ttl.isNegative }
        .forEach {
            deleteService.deleteResource(client, it) {
               client -> client.deleteApplicationDeployment(it.namespace, it.name)
            }
        }

}
