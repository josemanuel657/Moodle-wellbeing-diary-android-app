package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.entities.User

@Dao
interface UserDao {

    @Transaction
    suspend fun getOrCreateUser(): User {
        val user = getUser()
        return user ?: createDefaultUser()
    }

    @Query("SELECT * FROM user WHERE id = 1 LIMIT 1")
    suspend fun getUser(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    suspend fun createDefaultUser(): User {
        val user = User.DEFAULT_USER_SETTINGS
        insertUser(User.DEFAULT_USER_SETTINGS)
        return user
    }

    @Update
    suspend fun updateUser(user: User)
}
