package com.yjy.data.network.interceptor

import com.yjy.common.network.HttpStatusCodes
import com.yjy.data.network.util.TimeDiffManager
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

class TimeInterceptorTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var timeDiffManager: TimeDiffManager
    private lateinit var okHttpClient: OkHttpClient

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        timeDiffManager = mockk(relaxed = true)

        val timeInterceptor = TimeInterceptor(timeDiffManager)
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(timeInterceptor)
            .build()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should add local client time to request headers`() = runBlocking {
        // Given
        val mockResponse = MockResponse().setResponseCode(HttpStatusCodes.OK)
        mockWebServer.enqueue(mockResponse)

        // When
        val request = Request.Builder()
            .url(mockWebServer.url("/"))
            .build()

        okHttpClient.newCall(request).execute()

        // Then
        val recordedRequest = mockWebServer.takeRequest()
        val clientTimeHeader = recordedRequest.getHeader("X-Client-Time")
        val localDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        assertEquals(localDateTime.substring(0, 16), clientTimeHeader?.substring(0, 16)) // 앞부분 일치 확인 (초는 차이 가능성)
    }

    @Test
    fun `should update time difference when server time is present in response`() = runBlocking {
        // Given
        val localDateTime = LocalDateTime.now()
        val serverTime = localDateTime.minusSeconds(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        val mockResponse = MockResponse()
            .setResponseCode(HttpStatusCodes.OK)
            .setHeader("X-Server-Time", serverTime)
        mockWebServer.enqueue(mockResponse)

        // When
        val request = Request.Builder()
            .url(mockWebServer.url("/"))
            .build()

        okHttpClient.newCall(request).execute()

        // Then
        coVerify { timeDiffManager.setTimeDiff(10L) }
    }

    @Test
    fun `should not update time difference when server time is not present in response`() = runBlocking {
        // Given
        val mockResponse = MockResponse().setResponseCode(HttpStatusCodes.OK)
        mockWebServer.enqueue(mockResponse)

        // When
        val request = Request.Builder()
            .url(mockWebServer.url("/"))
            .build()

        okHttpClient.newCall(request).execute()

        // Then
        coVerify(exactly = 0) { timeDiffManager.setTimeDiff(any()) }
    }
}
