package no.skatteetaten.aurora.gorg.controller

import no.skatteetaten.aurora.gorg.service.ProjectService
import no.skatteetaten.aurora.gorg.service.TemporaryProject
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/projects")
class ProjectController(val projectService: ProjectService) {

    @DeleteMapping
    fun deleteProjects() {
        projectService.findTemporaryProjects(Instant.now())
                .filter { it.ttl.isNegative }
                .forEach { projectService.deleteProject(it) }
    }

    @GetMapping
    fun list(): List<TemporaryProject> {
        return projectService.findTemporaryProjects(Instant.now())
    }
}
