package no.skatteetaten.aurora.gorg.controller

import no.skatteetaten.aurora.gorg.service.CrawlService
import no.skatteetaten.aurora.model.ProjectInfo
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(val crawler: CrawlService) {

    @GetMapping("/api/projectname")
    fun hello(): List<String> {
        return crawler.findAllProjects()
    }

    @GetMapping("/api/ttl")
    fun ttl(): List<ProjectInfo> {
        return crawler.listTtl()
    }

    @GetMapping("/api/kim")
    fun kim() {
        crawler.testKim()
    }

}