package com.yjy.data.user.api

interface FcmTokenProvider {
    suspend fun getToken(): String
}