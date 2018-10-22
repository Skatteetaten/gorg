package no.skatteetaten.aurora.gorg.service

import java.time.Duration
import java.time.Instant

data class TemporaryApplication(val name: String, val namespace: String, val ttl: Duration, val removalTime: Instant)
data class TemporaryProject(val name: String, val affiliation: String, val ttl: Duration, val removalTime: Instant)
data class TemporaryBuild(val name: String, val affiliation: String, val ttl: Duration, val removalTime: Instant)