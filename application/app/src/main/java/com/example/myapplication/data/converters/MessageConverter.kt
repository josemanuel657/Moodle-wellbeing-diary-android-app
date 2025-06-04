package com.example.myapplication.data.converters

import androidx.room.TypeConverter
import com.example.myapplication.data.entities.Sender

class MessageConverter {
    @TypeConverter
    fun fromSender(sender: Sender): String {
        return sender.name
    }

    @TypeConverter
    fun toSender(senderString: String): Sender {
        return Sender.valueOf(senderString)
    }
}