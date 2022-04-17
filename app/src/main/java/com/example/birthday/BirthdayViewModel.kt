package com.example.birthday

import androidx.lifecycle.*
import com.example.birthday.data.Birthday
import com.example.birthday.data.BirthdayDao
import kotlinx.coroutines.launch

class BirthdayViewModel(private val birthdayDao: BirthdayDao) : ViewModel() {

    val allBirthdays: LiveData<List<Birthday>> = birthdayDao.getAll().asLiveData()

    fun saveBirthday(
        name: String,
        day: Int,
        month: Int,
        year: Int,
        message: String
    ) {
        val bday = getNewBirthday(name, day, month, year, message)
        insert(bday)
    }

    private fun updateBirthday(birthday: Birthday) {
        viewModelScope.launch {
            birthdayDao.update(birthday)
        }
    }

    fun updateBirthday(
        id: Int,
        name: String,
        message: String,
        day: Int,
        month: Int,
        year: Int
    ) {
        val bday = Birthday(
            id = id,
            name = name,
            message = message,
            dateDay = day,
            dateMonth = month,
            dateYear = year
        )

        updateBirthday(bday)
    }

    private fun getNewBirthday(
        name: String,
        day: Int,
        month: Int,
        year: Int,
        message: String
    ): Birthday {
        return Birthday(
            name = name,
            dateDay = day,
            dateMonth = month,
            dateYear = year,
            message = message
        )
    }

    fun getBirthday(id: Int): LiveData<Birthday> {
        return birthdayDao.getBirthday(id).asLiveData()
    }

    private fun insert(birthday: Birthday) {
        viewModelScope.launch {
            birthdayDao.insert(birthday)
        }
    }
}

class BirthdayViewModelFactory(private val birthdayDao: BirthdayDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BirthdayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BirthdayViewModel(birthdayDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
