package com.yjy.platform.widget.model

enum class ThemeType(val value: Int) {
    SYSTEM(0),
    LIGHT(1),
    DARK(2),
    ;

    companion object {
        fun from(value: Int?): ThemeType = entries.find { it.value == value } ?: SYSTEM
    }
}
