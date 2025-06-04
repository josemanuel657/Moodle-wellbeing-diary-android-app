package com.example.myapplication.data.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "image",
    foreignKeys = [ForeignKey(
        entity = Day::class,
        parentColumns = ["date"],
        childColumns = ["dayDate"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("dayDate")]
)
data class Image(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayDate: LocalDate,
    val uri: Uri
)

