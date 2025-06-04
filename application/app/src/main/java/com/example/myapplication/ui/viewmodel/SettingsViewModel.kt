package com.example.myapplication.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.dao.UserDao
import com.example.myapplication.data.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



class SettingsViewModel(
    private val userDao: UserDao
) : ViewModel() {

    private val _user = MutableStateFlow<User>(User.DEFAULT_USER_SETTINGS)
    val user: StateFlow<User> = _user

    init {
        viewModelScope.launch {
            val currentUser = userDao.getOrCreateUser()
            _user.value = currentUser
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            userDao.updateUser(user)
            _user.value = user
        }
    }


    companion object {
        fun getFactory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = DatabaseClient.getDatabase(context)
                    val userDao = db.userDao()
                    @Suppress("UNCHECKED_CAST")
                    return SettingsViewModel(userDao) as T
                }
            }
        }
    }
}
