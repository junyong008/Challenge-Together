package com.yjy.data.network.datasource

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.yjy.common.network.HttpStatusCodes.CONFLICT
import com.yjy.common.network.HttpStatusCodes.OK
import com.yjy.common.network.NetworkResult
import com.yjy.data.network.adapter.NetworkResultCallAdapterFactory
import com.yjy.data.network.interceptor.SessionInterceptor
import com.yjy.data.network.request.auth.EmailLoginRequest
import com.yjy.data.network.request.auth.SignUpRequest
import com.yjy.data.network.request.auth.VerifyRequest
import com.yjy.data.network.service.ChallengeTogetherService
import com.yjy.data.network.util.SessionManager
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

    // 세션을 받아오는 경우의 수: emailLogin, signUp, verifyCode
    @Test
    fun `emailLogin should save session ID when session id is in header`() = runTest {
        // Given
        val sessionId = "test-session-id"
        val mockResponse = MockResponse()
            .setResponseCode(OK)
            .addHeader("X-Session-ID", sessionId)
        mockWebServer.enqueue(mockResponse)

        // When
        authDataSource.emailLogin(
            EmailLoginRequest(
                email = "test@example.com",
                password = "password123",
            ),
        )

        // Then
        coVerify { sessionManager.setSessionToken(sessionId) }
    }

    @Test
    fun `signUp should save session ID when session id is in header`() = runTest {
        // Given
        val sessionId = "test-session-id"
        val mockResponse = MockResponse()
            .setResponseCode(OK)
            .addHeader("X-Session-ID", sessionId)
        mockWebServer.enqueue(mockResponse)

        // When
        authDataSource.signUp(
            SignUpRequest(
                nickname = "testUser",
                email = "test@example.com",
                password = "password123",
                kakaoId = "",
                googleId = "",
                naverId = "",
            ),
        )

        // Then
        coVerify { sessionManager.setSessionToken(sessionId) }
    }

    @Test
    fun `verifyCode should save session ID when session id is in header`() = runTest {
        // Given
        val sessionId = "test-session-id"
        val mockResponse = MockResponse()
            .setResponseCode(OK)
            .addHeader("X-Session-ID", sessionId)
        mockWebServer.enqueue(mockResponse)

        // When
        authDataSource.verifyCode(
            VerifyRequest(
                email = "test@example.com",
                verifyCode = "111111",
            ),
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
