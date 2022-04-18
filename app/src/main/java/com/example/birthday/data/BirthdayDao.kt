package com.example.birthday.data

import androidx.room.*
import com.example.birthday.data.Birthday
import kotlinx.coroutines.flow.Flow

@Dao
interface BirthdayDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(birthday: Birthday)

    @Delete
    suspend fun delete(birthday: Birthday)

    @Update
    suspend fun update(birthday: Birthday)

    @Query("SELECT * FROM birthday")
    fun getAll(): Flow<List<Birthday>>

    @Query("SELECT * FROM birthday WHERE id = :id")
    fun getBirthday(id: Int): Flow<Birthday>
}
