package com.yjy.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.yjy.data.database.model.ChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {

    @Query("SELECT * FROM challenges")
    fun getAll(): Flow<List<ChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(challenges: List<ChallengeEntity>)

    @Query("DELETE FROM challenges WHERE id NOT IN (:ids)")
    suspend fun deleteChallengesNotIn(ids: List<String>)

    @Transaction
    suspend fun syncChallenges(challenges: List<ChallengeEntity>) {
        insertAll(challenges)
        val ids = challenges.map { it.id }
        deleteChallengesNotIn(ids)
    }
}