package com.example.myapplication.data.converters

import androidx.room.TypeConverter
import com.example.myapplication.data.entities.ChatBotMood
import com.example.myapplication.data.entities.ThemePreference
import java.time.LocalTime

class UserConverter {
    @TypeConverter
    fun stringToThemePreference(value: String): ThemePreference = ThemePreference.valueOf(value)

    @TypeConverter
    fun themePreferenceToString(pref: ThemePreference): String = pref.name

    @TypeConverter
    fun chatBotMoodToString(mood: ChatBotMood): String = mood.name

    @TypeConverter
    fun stringToChatBotMood(value: String): ChatBotMood = ChatBotMood.valueOf(value)

    @TypeConverter
    fun localTimeToString(time: LocalTime): String = time.toString()

    @TypeConverter
    fun stringToLocalTime(value: String): LocalTime = LocalTime.parse(value)

}