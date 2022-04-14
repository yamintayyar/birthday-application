package com.example.birthday.data

import androidx.room.*
import com.example.birthday.data.Birthday
import kotlinx.coroutines.flow.Flow

@Dao
interface BirthdayDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(birthday : Birthday)

    @Delete
    suspend fun delete(birthday: Birthday)

    @Query("SELECT * FROM Birthday")
    fun getAll() : Flow<List<Birthday>>

    //TODO: add more queries and commands (one to get a single birthday item)
}
