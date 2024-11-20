package com.yjy.data.network.service

import com.yjy.data.network.WebSocketClient
import com.yjy.data.network.request.AddChallengePostRequest
import com.yjy.data.network.response.ChallengePostResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import timber.log.Timber
import javax.inject.Inject

class DefaultChallengePostWebSocketService @Inject constructor(
    @WebSocketClient private val okHttpClient: OkHttpClient,
    private val baseUrl: String,
) : ChallengePostWebSocketService {

    private var webSocket: WebSocket? = null

    override fun connectAsFlow(challengeId: Int): Flow<ChallengePostResponse> = callbackFlow {
        disconnect()
        val request = Request.Builder()
            .url("${baseUrl.replace("https", "wss")}challenge-post/$challengeId")
            .build()

        webSocket = okHttpClient.newWebSocket(
            request,
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Timber.d("WebSocket onOpen: $response")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    if (text.contains("\"type\":\"ping\"")) {
                        webSocket.send(Json.encodeToString(mapOf("type" to "pong")))
                        return
                    }

                    runCatching {
                        val response = Json.decodeFromString<ChallengePostResponse>(text)
                        trySend(response)
                    }.onFailure { e ->
                        Timber.e(e, "Failed to parse message")
                        close(e)
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Timber.e("WebSocket onFailure: ${t.localizedMessage}")
                    close(t)
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    if (code != NORMAL_CLOSURE_STATUS) {
                        close(IllegalStateException("WebSocket closed: $reason"))
                    } else {
                        close()
                    }
                }
            },
        )

        awaitClose {
            disconnect()
        }
    }

    private fun disconnect() {
        Timber.d("WebSocket disconnect")
        webSocket?.close(NORMAL_CLOSURE_STATUS, "Normal closure")
        webSocket = null
    }

    override fun addPost(request: AddChallengePostRequest) {
        Timber.d("WebSocket addPost: $request")
        val json = Json.encodeToString(request)
        webSocket?.send(json)
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
}
