package com.yjy.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.yjy.data.database.model.ChallengePostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengePostDao {

    @Query(
        """
        SELECT * FROM challenge_posts 
        WHERE challengeId = :challengeId 
        ORDER BY writtenDateTime DESC
    """,
    )
    fun pagingSource(challengeId: Int): PagingSource<Int, ChallengePostEntity>

    @Query(
        """
        SELECT * FROM challenge_posts 
        WHERE challengeId = :challengeId 
        ORDER BY writtenDateTime DESC LIMIT 1
    """,
    )
    fun getLatestPost(challengeId: Int): Flow<ChallengePostEntity?>

    @Query(
        """
        SELECT id FROM challenge_posts 
        WHERE challengeId = :challengeId 
        ORDER BY writtenDateTime ASC LIMIT 1
    """,
    )
    suspend fun getOldestPostId(challengeId: Int): Int?

    @Query(
        """
        SELECT id FROM challenge_posts 
        WHERE challengeId = :challengeId 
        ORDER BY writtenDateTime DESC LIMIT 1
    """,
    )
    suspend fun getLatestPostId(challengeId: Int): Int?

    @Query("DELETE FROM challenge_posts WHERE tempId = :tempId")
    suspend fun deleteByTempId(tempId: Int)

    @Query("DELETE FROM challenge_posts WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM challenge_posts")
    suspend fun deleteAll()

    @Query("DELETE FROM challenge_posts WHERE isSynced = 0")
    suspend fun deleteUnSynced()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(challengePosts: ChallengePostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(challengePosts: List<ChallengePostEntity>)

    @Transaction
    suspend fun replaceByTempId(post: ChallengePostEntity) {
        deleteByTempId(post.tempId)
        insert(post)
    }
}
