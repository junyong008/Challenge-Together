package com.yjy.common.core.util

private const val MIN_EVENT_DELAY_MS = 500L

interface MultipleEventsCutter {
    fun processEvent(event: () -> Unit)

    companion object
}

fun MultipleEventsCutter.Companion.get(): MultipleEventsCutter =
    MultipleEventsCutterImpl()

private class MultipleEventsCutterImpl : MultipleEventsCutter {
    private val now: Long
        get() = System.currentTimeMillis()

    private var lastEventTimeMs: Long = 0

    override fun processEvent(event: () -> Unit) {
        if (now - lastEventTimeMs >= MIN_EVENT_DELAY_MS) {
            event.invoke()
        }
        lastEventTimeMs = now
    }
}
