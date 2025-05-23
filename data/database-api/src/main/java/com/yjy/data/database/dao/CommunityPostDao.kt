package com.yjy.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yjy.data.database.model.CommunityPostEntity
import com.yjy.data.database.model.CommunityPostType
import java.time.LocalDateTime

@Dao
interface CommunityPostDao {

    @Query("SELECT * FROM community_posts WHERE type = :type ORDER BY id DESC")
    fun pagingSource(type: CommunityPostType): PagingSource<Int, CommunityPostEntity>

    @Query("DELETE FROM community_posts WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM community_posts WHERE type = :type")
    suspend fun deleteAllByType(type: CommunityPostType)

    @Query("DELETE FROM community_posts WHERE id = :id AND type = :type")
    suspend fun deleteByType(id: Int, type: CommunityPostType)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<CommunityPostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: CommunityPostEntity)

    @Query(
        """
        UPDATE community_posts 
        SET content = :content,
            writerName = :writerName,
            writerBestRecordInSeconds = :writerBestRecordInSeconds,
            commentCount = :commentCount,
            likeCount = :likeCount,
            writtenDateTime = :writtenDateTime,
            modifiedDateTime = :modifiedDateTime
        WHERE id = :id
    """,
    )
    suspend fun updatePost(
        id: Int,
        content: String,
        writerName: String,
        writerBestRecordInSeconds: Long,
        commentCount: Int,
        likeCount: Int,
        writtenDateTime: LocalDateTime,
        modifiedDateTime: LocalDateTime,
    )

    @Query("SELECT id FROM community_posts WHERE type = :type ORDER BY id ASC LIMIT 1")
    suspend fun getOldestPostId(type: CommunityPostType): Int?
}
