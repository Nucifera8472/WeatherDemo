package at.nuceria.weatherdemo.util

import org.junit.Assert.*
import org.junit.Test

class DateTimeExtensionsKtTest {
    private val epochTimeStamp = 1643392688L // 2022-01-28_17:58Z

    @Test
    fun epochToLocalTime() {
        val localTime = epochTimeStamp.epochToLocalTime("America/New_York")
        assertEquals(12, localTime?.hourOfDay)
    }

    @Test
    fun epochToLocalTime_InvalidTimeZone() {
        val localTime = epochTimeStamp.epochToLocalTime("UNKNOWN")
        assertNull(localTime)
    }

    @Test
    fun epochToDateTime() {
        val localTime = epochTimeStamp.epochToDateTime()
        assertEquals(17, localTime.hourOfDay)
        assertEquals(2022, localTime.year)
        assertEquals(1, localTime.monthOfYear)
        assertEquals(28, localTime.dayOfMonth)
    }

    @Test
    fun dateTimeTo24hString() {
        val time = epochTimeStamp.epochToDateTime()
        assertEquals("17:58", time.to24hTime())
    }

    @Test
    fun dateTimeTo12hString() {
        val time = epochTimeStamp.epochToDateTime()
        assertEquals("5.58 PM", time.to12hTime())
    }

    @Test
    fun dateTimeToLongDateString() {
        val time = epochTimeStamp.epochToDateTime()
        assertEquals("Jan 28", time.toLongDateString())
    }
}
