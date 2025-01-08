package com.yjy.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yjy.data.database.model.NotificationEntity

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications ORDER BY createdDateTime DESC")
    fun pagingSource(): PagingSource<Int, NotificationEntity>

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM notifications")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationEntity>)

    @Query("SELECT id FROM notifications ORDER BY createdDateTime ASC LIMIT 1")
    suspend fun getOldestNotificationId(): Int?
}
