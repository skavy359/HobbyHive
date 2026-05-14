package com.example.hobbyhive.data

import com.example.hobbyhive.model.Hobby
import com.example.hobbyhive.model.HobbyCategory
import com.example.hobbyhive.model.HobbyStatus
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════
// Hobby Repository — Clean API for ViewModels
// ═══════════════════════════════════════════════════

open class HobbyRepository(protected val hobbyDao: HobbyDao) {

    open fun getAllHobbies(): Flow<List<Hobby>> = hobbyDao.getAllHobbies()

    open fun getHobbyById(id: Long): Flow<Hobby?> = hobbyDao.getHobbyById(id)

    suspend fun getHobbyByIdOnce(id: Long): Hobby? = hobbyDao.getHobbyByIdOnce(id)

    fun getHobbiesByStatus(status: HobbyStatus): Flow<List<Hobby>> =
        hobbyDao.getHobbiesByStatus(status)

    fun getHobbiesByCategory(category: HobbyCategory): Flow<List<Hobby>> =
        hobbyDao.getHobbiesByCategory(category)

    open fun searchHobbies(query: String): Flow<List<Hobby>> = hobbyDao.searchHobbies(query)

    fun getHobbyCount(): Flow<Int> = hobbyDao.getHobbyCount()

    fun getHobbyCountByStatus(status: HobbyStatus): Flow<Int> =
        hobbyDao.getHobbyCountByStatus(status)

    fun getAverageRating(): Flow<Float?> = hobbyDao.getAverageRating()

    open suspend fun insertHobby(hobby: Hobby): Long = hobbyDao.insert(hobby)

    open suspend fun updateHobby(hobby: Hobby) = hobbyDao.update(
        hobby.copy(updatedAt = System.currentTimeMillis())
    )

    open suspend fun deleteHobby(hobby: Hobby) = hobbyDao.delete(hobby)

    suspend fun deleteHobbyById(id: Long) = hobbyDao.deleteById(id)
}
