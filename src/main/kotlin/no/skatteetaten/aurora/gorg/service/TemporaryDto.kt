package no.skatteetaten.aurora.gorg.service

import java.time.Duration
import java.time.Instant

data class TemporaryResource(val name: String, val namespace: String, val ttl: Duration, val removalTime: Instant, val resourceType:String)