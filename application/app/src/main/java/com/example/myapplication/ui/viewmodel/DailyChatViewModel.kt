package com.example.myapplication.ui.viewmodel

import  android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.UiState
import com.example.myapplication.ui.model.ChatBotModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.myapplication.data.dao.ChatDao
import com.example.myapplication.Setting
import com.example.myapplication.data.dao.DayDao
import com.example.myapplication.data.entities.Message
import com.example.myapplication.data.entities.Sender
import com.example.myapplication.data.entities.User
import java.time.LocalDate

class DailyChatViewModel(
    private val chatBotModel: ChatBotModel,
    private val chatDao: ChatDao,
    private val dayDao: DayDao,
    private val date: LocalDate,
    private val user: User
) : ViewModel() {

    private val _chatMessages = MutableStateFlow<List<Message>>(emptyList())
    val chatMessages: StateFlow<List<Message>> get() = _chatMessages

    private val _errorMessage = MutableStateFlow<Throwable?>(null)
    val errorMessage: StateFlow<Throwable?> get() = _errorMessage

    private val _setting = MutableStateFlow(Setting())
    val setting: StateFlow<Setting> get() = _setting

    val uiState: StateFlow<UiState> = combine(
        _chatMessages,
        _setting.filterNotNull(),
        _errorMessage
    ) { chatHistory, setting, error ->
        UiState(
            chatMessages = chatHistory,
            setting = setting,
            errorMessage = error?.message
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    init {
        viewModelScope.launch {
            dayDao.getOrCreateDay(date)

            val chats = chatDao.getMessagesForDay(date)
            _chatMessages.update { chats }

            if (chats.isEmpty()) {
                answer("")
            }
        }
    }

    fun answer(userMessage: String) {
        viewModelScope.launch {
            try {
                val prompt = chatBotModel.buildDailyConversationPrompt(
                    userMessage,
                    date,
                    user,
                    _chatMessages.value
                )

                val nextQuestion = chatBotModel.generateResponse(prompt)

                val botMsg = Message(
                    dayDate = date,
                    content = nextQuestion,
                    sender = Sender.CHATBOT,
                )

                viewModelScope.launch {
                    if (userMessage.isNotBlank()) {
                        val userMsg = Message(
                            dayDate = date,
                            content = userMessage,
                            sender = Sender.USER,
                        )
                        chatDao.insertMessage(userMsg)
                        _chatMessages.update { it + userMsg + botMsg }
                    } else {
                        _chatMessages.update { it + botMsg }
                    }

                    chatDao.insertMessage(botMsg)
                }

            } catch (e: Exception) {
                _errorMessage.emit(e)
            }
        }
    }

    companion object {
        fun getFactory(context: Context, date: LocalDate, user: User): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = DatabaseClient.getDatabase(context)
                    val chatBotModel = ChatBotModel(context)
                    @Suppress("UNCHECKED_CAST")
                    return DailyChatViewModel(chatBotModel, db.chatDao(), db.dayDao(), date, user) as T
                }
            }
        }
    }
}
