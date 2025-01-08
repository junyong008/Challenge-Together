package com.yjy.data.network.service

import kotlinx.coroutines.flow.Flow

interface FirebaseService {
    fun getRemoteAppVersion(): Flow<String>
    fun getMaintenanceEndTime(): Flow<String>
}
