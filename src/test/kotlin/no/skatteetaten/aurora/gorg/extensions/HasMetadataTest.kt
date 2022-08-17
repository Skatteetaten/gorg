package no.skatteetaten.aurora.gorg.extensions

import org.junit.jupiter.api.Test
import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import no.skatteetaten.aurora.gorg.BuildConfigDataBuilder

class HasMetadataTest {

    @Test
    fun `Should return null when is not a valid timestamp`() {
        val bc = BuildConfigDataBuilder(bcTtl = "not timestamp").build()

        val removalTime = bc.removalTime()
        assertThat(removalTime).isNull()
    }

    @Test
    fun `Should return null when removeAfter is null`() {
        val bc = BuildConfigDataBuilder(bcTtl = null).build()

        val removalTime = bc.removalTime()
        assertThat(removalTime).isNull()
    }

    @Test
    fun `Should return null when removeAfter is null string`() {
        val bc = BuildConfigDataBuilder(bcTtl = "null").build()

        val removalTime = bc.removalTime()
        assertThat(removalTime).isNull()
    }

    @Test
    fun `Should return not null when removeAfter is valid`() {
        val bc = BuildConfigDataBuilder().build()

        val removalTime = bc.removalTime()
        assertThat(removalTime).isNotNull()
    }
}
