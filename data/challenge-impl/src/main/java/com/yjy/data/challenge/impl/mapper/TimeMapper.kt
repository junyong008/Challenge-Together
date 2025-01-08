package com.yjy.data.challenge.impl.mapper

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

internal fun LocalDateTime.toRequestString(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return this.format(formatter)
}

internal fun String.toLocalDateTime(): LocalDateTime {
    return if (this == "0000-00-00 00:00:00") {
        LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC)
    } else {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        LocalDateTime.parse(this, formatter)
    }
}
