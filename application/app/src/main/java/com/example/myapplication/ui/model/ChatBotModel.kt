package com.example.myapplication.ui.model

import android.content.Context
import android.util.Log
import com.example.myapplication.data.entities.Message
import com.example.myapplication.data.entities.Mood
import com.example.myapplication.data.entities.Sender
import com.example.myapplication.data.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

class ChatBotModel(
    private val context: Context,
    private var options: Options = Options(),
) {
    class Options(
        var model: Model = DEFAULT_MODEL,
    )

    companion object {
        private const val TAG = "ChatBot"
        val DEFAULT_MODEL = Model.Gemini

        private val client = OkHttpClient()
    }

    fun setOptions(options: Options) {
        this.options = options
    }

    suspend fun generateResponse(prompt: JSONObject): String {
        return try {
            withContext(Dispatchers.IO) {
                val response = callGeminiApi(prompt)
                Log.i("ChatBotModel", "Response: $response")
                return@withContext response
            }
        } catch (e: Exception) {
            ""
        }
    }

    private fun callGeminiApi(prompt: JSONObject): String {
        val apiKey = "AIzaSyBbBDCnIs_8H4m_DwNRkJXA6RzyUkm8PPs"

        val contentsArray = prompt.getJSONArray("contents")

        val fullRequest = JSONObject().apply {
            put("contents", contentsArray)
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("candidateCount", 1)
                put("maxOutputTokens", 2048)
            })
        }

        Log.i("fullrequest", fullRequest.toString())

        val requestBody = fullRequest
            .toString()
            .toRequestBody("application/json".toMediaTypeOrNull())



        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()


        try {
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.e(TAG, "Server error: ${response.code}")
                val errorBody = response.body?.string() ?: "No response body"
                Log.e(TAG, "Error response body: $errorBody")
                return "Server error: ${response.code}. Response body: $errorBody"
            }


            val body = response.body?.string() ?: return "Empty response"
            val jsonObject = JSONObject(body)

            Log.i("prompt", prompt.toString())
            Log.i("body", body)

            val candidates = jsonObject.optJSONArray("candidates") ?: return "No candidates"
            if (candidates.length() == 0) return "No response"

            val parts = candidates.getJSONObject(0)
                .optJSONObject("content")
                ?.optJSONArray("parts") ?: return "No parts"

            if (parts.length() == 0) return "No parts"

            return parts.getJSONObject(0).optString("text", "No text")
        } catch (e: Exception) {
            Log.e(TAG, "API call failed: ${e.message}", e)
            return "API call failed: ${e.message}"
        }
    }


    fun buildDailyConversationPrompt(
        userInput: String,
        date: LocalDate,
        user: User,
        history: List<Message>
    ): JSONObject {
        val contentsArray = JSONArray()

        contentsArray.put(
            JSONObject().apply {
                put("role", "user")
                put(
                    "parts", JSONArray().put(
                        JSONObject().put(
                            "text", """
                You are the MOODLE Chat Companion, a warm, supportive assistant designed to help users reflect on their emotional well-being in a relaxed and conversational way.

                MOODLE is not just a diary—it's a trusted space where users can share how their day went, guided by gentle questions rather than intimidating ones. Your job is to make them feel safe, heard, and encouraged.

                Start by asking, variations of the following examples:
                "Hi ${user.name}, how did your day go today?" or "Hey ${user.name}, what’s something that made you smile today?"

                - Speak in a casual, friendly tone
                - Ask only one question at a time
                - Acknowledge user responses with empathy
                - Provide uplifting and thoughtful follow-ups
                - Use plain text
                 - If your mood tone is ANGRY make sure to talk angrily. If your mood tone is HAPPY make sure to talk positively and beautifully.
             
                User info:
                - Name: ${user.name}
                - Current chatbot mood tone to match: ${user.chatBotMood}
                - Date: $date
            """.trimIndent()
                        )
                    )
                )
            }
        )

        // Add chat history
        history.sortedBy { it.timestamp }.forEach { message ->
            contentsArray.put(
                JSONObject().apply {
                    put(
                        "role", when (message.sender) {
                            Sender.USER -> "user"
                            Sender.CHATBOT -> "model"
                        }
                    )
                    put("parts", JSONArray().put(JSONObject().put("text", message.content)))
                }
            )
        }

        if (userInput.isNotBlank()) {
            contentsArray.put(
                JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().put(JSONObject().put("text", userInput)))
                }
            )
        }

        return JSONObject().apply {
            put("contents", contentsArray)
        }
    }

    fun buildReflectionForDayPrompt(
        date: LocalDate,
        user: User,
        history: List<Message>
    ): JSONObject {
        val conversationText = history
            .sortedBy { it.timestamp }
            .joinToString(separator = "\n") { message ->
                val sender = if (message.sender == Sender.USER) "User" else "Companion"
                "$sender: ${message.content}"
            }

        val promptText = """
        You are the MOODLE Chat Companion, an assistant designed to help users reflect on their emotional journey in a supportive and compassionate way.

        Here’s the conversation from $date:

        $conversationText

        Please analyze the user's emotional journey and summarize it in a short, empathetic, and warm reflection. The reflection should:
        - Highlight key moments, emotions, or shifts in the user's day
        - Be gentle, avoid judgment, and offer acknowledgment (e.g., "You handled a lot today, thank you for sharing.")
        - Use plain text, no need to explain how you are analyzing
        - Avoid offering suggestions

        ONLY return the reflection. NO explanations, no intro, no formatting — just the reflection.
    """.trimIndent()

        return JSONObject().apply {
            put("contents", JSONArray().put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", promptText)))
            }))
        }
    }


    fun buildMotivationalMessageForDayPrompt(
        date: LocalDate,
        user: User,
        history: List<Message>
    ): JSONObject {
        val conversationText = history
            .sortedBy { it.timestamp }
            .joinToString(separator = "\n") { message ->
                val sender = if (message.sender == Sender.USER) "User" else "Companion"
                "$sender: ${message.content}"
            }

        val promptText = """
        You are the MOODLE Chat Companion. Your goal is to provide the user with a short, personalized motivational message based on their day.

        Here is the conversation from $date:

        $conversationText

        Please analyze the conversation and generate a 1-2 sentence motivational quote that:
        - Feels personalized and relevant to the user's experiences
        - Uplifts the user, based on their shared emotions and moments
        - Uses a friendly, casual tone
        - Does not include explanations, just the quote
        - Only return the quote, nothing else

        The user's name is ${user.name}, and their current mood tone is: ${user.chatBotMood}.
    """.trimIndent()

        return JSONObject().apply {
            put("contents", JSONArray().put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", promptText)))
            }))
        }
    }


    fun buildMoodAnalysisPrompt(
        date: LocalDate,
        user: User,
        history: List<Message>
    ): JSONObject {
        val conversationText = history
            .sortedBy { it.timestamp }
            .joinToString(separator = "\n") { message ->
                val sender = if (message.sender == Sender.USER) "User" else "Companion"
                "$sender: ${message.content}"
            }

        val promptText = """
        You are the MOODLE Chat Companion. Please analyze the following conversation from $date and return **only** a JSON object with the user's emotional state. The structure should be:

        {
          "HAPPY": <float between 0 and 1>,
          "CALMED": <float between 0 and 1>,
          "ANGRY": <float between 0 and 1>,
          "MOTIVATED": <float between 0 and 1>,
          "SAD": <float between 0 and 1>
        }

        The values should reflect the user's emotional state based on the conversation below. Do not include any explanations, comments, or extra text.

        Conversation:
        $conversationText

        User: ${user.name}
        Mood tone: ${user.chatBotMood}
    """.trimIndent()

        return JSONObject().apply {
            put("contents", JSONArray().put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", promptText)))
            }))
        }
    }


    fun extractMoodScores(response: String): Map<Mood, Float> {
        val scores = mutableMapOf<Mood, Float>()

        Log.i("parsing", response)

        try {
            val cleanedResponse = response.trim().removePrefix("```json").removeSuffix("```").trim()

            val json = JSONObject(cleanedResponse)
            scores[Mood.HAPPY] = json.optDouble("HAPPY", 0.0).toFloat()
            scores[Mood.CALMED] = json.optDouble("CALMED", 0.0).toFloat()
            scores[Mood.ANGRY] = json.optDouble("ANGRY", 0.0).toFloat()
            scores[Mood.MOTIVATED] = json.optDouble("MOTIVATED", 0.0).toFloat()
            scores[Mood.SAD] = json.optDouble("SAD", 0.0).toFloat()
        } catch (e: Exception) {
            Log.e("MoodAnalysis", "Error parsing mood scores: ${e.message}")
        }

        return scores
    }

    enum class Model {
        Gemini,
    }
}
