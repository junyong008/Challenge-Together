package com.yjy.core.network.datasource

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.yjy.core.common.network.HttpStatusCodes.CONFLICT
import com.yjy.core.common.network.HttpStatusCodes.OK
import com.yjy.core.common.network.NetworkResult
import com.yjy.core.network.adapter.NetworkResultCallAdapterFactory
import com.yjy.core.network.interceptor.SessionInterceptor
import com.yjy.core.network.service.ChallengeTogetherService
import com.yjy.core.network.util.SessionManager
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import retrofit2.Retrofit
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthDataSourceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var challengeTogetherService: ChallengeTogetherService
    private lateinit var authDataSource: AuthDataSourceImpl
    private lateinit var sessionManager: SessionManager
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        sessionManager = mockk(relaxed = true)

        val client = OkHttpClient.Builder()
            .addInterceptor(SessionInterceptor(sessionManager))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(NetworkResultCallAdapterFactory())
            .build()

        challengeTogetherService = retrofit.create(ChallengeTogetherService::class.java)
        authDataSource = AuthDataSourceImpl(challengeTogetherService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `signUp should save session ID from response to session manager`() = runTest {
        // Given
        val sessionId = "test-session-id"
        val mockResponse = MockResponse()
            .setResponseCode(OK)
            .addHeader("X-Session-ID", sessionId)
        mockWebServer.enqueue(mockResponse)

        // When
        authDataSource.signUp(
            nickname = "testUser",
            email = "test@example.com",
            password = "password123",
            kakaoId = "",
            googleId = "",
            naverId = "",
        )

        // Then
        coVerify { sessionManager.setSessionToken(sessionId) }
    }

    @Test
    fun `checkEmailDuplicate should handle conflict response`() = runTest {
        // Given
        val responseCode = CONFLICT
        val mockResponse = MockResponse()
            .setResponseCode(responseCode)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = authDataSource.checkEmailDuplicate("test@example.com")

        // Then
        assert(result is NetworkResult.Failure.HttpError)
        val errorResult = result as NetworkResult.Failure.HttpError
        assertEquals(responseCode, errorResult.code)
    }
}