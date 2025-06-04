package com.example.myapplication

import androidx.compose.runtime.Immutable
import com.example.myapplication.data.entities.Message
import com.example.myapplication.ui.model.ChatBotModel

@Immutable
class UiState(
    val chatMessages: List<Message> = emptyList(),
    val setting: Setting = Setting(),
    val errorMessage: String? = null,
)

@Immutable
data class Setting(
    val model: ChatBotModel.Model = ChatBotModel.DEFAULT_MODEL,
)
