package com.yjy.platform.time

import kotlinx.coroutines.flow.Flow

interface TimeMonitor {
    val isAutoTime: Flow<Boolean>
}
