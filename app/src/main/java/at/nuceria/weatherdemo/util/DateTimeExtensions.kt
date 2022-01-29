package at.nuceria.weatherdemo.util

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.lang.Exception

/**
 * @return null if the zone ID is invalid
 */
fun Long.epochToLocalTime(zoneId: String): DateTime? = try {
    epochToDateTime().withZone(DateTimeZone.forID(zoneId))
} catch (e: Exception) {
    null
}

fun Long.epochToDateTime(): DateTime = DateTime(epochToMillis(), DateTimeZone.UTC)

fun Long.epochToMillis(): Long = this * 1000

fun DateTime.to24hTime(): String {
    val formatter: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")
    return formatter.print(this)
}

fun DateTime.to12hTime(): String {
    val formatter: DateTimeFormatter = DateTimeFormat.forPattern("KK.mm aa")
    return formatter.print(this)
}
