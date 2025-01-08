package com.yjy.data.user.impl.mapper

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun String.toLocalDateTime(): LocalDateTime =
    LocalDateTime.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

fun String.toLocalDateTimeOrNull(): LocalDateTime? {
    return try {
        LocalDateTime.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    } catch (e: DateTimeParseException) {
        null
    }
}
