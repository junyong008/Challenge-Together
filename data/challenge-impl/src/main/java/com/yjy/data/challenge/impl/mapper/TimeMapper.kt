package com.yjy.data.challenge.impl.mapper

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.toRequestString(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return this.format(formatter)
}
