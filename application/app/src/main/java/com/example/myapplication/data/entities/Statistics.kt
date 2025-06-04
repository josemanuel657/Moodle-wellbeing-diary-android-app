package com.example.myapplication.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate


@Entity(
    tableName = "statistics",
    foreignKeys = [ForeignKey(
        entity = Day::class,
        parentColumns = ["date"],
        childColumns = ["dayDate"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Statistics(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayDate: LocalDate,
    val reflection: String? = null,
    val motivationalMessage: String? = null,
    val moodScores: Map<Mood, Float> = emptyMap()
)

enum class Mood {
    HAPPY,
    CALMED,
    ANGRY,
    MOTIVATED,
    SAD,
}