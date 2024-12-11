package dev.veryniche.stitchcounter.data.models

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Serializable
data class Project(
    val id: Int? = null,
    val name: String,
    val elapsedTime: Long = 0L,
    val counters: List<Counter> = listOf(),
    val lastModified: Long = System.currentTimeMillis(),
) {

    val lastModifiedString: String
        get() = DateTimeFormatter.ofLocalizedDateTime(
            FormatStyle.MEDIUM
        )
            .withLocale( Locale.getDefault() )
            .withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(lastModified))
}
