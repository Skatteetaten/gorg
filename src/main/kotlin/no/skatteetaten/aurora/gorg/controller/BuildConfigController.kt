package no.skatteetaten.aurora.gorg.controller

import no.skatteetaten.aurora.gorg.service.BuildConfigService
import no.skatteetaten.aurora.gorg.service.TemporaryBuildConfig
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/buildConfigs")
class BuildConfigController(val buildConfigService: BuildConfigService){

    @DeleteMapping
    fun deleteBuildConfigs() {
        buildConfigService.findTemporaryBuildConfigs(Instant.now())
            .filter { it.ttl.isNegative }
            .forEach { buildConfigService.deleteBuildConfig(it) }
    }

    @GetMapping
    fun list(): List<TemporaryBuildConfig> {
        return buildConfigService.findTemporaryBuildConfigs(Instant.now())
    }

}