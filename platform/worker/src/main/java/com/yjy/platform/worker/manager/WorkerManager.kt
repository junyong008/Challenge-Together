package com.yjy.platform.worker.manager

interface WorkerManager {
    fun startPeriodicCheck()
    fun stopPeriodicCheck()
    fun startTimeSync()
}
