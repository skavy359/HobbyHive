package com.example.hobbyhive.data

import com.example.hobbyhive.model.Hobby
import com.example.hobbyhive.model.HobbyCategory
import com.example.hobbyhive.model.HobbyStatus
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════
// Hobby Repository — Clean API for ViewModels
// ═══════════════════════════════════════════════════

class HobbyRepository(private val hobbyDao: HobbyDao) {

    fun getAllHobbies(): Flow<List<Hobby>> = hobbyDao.getAllHobbies()

    fun getHobbyById(id: Long): Flow<Hobby?> = hobbyDao.getHobbyById(id)

    suspend fun getHobbyByIdOnce(id: Long): Hobby? = hobbyDao.getHobbyByIdOnce(id)

    fun getHobbiesByStatus(status: HobbyStatus): Flow<List<Hobby>> =
        hobbyDao.getHobbiesByStatus(status)

    fun getHobbiesByCategory(category: HobbyCategory): Flow<List<Hobby>> =
        hobbyDao.getHobbiesByCategory(category)

    fun searchHobbies(query: String): Flow<List<Hobby>> = hobbyDao.searchHobbies(query)

    fun getHobbyCount(): Flow<Int> = hobbyDao.getHobbyCount()

    fun getHobbyCountByStatus(status: HobbyStatus): Flow<Int> =
        hobbyDao.getHobbyCountByStatus(status)

    fun getAverageRating(): Flow<Float?> = hobbyDao.getAverageRating()

    suspend fun insertHobby(hobby: Hobby): Long = hobbyDao.insert(hobby)

    suspend fun updateHobby(hobby: Hobby) = hobbyDao.update(
        hobby.copy(updatedAt = System.currentTimeMillis())
    )

    suspend fun deleteHobby(hobby: Hobby) = hobbyDao.delete(hobby)

    suspend fun deleteHobbyById(id: Long) = hobbyDao.deleteById(id)
}
