package com.example.myapplication.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplication.data.converters.DayConverter
import com.example.myapplication.data.converters.ImageConverter
import com.example.myapplication.data.converters.MessageConverter
import com.example.myapplication.data.converters.StatisticsConverter
import com.example.myapplication.data.converters.UserConverter
import com.example.myapplication.data.dao.ChatDao
import com.example.myapplication.data.dao.DayDao
import com.example.myapplication.data.dao.ImageDao
import com.example.myapplication.data.dao.StatisticsDao
import com.example.myapplication.data.dao.UserDao
import com.example.myapplication.data.entities.Day
import com.example.myapplication.data.entities.Image
import com.example.myapplication.data.entities.Message
import com.example.myapplication.data.entities.Statistics
import com.example.myapplication.data.entities.User

@Database(
    entities = [
        User::class,
        Day::class,
        Message::class,
        Image::class,
        Statistics::class
   ],
    version = 25
)
@TypeConverters(
    UserConverter::class,
    DayConverter::class,
    MessageConverter::class,
    ImageConverter::class,
    StatisticsConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun dayDao(): DayDao
    abstract fun chatDao(): ChatDao
    abstract fun imageDao(): ImageDao
    abstract fun statisticsDao(): StatisticsDao
}