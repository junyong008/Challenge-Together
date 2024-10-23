package com.yjy.data.network.interceptor

import com.yjy.data.network.util.TimeDiffManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal class TimeInterceptor @Inject constructor(
    private val timeDiffManager: TimeDiffManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        val localDateTime = LocalDateTime.now()

        // 현재 로컬의 시간을 헤더에 담아 전송. 챌린지 추가 등 서버에서 받아 오차를 보정하여 사용.
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedLocalTime = localDateTime.format(formatter)
        requestBuilder.addHeader("X-Client-Time", formattedLocalTime)
        val response = chain.proceed(requestBuilder.build())

        // 헤더에 담겨온 서버 시간과 로컬 시간의 차이를 계산 하여 저장.
        val serverTime = response.headers["X-Server-Time"]

        if (!serverTime.isNullOrBlank()) {
            val serverDateTime = LocalDateTime.parse(serverTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            val diff = Duration.between(serverDateTime, localDateTime).seconds
            updateTimeDiff(diff)
        }

        return response
    }

    private fun updateTimeDiff(timeDiff: Long) = runBlocking {
        timeDiffManager.setTimeDiff(timeDiff)
        Timber.d("updateTimeDiff: $timeDiff")
    }
}
