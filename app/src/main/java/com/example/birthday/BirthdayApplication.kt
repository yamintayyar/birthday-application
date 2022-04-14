package com.example.birthday

import android.app.Application
import com.example.birthday.data.BirthdayDatabase

class BirthdayApplication : Application() {
    val database: BirthdayDatabase by lazy { BirthdayDatabase.getDatabase(this) }
}