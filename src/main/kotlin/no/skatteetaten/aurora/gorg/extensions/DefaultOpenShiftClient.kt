package no.skatteetaten.aurora.gorg.extensions

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.fabric8.kubernetes.api.model.ObjectMeta
import io.fabric8.kubernetes.client.KubernetesClientException
import io.fabric8.openshift.client.DefaultOpenShiftClient
import okhttp3.Request

/*
fun DefaultOpenShiftClient.deleteApplicationDeployment(namespace:String, name:String): ApplicationDeployment {
    val url =
        this.openshiftUrl.toURI().resolve("/apis/skatteetaten.no/v1/namespaces/$namespace/applicationdeployments/$name")
    return try {
        val request = Request.Builder().url(url.toString()).build() //set verb
        val response = this.httpClient.newCall(request).execute()
        jacksonObjectMapper().readValue(response.body()?.bytes(), ApplicationDeployment::class.java)
            ?: throw KubernetesClientException("Error occurred while fetching temporary application deployments")
    } catch (e: Exception) {
        throw KubernetesClientException("Error occurred while fetching temporary application deployments", e)
    }
}*/


fun DefaultOpenShiftClient.applicationDeploymentsTemporary(): List<ApplicationDeployment> {
    val url =
        this.openshiftUrl.toURI().resolve("/apis/skatteetaten.no/v1/applicationdeployments?labelSelector=ttl")
    return try {
        val request = Request.Builder().url(url.toString()).build()
        val response = this.httpClient.newCall(request).execute()
        jacksonObjectMapper().readValue(response.body()?.bytes(), ApplicationDeploymentList::class.java)
            ?.items
            ?: throw KubernetesClientException("Error occurred while fetching temporary application deployments")
    } catch (e: Exception) {
        throw KubernetesClientException("Error occurred while fetching temporary application deployments", e)
    }
}


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApplicationDeploymentList(
    val items: List<ApplicationDeployment> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApplicationDeployment(
    val kind: String = "ApplicationDeployment",
    val metadata: ObjectMeta,
    val apiVersion: String = "skatteetaten.no/v1"
)
