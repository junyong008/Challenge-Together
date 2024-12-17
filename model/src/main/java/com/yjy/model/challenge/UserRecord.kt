package com.yjy.model.challenge

data class UserRecord(
    val tryCount: Int,
    val successCount: Int,
    val resetCount: Int,
    val bestRecordInSeconds: Long,
)
