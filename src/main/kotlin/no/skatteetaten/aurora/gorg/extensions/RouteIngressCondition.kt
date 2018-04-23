package no.skatteetaten.aurora.gorg.extensions

import io.fabric8.openshift.api.model.RouteIngressCondition

fun RouteIngressCondition.success(): Boolean = this.type == "Admitted" && this.status == "True"
