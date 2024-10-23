package com.yjy.data.network.util

interface TimeDiffManager {
    suspend fun getTimeDiff(): Long
    suspend fun setTimeDiff(diff: Long)
}
