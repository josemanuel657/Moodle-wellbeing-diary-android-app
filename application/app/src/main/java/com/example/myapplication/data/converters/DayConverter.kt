package com.example.myapplication.data.converters

import androidx.room.TypeConverter
import java.time.LocalDate

class DayConverter {
    @TypeConverter
    fun localDateToString(date: LocalDate): String = date.toString()

    @TypeConverter
    fun stringToLocalDate(value: String): LocalDate = LocalDate.parse(value)
}
