package no.skatteetaten.aurora.gorg.service

import java.time.Duration
import java.time.Instant



interface BaseResource {
    val ttl:Duration
    val name: String
    val removalTime: Instant
}

data class ApplicationDeploymentResource(
    override val name: String,
    val namespace: String,
    override val ttl: Duration,
    override val removalTime: Instant
) : BaseResource


data class BuildConfigResource(
    override val name: String,
    val namespace: String,
    override val ttl: Duration,
    override val removalTime: Instant
) : BaseResource


data class ProjectResource(
    override val name: String,
    override val ttl: Duration,
    override val removalTime: Instant
) : BaseResource

