package com.example.birthday

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.icu.util.TimeUnit
import android.os.Build
import android.os.SystemClock.sleep
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.work.*
import com.example.birthday.data.Birthday
import com.example.birthday.data.BirthdayDao
import com.example.birthday.worker.BirthdayNotificationWorker
import kotlinx.coroutines.launch
import java.sql.Time

class BirthdayViewModel(
    private val birthdayDao: BirthdayDao,
    private val application: BirthdayApplication
) : ViewModel() {

    val allBirthdays: LiveData<List<Birthday>> = birthdayDao.getAll().asLiveData()

    private val workManager = WorkManager.getInstance(application)

    @RequiresApi(Build.VERSION_CODES.N)
    fun checkBirthdays(birthdays: List<Birthday>) {

        val calendar: Calendar = Calendar.getInstance()
        val currentTime = calendar.get(Calendar.HOUR_OF_DAY)

        if (!birthdays.isNullOrEmpty()) { //only allows function to run if the database contains birthdays
            makeNotification(birthdays, currentTime)
        }
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun makeNotification(birthdays: List<Birthday>, time: Int) {

        val birthdaysData = mutableListOf<String>() //input data for the worker to work with
        val datesData = mutableListOf<Int>()

        for (i in birthdays.indices) { //populating the data lists
            birthdaysData.add(i, birthdays[i].name) //adding names
            datesData.add(
                i,
                ((birthdays[i].dateMonth + 1) * 30) + birthdays[i].dateDay
            ) //adding comparable dates
        }

        val data = Data.Builder()
            .putStringArray(BirthdayNotificationWorker.birthdays, birthdaysData.toTypedArray())
            .putIntArray(BirthdayNotificationWorker.dates, datesData.toIntArray())
            .build()

        var delay = 0

        if (time < 13) {
            delay = 13 - time
        } else if (time > 13) {
            delay = time + (24 - time) + 13 //makes WorkManager wait until 2 PM the next day
        } else if (time == 13) { //probably redundant, but just in case
            delay = 0
        }

        val reminder =
            PeriodicWorkRequestBuilder<BirthdayNotificationWorker>(
                1,
                java.util.concurrent.TimeUnit.DAYS,
                1,
                java.util.concurrent.TimeUnit.HOURS
            )
                .setInputData(data)
                .setInitialDelay(delay.toLong(), java.util.concurrent.TimeUnit.HOURS)
                .build()

        workManager.enqueueUniquePeriodicWork(
            "birthday_notification",
            ExistingPeriodicWorkPolicy.REPLACE, //this means that if the user opens the app when there is a birthday that day,
            //it will not send out a notification that day, as the next work will be executed the following day.

            //this issue could be circumvented if we execute a one time work request that sends a notification right then
            //if there is a birthday, and then execute the periodic work request that will wait until the next day to execute,
            //but that seems excessive, as a user who opens the app will then know that a birthday is that day, so a notification
            // is not really necessary.

            reminder
        )

        //TODO: save info about if a notif for a bday has been sent, and take that into account when deciding if you send notif's for bday's
    }

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

    fun deleteBirthday(birthday: Birthday) {
        viewModelScope.launch {
            birthdayDao.delete(birthday)
        }
    }
}

class BirthdayViewModelFactory(
    private val birthdayDao: BirthdayDao,
    private val application: BirthdayApplication
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BirthdayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BirthdayViewModel(birthdayDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
