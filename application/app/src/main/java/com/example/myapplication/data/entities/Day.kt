package com.example.myapplication.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "day")
data class Day(
    @PrimaryKey val date: LocalDate
)




