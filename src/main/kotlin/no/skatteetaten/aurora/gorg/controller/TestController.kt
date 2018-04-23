package no.skatteetaten.aurora.gorg.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {

    @GetMapping("/api/hello")
    fun hello(): String {
        return "Hello"
    }
}