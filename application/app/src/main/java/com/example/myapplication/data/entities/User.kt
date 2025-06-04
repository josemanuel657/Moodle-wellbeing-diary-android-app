package com.example.myapplication.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime
import android.net.Uri
import androidx.core.net.toUri

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val dateOfBirth: LocalDate,

    val profilePicture: Uri,

    val remindersEnabled: Boolean,
    val remindersTime: LocalTime,

    val themePreference: ThemePreference,

    val chatBotMood: ChatBotMood
){
    companion object {
        val DEFAULT_USER_SETTINGS = User(
            id = 1,
            name = "Jose",
            email = "jjimenez1@wpi.edu",
            profilePicture = "android.resource://com.example.myapplication/drawable/default_profile_picture".toUri(),
            dateOfBirth = LocalDate.of(2004, 3, 26),
            remindersEnabled = false,
            remindersTime = LocalTime.of(12, 0),
            themePreference = ThemePreference.SYSTEM,
            chatBotMood = ChatBotMood.NEUTRAL
        )
    }
}

enum class ChatBotMood {
    NEUTRAL,
    HAPPY,
    ANGRY,
}

enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK
}

