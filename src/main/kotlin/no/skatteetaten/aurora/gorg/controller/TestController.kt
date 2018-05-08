package no.skatteetaten.aurora.gorg.controller

import no.skatteetaten.aurora.gorg.service.CrawlService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(val crawler: CrawlService) {

    @GetMapping("/api/delete")
    fun delete() {
        crawler.deleteProjects()
    }

    @GetMapping("/api/test")
    fun test() {
        crawler.test()
    }

}
