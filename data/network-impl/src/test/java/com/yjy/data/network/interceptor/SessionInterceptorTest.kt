package com.yjy.data.network.interceptor

import com.yjy.common.network.HttpStatusCodes.OK
import com.yjy.common.network.HttpStatusCodes.SESSION_EXPIRED
import com.yjy.data.network.util.SessionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

class SessionInterceptorTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var sessionManager: SessionManager
    private lateinit var okHttpClient: OkHttpClient

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        sessionManager = mockk(relaxed = true)

        val sessionInterceptor = SessionInterceptor(sessionManager)
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(sessionInterceptor)
            .build()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should add session token to request headers if available`() = runBlocking {
        // Given
        val sessionToken = "test-session-id"
        coEvery { sessionManager.getSessionToken() } returns sessionToken
        val mockResponse = MockResponse()
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        // When
        val request = Request.Builder()
            .url(mockWebServer.url("/"))
            .build()

        okHttpClient.newCall(request).execute()

        // Then
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals(sessionToken, recordedRequest.getHeader("X-Session-ID"))
    }

    @Test
    fun `should clear session when unauthorized response is received`() = runBlocking {
        // Given
        coEvery { sessionManager.getSessionToken() } returns "test-session-id"
        val mockResponse = MockResponse()
            .setResponseCode(SESSION_EXPIRED)
        mockWebServer.enqueue(mockResponse)

        // When
        val request = Request.Builder()
            .url(mockWebServer.url("/"))
            .build()

        okHttpClient.newCall(request).execute()

        // Then
        coVerify { sessionManager.setSessionToken(null) }
    }

    @Test
    fun `should update session token when a new one is provided`() = runBlocking {
        // Given
        val newSessionToken = "new-session-id"
        coEvery { sessionManager.getSessionToken() } returns "old-session-id"
        val mockResponse = MockResponse()
            .setResponseCode(OK)
            .setHeader("X-Session-ID", newSessionToken)
        mockWebServer.enqueue(mockResponse)

        // When
        val request = Request.Builder()
            .url(mockWebServer.url("/"))
            .build()

        okHttpClient.newCall(request).execute()

        // Then
        coVerify { sessionManager.setSessionToken(newSessionToken) }
    }

    @Test
    fun `should not update session token if no token is provided in response`() = runBlocking {
        // Given
        coEvery { sessionManager.getSessionToken() } returns "test-session-id"
        val mockResponse = MockResponse()
            .setResponseCode(OK)
        mockWebServer.enqueue(mockResponse)

        // When
        val request = Request.Builder()
            .url(mockWebServer.url("/"))
            .build()

        okHttpClient.newCall(request).execute()

        // Then
        coVerify(exactly = 0) { sessionManager.setSessionToken(any()) }
    }
}
