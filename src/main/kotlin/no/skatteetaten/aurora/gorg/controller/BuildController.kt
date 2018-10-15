package no.skatteetaten.aurora.gorg.controller

import no.skatteetaten.aurora.gorg.service.BuildService
import no.skatteetaten.aurora.gorg.service.TemporaryBuild
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/builds")
class BuildController(val buildService: BuildService){

    @DeleteMapping
    fun deleteProjects() {
        buildService.findTemporaryBuilds(Instant.now())
            .filter { it.ttl.isNegative }
            .forEach { buildService.deleteBuild(it) }
    }

    @GetMapping
    fun list(): List<TemporaryBuild> {
        return buildService.findTemporaryBuilds(Instant.now())
    }

}