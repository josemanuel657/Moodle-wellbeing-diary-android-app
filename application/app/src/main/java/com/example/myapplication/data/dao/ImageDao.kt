package com.example.myapplication.data.dao

import android.net.Uri
import androidx.room.*
import com.example.myapplication.data.entities.Day
import com.example.myapplication.data.entities.Image
import com.example.myapplication.data.entities.Message
import java.time.LocalDate

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertImage(image: Image): Long

    @Query("""
        UPDATE image 
        SET uri = :uri
        WHERE id = :imageId
    """)
    suspend fun updateImageById(
        imageId: Long,
        uri: Uri
    )

    @Query("DELETE FROM image WHERE id = :imageId")
    suspend fun deleteImageById(imageId: Long)

    @Query("SELECT * FROM image WHERE dayDate = :dayDate ORDER BY id ASC")
    suspend fun getImagesForDay(dayDate: LocalDate): List<Image>

    @Query("DELETE FROM image WHERE dayDate = :dayDate")
    suspend fun deleteImagesForDay(dayDate: LocalDate)

    @Query("""
        SELECT image.* FROM image
        INNER JOIN day ON image.dayDate = day.date
        WHERE day.date BETWEEN :startDate AND :endDate
        ORDER BY day.date ASC
    """)
    suspend fun getImagesByDateRange(startDate: LocalDate, endDate: LocalDate): List<Image>
}
