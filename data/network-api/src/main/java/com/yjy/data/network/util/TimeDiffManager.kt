package com.yjy.data.network.util

interface TimeDiffManager {
    suspend fun setTimeDiff(diff: Long)
}
