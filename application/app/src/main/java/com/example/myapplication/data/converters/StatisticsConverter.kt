package com.example.myapplication.data.converters

import androidx.room.TypeConverter
import com.example.myapplication.data.entities.Mood

class StatisticsConverter {
    @TypeConverter
    fun fromMoodScores(value: Map<Mood, Float>): String =
        value.entries.joinToString(";") { "${it.key.name}:${it.value}" }

    @TypeConverter
    fun toMoodScores(value: String): Map<Mood, Float> =
        value.split(";").mapNotNull {
            val parts = it.split(":")
            if (parts.size == 2) {
                val mood = parts[0].toMoodOrNull()
                val score = parts[1].toFloatOrNull()
                if (mood != null && score != null) mood to score else null
            } else null
        }.toMap()


    private fun String.toMoodOrNull() = Mood.entries.find { it.name == this }
}
