package com.yjy.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.yjy.data.database.model.TogetherChallengeEntity

@Dao
interface TogetherChallengeDao {

    @Query("SELECT * FROM together_challenges WHERE category = :category ORDER BY id DESC")
    fun pagingSource(category: String): PagingSource<Int, TogetherChallengeEntity>

    @Query("SELECT * FROM together_challenges ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, TogetherChallengeEntity>

    @Query("DELETE FROM together_challenges WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM together_challenges")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(challenge: TogetherChallengeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(challenges: List<TogetherChallengeEntity>)

    @Transaction
    suspend fun replaceAll(challenges: List<TogetherChallengeEntity>) {
        deleteAll()
        insertAll(challenges)
    }

    @Query("SELECT id FROM together_challenges ORDER BY id ASC LIMIT 1")
    suspend fun getOldestChallengeId(): Int?
}
