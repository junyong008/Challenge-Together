package com.yjy.model.challenge

data class ResetInfo(
    val isCompleted: Boolean,
    val resetRecords: List<ResetRecord>,
)
