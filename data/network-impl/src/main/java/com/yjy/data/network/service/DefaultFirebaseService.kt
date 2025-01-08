package com.yjy.data.network.service

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject

class DefaultFirebaseService @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
) : FirebaseService {

    override fun getRemoteAppVersion(): Flow<String> = callbackFlow {
        val appVersionRef = firebaseDatabase.reference.child(CURRENT_APP_VERSION_KEY)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appVersion = snapshot.getValue<String>() ?: ""
                Timber.d("Remote app version changed: $appVersion, raw snapshot: $snapshot")
                trySend(appVersion)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Failed to get remote app version")
                close(error.toException())
            }
        }

        appVersionRef.addValueEventListener(listener)
        awaitClose {
            appVersionRef.removeEventListener(listener)
        }
    }

    override fun getMaintenanceEndTime(): Flow<String> = callbackFlow {
        val endTimeRef = firebaseDatabase.reference.child(MAINTENANCE_END_TIME_KEY)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val endTime = snapshot.getValue<String>() ?: ""
                Timber.d("Maintenance end time changed: $endTime, raw snapshot: $snapshot")
                trySend(endTime)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Failed to get maintenance end time")
                close(error.toException())
            }
        }

        endTimeRef.addValueEventListener(listener)
        awaitClose {
            endTimeRef.removeEventListener(listener)
        }
    }

    companion object {
        private const val CURRENT_APP_VERSION_KEY = "current_app_version"
        private const val MAINTENANCE_END_TIME_KEY = "maintenance_end_time"
    }
}
