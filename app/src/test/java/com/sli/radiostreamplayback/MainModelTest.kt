package com.sli.radiostreamplayback

import com.sli.radiostreamplayback.main.model.ApiService
import com.sli.radiostreamplayback.main.model.MainModelImpl
import com.sli.radiostreamplayback.main.model.RadioStation
import com.sli.radiostreamplayback.main.model.SortType
import com.sli.radiostreamplayback.main.model.Tag
import com.sli.radiostreamplayback.main.model.TagsList
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

class MainModelTest {

    private lateinit var mainModel: MainModelImpl
    private lateinit var apiService: ApiService
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        mainModel = MainModelImpl(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getRadioList should return list of radio stations`() {
        val mockResponseBody = """
            {
                "data": [
                    {
                        "id": "1",
                        "description": "Rock station",
                        "name": "Rock FM",
                        "imgUrl": "http://example.com/rock.jpg",
                        "streamUrl": "http://example.com/rock",
                        "reliability": 80,
                        "popularity": 75.5,
                        "tags": ["rock"]
                    },
                    {
                        "id": "2",
                        "description": "Jazz station",
                        "name": "Jazz FM",
                        "imgUrl": "http://example.com/jazz.jpg",
                        "streamUrl": "http://example.com/jazz",
                        "reliability": 85,
                        "popularity": 65.0,
                        "tags": ["jazz"]
                    }
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponseBody)
        )

        val call = mainModel.getRadioList()
        val response = call.execute()

        assert(response.isSuccessful)
        val radioList = response.body()
        assertEquals(2, radioList?.radioList?.size)
        assertEquals("Rock FM", radioList?.radioList?.get(0)?.name)
        assertEquals("Jazz FM", radioList?.radioList?.get(1)?.name)
    }

    @Test
    fun `filterListByTags should return original list when no tags are selected`() {
        val radioStations = listOf(
            createRadioStation(id = "1", name = "Rock FM", tags = listOf("rock")),
            createRadioStation(id = "2", name = "Jazz FM", tags = listOf("jazz")),
            createRadioStation(id = "3", name = "Pop FM", tags = listOf("pop"))
        )
        val tagsList = TagsList(
            tagsList = listOf(
                Tag("rock", isSelected = false),
                Tag("jazz", isSelected = false),
                Tag("pop", isSelected = false)
            )
        )

        val result = mainModel.filterListByTags(radioStations, tagsList)

        assertEquals(radioStations, result)
    }

    @Test
    fun `filterListByTags should filter list based on selected tags`() {
        val radioStations = listOf(
            createRadioStation(id = "1", name = "Rock FM", tags = listOf("rock")),
            createRadioStation(id = "2", name = "Jazz FM", tags = listOf("jazz")),
            createRadioStation(id = "3", name = "PopRock FM", tags = listOf("pop", "rock")),
            createRadioStation(id = "4", name = "Classical FM", tags = listOf("classical"))
        )
        val tagsList = TagsList(
            tagsList = listOf(
                Tag("rock", isSelected = true),
                Tag("pop", isSelected = true),
                Tag("classical", isSelected = false)
            )
        )
        val expected = listOf(
            radioStations[0],
            radioStations[2]
        )

        val result = mainModel.filterListByTags(radioStations, tagsList)

        assertEquals(expected, result)
    }

    @Test
    fun `sortListBy should sort list by name`() {
        val radioStations = listOf(
            createRadioStation(name = "Zeta FM"),
            createRadioStation(name = "Alpha FM"),
            createRadioStation(name = "Delta FM")
        )
        val expected = listOf(
            radioStations[1],
            radioStations[2],
            radioStations[0]
        )

        val result = mainModel.sortListBy(radioStations, SortType.NAME)

        assertEquals(expected, result)
    }

    @Test
    fun `sortListBy should sort list by reliability ascending`() {
        val radioStations = listOf(
            createRadioStation(name = "Station1", reliability = 80),
            createRadioStation(name = "Station2", reliability = 60),
            createRadioStation(name = "Station3", reliability = 90)
        )
        val expected = listOf(
            radioStations[1],
            radioStations[0],
            radioStations[2]
        )

        val result = mainModel.sortListBy(radioStations, SortType.RELIABILITY_ASC)

        assertEquals(expected, result)
    }

    @Test
    fun `sortListBy should sort list by reliability descending`() {
        val radioStations = listOf(
            createRadioStation(name = "Station1", reliability = 80),
            createRadioStation(name = "Station2", reliability = 60),
            createRadioStation(name = "Station3", reliability = 90)
        )
        val expected = listOf(
            radioStations[2],
            radioStations[0],
            radioStations[1]
        )

        val result = mainModel.sortListBy(radioStations, SortType.RELIABILITY_DESC)

        assertEquals(expected, result)
    }

    @Test
    fun `sortListBy should sort list by popularity ascending`() {
        val radioStations = listOf(
            createRadioStation(name = "Station1", popularity = 70.0f),
            createRadioStation(name = "Station2", popularity = 75.0f),
            createRadioStation(name = "Station3", popularity = 65.0f)
        )
        val expected = listOf(
            radioStations[2],
            radioStations[0],
            radioStations[1]
        )

        val result = mainModel.sortListBy(radioStations, SortType.POPULARITY_ASC)

        assertEquals(expected, result)
    }

    @Test
    fun `sortListBy should sort list by popularity descending`() {
        val radioStations = listOf(
            createRadioStation(name = "Station1", popularity = 70.0f),
            createRadioStation(name = "Station2", popularity = 75.0f),
            createRadioStation(name = "Station3", popularity = 65.0f)
        )
        val expected = listOf(
            radioStations[1],
            radioStations[0],
            radioStations[2]
        )

        val result = mainModel.sortListBy(radioStations, SortType.POPULARITY_DESC)

        assertEquals(expected, result)
    }

    private fun createRadioStation(
        id: String = UUID.randomUUID().toString(),
        description: String = "Description",
        name: String,
        imgUrl: String = "http://example.com/image.jpg",
        streamUrl: String = "http://example.com/stream",
        reliability: Int = 80,
        popularity: Float = 75.0f,
        tags: List<String> = emptyList()
    ): RadioStation {
        return RadioStation(
            id = id,
            description = description,
            name = name,
            imgUrl = imgUrl,
            streamUrl = streamUrl,
            reliability = reliability,
            popularity = popularity,
            tags = tags
        )
    }
}