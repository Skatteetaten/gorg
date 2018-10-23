package no.skatteetaten.aurora.gorg.service

import java.time.Duration
import java.time.Instant

data class TemporaryApplicationDeployment(val name: String, val namespace: String, val ttl: Duration, val removalTime: Instant)
data class TemporaryProject(val name: String, val affiliation: String, val ttl: Duration, val removalTime: Instant)
data class TemporaryBuildConfig(val name: String, val namespace: String, val ttl: Duration, val removalTime: Instant)