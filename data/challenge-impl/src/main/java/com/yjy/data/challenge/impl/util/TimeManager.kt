package com.yjy.data.challenge.impl.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import java.time.LocalDateTime

interface TimeProvider {
    fun getCurrentTime(): LocalDateTime
    fun getBootTime(): Long
}

interface TimeManager {
    val timeChangedFlow: SharedFlow<Unit>
    val tickerFlow: Flow<LocalDateTime>
}
