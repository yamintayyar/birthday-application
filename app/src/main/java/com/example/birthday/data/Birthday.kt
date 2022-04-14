package com.example.birthday.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "birthday")
data class Birthday(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val name: String,
    @ColumnInfo(name = "date_day") val dateDay: Int,
    @ColumnInfo(name = "date_month") val dateMonth: Int,
    @ColumnInfo(name = "date_year") val dateYear: Int,
    @ColumnInfo val message: String
)

