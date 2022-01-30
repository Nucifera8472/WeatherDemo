package at.nuceria.weatherdemo.data

import android.location.Location
import androidx.room.withTransaction
import at.nuceria.weatherdemo.data.TestData.currentWeatherResponse
import at.nuceria.weatherdemo.data.TestData.outdatedWeatherResponse
import at.nuceria.weatherdemo.data.local.AppDatabase
import at.nuceria.weatherdemo.data.local.CurrentWeatherDao
import at.nuceria.weatherdemo.data.local.DailyWeatherDao
import at.nuceria.weatherdemo.data.managers.WeatherRepository
import at.nuceria.weatherdemo.data.model.DailyWeatherData
import at.nuceria.weatherdemo.data.model.WeatherData
import at.nuceria.weatherdemo.data.remote.WeatherService
import at.nuceria.weatherdemo.data.remote.response.WeatherResponse
import at.nuceria.weatherdemo.data.local.toWeatherData
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

@ExperimentalCoroutinesApi
class WeatherRepositoryTest {

    private val mockService = mockk<WeatherService>()
    private val mockCurrentWeatherDao = mockk<CurrentWeatherDao>()
    private val mockDailyWeatherDao = mockk<DailyWeatherDao>()
    private val mockAppDatabase = mockk<AppDatabase>()
    private val location = Location("dummy").apply {
        latitude = 22.2
        longitude = 44.4
    }

    private val sut =
        WeatherRepository(mockService, mockCurrentWeatherDao, mockDailyWeatherDao, mockAppDatabase)

    @Before
    fun setUp() {
        mockkStatic("androidx.room.RoomDatabaseKt")

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { mockAppDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { mockCurrentWeatherDao.deleteAll() } just runs
        coEvery { mockDailyWeatherDao.deleteAll() } just runs
        coEvery { mockDailyWeatherDao.insertAll(any()) } just runs
        coEvery { mockCurrentWeatherDao.insertAll(any()) } just runs
    }

    @After
    fun tearDown() {

    }

    @Test
    fun `getWeather conditions=dbEmpty,apiSuccess flow=return Loading without data,fetch,save`() =
        runTest {
            // PREPARE
            val expectedWeatherData = currentWeatherResponse.toWeatherData()
            givenEmptyThenFilledDb(expectedWeatherData)
            givenApiResponse()

            // CALL TEST FUNCTION
            val weatherDataFlow = sut.getWeather(location)

            // ASSERTS
            val flowResponses = mutableListOf<Resource<out WeatherData?>>()
            weatherDataFlow.toList(flowResponses)
            // db is queried at start
            verifyDbQueried()
            // FLOW_1: loading state without data returned
            assertTrue(flowResponses[0] is Resource.Loading)
            assertNull(flowResponses[0].data) // db empty, null returned
            // data fetched from api and saved to db
            verifyApiFetch()
            verifyDataSaved(expectedWeatherData)
            // FLOW_2: success state with expected data returned
            assertTrue(flowResponses[1] is Resource.Success)
            assertEquals(expectedWeatherData, flowResponses[1].data) // saved data returned
        }

    @Test
    fun `getWeather conditions=recentCache flow=returnCache,noFetch`() = runTest {
        // PREPARE
        val expectedWeatherData = currentWeatherResponse.toWeatherData()
        givenFilledDb(expectedWeatherData)
        givenApiResponse()

        // CALL TEST FUNCTION
        val weatherDataFlow = sut.getWeather(location)

        // ASSERTS
        val flowResponses = mutableListOf<Resource<out WeatherData?>>()
        weatherDataFlow.toList(flowResponses)
        // db is queried at start
        verifyDbQueried()
        // FLOW_1: loading state without data returned
        assertTrue(flowResponses[0] is Resource.Success)
        assertEquals(expectedWeatherData, flowResponses[0].data) // saved data returned
        // no data fetched from api
        verify { mockService wasNot called }
    }

    @Test
    fun `getWeather conditions=cacheTooOld,apiSuccess flow=return Loading with Cache,fetch,save`() =
        runTest {
            // PREPARE
            val outDatedWeatherData = outdatedWeatherResponse.toWeatherData()
            val refreshedData = currentWeatherResponse.toWeatherData()
            givenApiResponse()
            // DB first returns the outdated data and after api refresh the new data
            every {
                mockDailyWeatherDao.getForecastDataFlow()
            } answers {
                flowOf(listOf(outDatedWeatherData.todayOverView) + outDatedWeatherData.forecastWeatherData)
            } andThen {
                flowOf(listOf(refreshedData.todayOverView) + refreshedData.forecastWeatherData)
            }
            every {
                mockCurrentWeatherDao.getCurrentWeatherFlow()
            } answers {
                flowOf(outDatedWeatherData.currentWeatherData)
            } andThen {
                flowOf(refreshedData.currentWeatherData)
            }

            // CALL TEST FUNCTION
            val weatherDataFlow = sut.getWeather(location)

            // ASSERTS
            val flowResponses = mutableListOf<Resource<out WeatherData?>>()
            weatherDataFlow.toList(flowResponses)
            // db is queried at start
            verifyDbQueried()
            // FLOW_1: loading state with old data returned
            assertTrue(flowResponses[0] is Resource.Loading)
            assertEquals(outDatedWeatherData, flowResponses[0].data) // saved data returned
            // data fetched from api and saved to db
            verifyApiFetch()
            verifyDataSaved(refreshedData)
            // FLOW_2: success state with expected data returned
            assertTrue(flowResponses[1] is Resource.Success)
            assertEquals(refreshedData, flowResponses[1].data) // saved data returned
        }

    @Test
    fun `getWeather conditions=dbEmpty,apiFailure flow=return Loading with without data,fetch`() =
        runTest {
            // PREPARE
            givenEmptyDb()
            givenApiError()

            // CALL TEST FUNCTION
            val weatherDataFlow = sut.getWeather(location)

            // ASSERTS
            val flowResponses = mutableListOf<Resource<out WeatherData?>>()
            weatherDataFlow.toList(flowResponses)
            // db is queried at start
            verifyDbQueried()
            // FLOW_1: loading state without data returned
            assertTrue(flowResponses[0] is Resource.Loading)
            assertNull(flowResponses[0].data) // db empty, null returned
            // data fetched from api and saved to db
            verifyApiFetch()
            // FLOW_2: success state with expected data returned
            assertTrue(flowResponses[1] is Resource.Error)
            assertNull(flowResponses[1].data) // saved data returned
            assertEquals(
                400,
                (flowResponses[1].error as HttpException).code()
            ) // api exception returned
        }

    @Test
    fun `getWeather conditions=cacheTooOld,apiFailure flow=return Loading with Cache,fetch`() =
        runTest {
            fail("NOT YET IMPLEMENTED")
        }

    private fun verifyDbQueried() {
        coVerify { mockCurrentWeatherDao.getCurrentWeatherFlow() }
        coVerify { mockDailyWeatherDao.getForecastDataFlow() }
    }

    private fun verifyApiFetch() {
        coVerify { mockService.getWeatherData(location.latitude, location.longitude, any(), any()) }
    }

    private fun verifyDataSaved(weatherData: WeatherData) {
        val insertedList = mutableListOf<DailyWeatherData>()
        insertedList.addAll(weatherData.forecastWeatherData)
        insertedList.add(weatherData.todayOverView)
        coVerify { mockCurrentWeatherDao.deleteAll() }
        coVerify { mockCurrentWeatherDao.insertAll(weatherData.currentWeatherData) }
        coVerify { mockDailyWeatherDao.deleteAll() }
        coVerify { mockDailyWeatherDao.insertAll(insertedList) }
    }

    private fun givenEmptyDb() {
        every {
            mockDailyWeatherDao.getForecastDataFlow()
        } returns flowOf(emptyList())

        every {
            mockCurrentWeatherDao.getCurrentWeatherFlow()
        } returns flowOf(null)
    }

    private fun givenEmptyThenFilledDb(weatherData: WeatherData) {
        every {
            mockDailyWeatherDao.getForecastDataFlow()
        } answers {
            flowOf(emptyList())
        } andThen {
            flowOf(listOf(weatherData.todayOverView) + weatherData.forecastWeatherData)
        }

        every {
            mockCurrentWeatherDao.getCurrentWeatherFlow()
        } answers {
            flowOf(null)
        } andThen {
            flowOf(weatherData.currentWeatherData)
        }
    }

    private fun givenFilledDb(weatherData: WeatherData) {
        every {
            mockDailyWeatherDao.getForecastDataFlow()
        } returns flowOf(listOf(weatherData.todayOverView) + weatherData.forecastWeatherData)

        every {
            mockCurrentWeatherDao.getCurrentWeatherFlow()
        } returns flowOf(weatherData.currentWeatherData)
    }

    private fun givenApiResponse() {
        coEvery {
            mockService.getWeatherData(any(), any(), any(), any())
        } returns currentWeatherResponse
    }

    private fun givenApiError() {
        coEvery {
            mockService.getWeatherData(any(), any(), any(), any())
        } throws HttpException(Response.error<WeatherResponse>(400, "".toResponseBody()))
    }
}
