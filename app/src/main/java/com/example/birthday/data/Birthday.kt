package com.example.birthday.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.format.DateTimeFormatter

@Entity(tableName = "birthday")
data class Birthday(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val name: String,
    @ColumnInfo(name = "date_day") val dateDay: Int,
    @ColumnInfo(name = "date_month") val dateMonth: Int,
    @ColumnInfo(name = "date_year") val dateYear: Int,
    @ColumnInfo val message: String
)

@RequiresApi(Build.VERSION_CODES.O)
fun Birthday.getFormattedDate(dateDay: Int, dateMonth: Int): String {

    val month = when (dateMonth) {
        0 -> "January"
        1 -> "February"
        2 -> "March"
        3 -> "April"
        4 -> "May"
        5 -> "June"
        6 -> "July"
        7 -> "August"
        8 -> "September"
        9 -> "October"
        10 -> "November"
        11 -> "December"
        else -> "ERROR: Month not given"
    }

    val formattedDate = "$month $dateDay"

    return formattedDate
}
