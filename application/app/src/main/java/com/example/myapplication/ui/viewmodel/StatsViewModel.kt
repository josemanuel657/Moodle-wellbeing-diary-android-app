package com.example.myapplication.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.dao.DayDao
import com.example.myapplication.data.dao.StatisticsDao
import com.example.myapplication.ui.model.ChatBotModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.entities.Mood
import com.example.myapplication.data.entities.Statistics
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.dao.ChatDao
import com.example.myapplication.data.entities.Message
import com.example.myapplication.data.entities.User
import kotlinx.coroutines.flow.update

class StatsViewModel(
    private val user: User,
    private val chatBotModel: ChatBotModel,
    private val date: LocalDate,
    private val dayDao: DayDao,
    private val statisticsDao: StatisticsDao,
    private val chatDao: ChatDao,
    private val folder: String = "android.resource://com.example.myapplication/drawable",
    private val imageMoodMap: Map<Mood, Uri> = mapOf(
        Mood.HAPPY to "$folder/happy".toUri(),
        Mood.CALMED to "$folder/calmed".toUri(),
        Mood.ANGRY to "$folder/angry".toUri(),
        Mood.MOTIVATED to "$folder/motivated".toUri(),
        Mood.SAD to "$folder/sad".toUri()
    )
) : ViewModel() {

    private val _dailyHistory = MutableStateFlow<List<Message>>(emptyList())

    private val _dailyStatistics = MutableStateFlow<Statistics?>(null)
    val dailyStatistics: StateFlow<Statistics?> get() = _dailyStatistics

    private val _monthlyStatistics = MutableStateFlow<Map<LocalDate, Statistics?>>(emptyMap())
    val monthlyStatistics: StateFlow<Map<LocalDate, Statistics?>> get() = _monthlyStatistics

    private val _imageMoodUri = MutableStateFlow<Uri?>(null)
    val imageMoodUri: StateFlow<Uri?> get() = _imageMoodUri

    init {
        viewModelScope.launch {
            dayDao.getOrCreateDay(date)
            val history = chatDao.getMessagesForDay(date)
            _dailyHistory.update { history }
            if (history.size > 2) performDailyAnalysis()
        }
    }

    private suspend fun getDailyHistory() {
        val history = chatDao.getMessagesForDay(date)
        _dailyHistory.update { history }
    }


    fun performDailyAnalysis() {

        viewModelScope.launch {
            val existing = statisticsDao.getStatisticsForDay(date)
            if (existing == null || existing.reflection.isNullOrEmpty() || existing.motivationalMessage.isNullOrEmpty() || existing.moodScores.isEmpty()) {
                val reflection = chatBotModel.generateResponse(
                    chatBotModel.buildReflectionForDayPrompt(date, user, _dailyHistory.value)
                )

                val motivationalMessage = chatBotModel.generateResponse(
                    chatBotModel.buildMotivationalMessageForDayPrompt(
                        date,
                        user,
                        _dailyHistory.value
                    )
                )
                val moodScores = chatBotModel.extractMoodScores(
                    chatBotModel.generateResponse(
                        chatBotModel.buildMoodAnalysisPrompt(date, user, _dailyHistory.value)
                    )
                )

                Log.i("StatsViewModel", "Reflection: $reflection")
                Log.i("StatsViewModel", "Motivational Message: $motivationalMessage")
                Log.i("StatsViewModel", "Mood Scores: $moodScores")

                val newStatistics = Statistics(
                    dayDate = date,
                    reflection = reflection,
                    motivationalMessage = motivationalMessage,
                    moodScores = moodScores
                )

                statisticsDao.insertStatistics(newStatistics)
                _dailyStatistics.value = newStatistics
                getImageForDay(moodScores)
            }
            else {
                Log.i("StatsViewModel", "Existing statistics found for day: $existing")
                _dailyStatistics.value = existing
                getImageForDay(existing.moodScores) // TODO
            }
        }
    }

    private fun getImageForDay(moodScores: Map<Mood, Float>) {
        val mood = moodScores.maxByOrNull { it.value }?.key
        _imageMoodUri.value = mood?.let { imageMoodMap[it] }
    }

    companion object {
        fun getFactory(context: Context, date: LocalDate, user: User): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = DatabaseClient.getDatabase(context)
                    val chatBotModel = ChatBotModel(context)
                    @Suppress("UNCHECKED_CAST")
                    return StatsViewModel(user, chatBotModel, date, db.dayDao(), db.statisticsDao(), db.chatDao()) as T
                }
            }
        }
    }
}
