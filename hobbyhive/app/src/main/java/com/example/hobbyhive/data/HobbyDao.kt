package com.example.hobbyhive.data

import androidx.room.*
import com.example.hobbyhive.model.Hobby
import com.example.hobbyhive.model.HobbyCategory
import com.example.hobbyhive.model.HobbyStatus
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════
// Hobby DAO — CRUD + Flow-based queries
// ═══════════════════════════════════════════════════

@Dao
interface HobbyDao {

    @Query("SELECT * FROM hobbies ORDER BY updatedAt DESC")
    fun getAllHobbies(): Flow<List<Hobby>>

    @Query("SELECT * FROM hobbies WHERE id = :hobbyId")
    fun getHobbyById(hobbyId: Long): Flow<Hobby?>

    @Query("SELECT * FROM hobbies WHERE id = :hobbyId")
    suspend fun getHobbyByIdOnce(hobbyId: Long): Hobby?

    @Query("SELECT * FROM hobbies WHERE status = :status ORDER BY updatedAt DESC")
    fun getHobbiesByStatus(status: HobbyStatus): Flow<List<Hobby>>

    @Query("SELECT * FROM hobbies WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchHobbies(query: String): Flow<List<Hobby>>

    @Query("SELECT COUNT(*) FROM hobbies")
    fun getHobbyCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM hobbies WHERE status = :status")
    fun getHobbyCountByStatus(status: HobbyStatus): Flow<Int>

    @Query("SELECT AVG(rating) FROM hobbies WHERE rating > 0")
    fun getAverageRating(): Flow<Float?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hobby: Hobby): Long

    @Update
    suspend fun update(hobby: Hobby)

    @Delete
    suspend fun delete(hobby: Hobby)

    @Query("DELETE FROM hobbies WHERE id = :hobbyId")
    suspend fun deleteById(hobbyId: Long)

    @Query("SELECT * FROM hobbies WHERE category = :category ORDER BY updatedAt DESC")
    fun getHobbiesByCategory(category: HobbyCategory): Flow<List<Hobby>>
}
