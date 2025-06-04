package com.example.myapplication.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "message",
    foreignKeys = [ForeignKey(
        entity = Day::class,
        parentColumns = ["date"],
        childColumns = ["dayDate"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayDate: LocalDate,
    val content: String,
    val sender: Sender,
    val timestamp: Long = System.currentTimeMillis()
)

enum class Sender {
    USER,
    CHATBOT
}
