package no.skatteetaten.aurora.gorg.extensions

fun String.ensureStartWith(startWith: String, seperator: String = ""): String {
    if (this.startsWith(startWith)) {
        return this
    }
    return "$startWith$seperator$this"

}