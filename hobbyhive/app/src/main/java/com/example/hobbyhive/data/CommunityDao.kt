package com.example.hobbyhive.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.hobbyhive.model.ForumComment
import com.example.hobbyhive.model.ForumPost
import com.example.hobbyhive.model.HobbyGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface CommunityDao {
    // Forum Posts
    @Query("SELECT * FROM forum_posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<ForumPost>>

    @Query("SELECT * FROM forum_posts WHERE id = :postId")
    fun getPostById(postId: Long): Flow<ForumPost?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: ForumPost): Long

    @Update
    suspend fun updatePost(post: ForumPost)

    @Query("UPDATE forum_posts SET repliesCount = repliesCount + 1 WHERE id = :postId")
    suspend fun incrementRepliesCount(postId: Long)

    // Comments
    @Query("SELECT * FROM forum_comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun getCommentsForPost(postId: Long): Flow<List<ForumComment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: ForumComment)

    // Groups
    @Query("SELECT * FROM hobby_groups")
    fun getAllGroups(): Flow<List<HobbyGroup>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGroup(group: HobbyGroup)

    @Update
    suspend fun updateGroup(group: HobbyGroup)
}
