package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.myapplication.data.entities.Day
import java.time.LocalDate

@Dao
interface DayDao {

    @Query("SELECT * FROM day WHERE date = :date")
    suspend fun getDay(date: LocalDate): Day?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDay(day: Day): Long

    @Transaction
    suspend fun getOrCreateDay(date: LocalDate) {
        val existingDay = getDay(date)
        if (existingDay == null) {
            insertDay(Day(date = date))
        }
    }
}