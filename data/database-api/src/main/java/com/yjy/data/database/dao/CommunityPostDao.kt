package com.yjy.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.yjy.data.database.model.CommunityPostEntity
import com.yjy.data.database.model.CommunityPostType

@Dao
interface CommunityPostDao {

    @Query("SELECT * FROM community_posts WHERE type = :type ORDER BY id DESC")
    fun pagingSource(type: CommunityPostType): PagingSource<Int, CommunityPostEntity>

    @Query("DELETE FROM community_posts WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM community_posts WHERE type = :type")
    suspend fun deleteAllByType(type: CommunityPostType)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<CommunityPostEntity>)

    @RawQuery
    suspend fun updatePostByRawQuery(query: SupportSQLiteQuery): Int

    @Transaction
    suspend fun updatePost(post: CommunityPostEntity) {
        val query = SimpleSQLiteQuery(
            """
            UPDATE community_posts SET 
                content = ?, writerName = ?, writerBestRecordInSeconds = ?,
                commentCount = ?, likeCount = ?, writtenDateTime = ?, modifiedDateTime = ?
            WHERE id = ?
            """,
            arrayOf(
                post.content,
                post.writerName,
                post.writerBestRecordInSeconds,
                post.commentCount,
                post.likeCount,
                post.writtenDateTime,
                post.modifiedDateTime,
                post.id,
            ),
        )
        updatePostByRawQuery(query)
    }

    @Query("SELECT id FROM community_posts WHERE type = :type ORDER BY id ASC LIMIT 1")
    suspend fun getOldestPostId(type: CommunityPostType): Int?
}
