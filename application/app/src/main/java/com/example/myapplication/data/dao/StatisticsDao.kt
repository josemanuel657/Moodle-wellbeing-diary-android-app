package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.myapplication.data.entities.Statistics
import java.time.LocalDate

@Dao
interface StatisticsDao {

    @Query("SELECT * FROM statistics WHERE dayDate = :date LIMIT 1")
    suspend fun getStatisticsForDay(date: LocalDate): Statistics?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatistics(statistics: Statistics)

    @Transaction
    @Query("""
        SELECT * FROM statistics
        WHERE dayDate BETWEEN :startDate AND :endDate
    """)

    suspend fun getMonthlyStatistics(startDate: LocalDate, endDate: LocalDate): List<Statistics>
}
