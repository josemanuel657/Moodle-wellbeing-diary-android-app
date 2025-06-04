package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.entities.Message
import java.time.LocalDate

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessage(message: Message): Long

    @Query(
        """
        UPDATE message 
        SET content = :content
        WHERE id = :messageId
    """
    )
    suspend fun updateMessageById(
        messageId: Long,
        content: String,
    )

    @Query("DELETE FROM message WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: Long)

    @Query("SELECT * FROM message WHERE dayDate = :dayDate ORDER BY id ASC")
    suspend fun getMessagesForDay(dayDate: LocalDate): List<Message>

    @Query("DELETE FROM message WHERE dayDate = :dayDate")
    suspend fun deleteMessagesForDay(dayDate: LocalDate)
}

