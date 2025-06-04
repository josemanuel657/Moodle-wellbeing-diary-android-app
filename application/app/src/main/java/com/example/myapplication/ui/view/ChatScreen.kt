package com.example.myapplication.ui.view

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.entities.Message
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.entities.Sender
import com.example.myapplication.data.entities.User
import com.example.myapplication.ui.viewmodel.DailyChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Composable
fun ChatScreen(user: User, date: LocalDate, modifier: Modifier = Modifier, ) {

    val context = LocalContext.current

    val dailyChatViewModel: DailyChatViewModel = viewModel(
        key = date.toString(),
        factory = DailyChatViewModel.getFactory(context = context, user = user, date = date)
    )

    val uiState = dailyChatViewModel.uiState.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface) { innerPadding ->
        Chat (
            chatMessages = uiState.value.chatMessages,
            onSendMessage = { message ->
                dailyChatViewModel.answer(message)
            },
            modifier = Modifier.padding(innerPadding)

        )
    }
}

@Composable
fun Chat(
    chatMessages: List<Message>,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var userInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 10.dp),
        ) {
            itemsIndexed(chatMessages) { _, message ->
                Column(modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
                ) {

                    if (message.sender == Sender.CHATBOT){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp)


                        ) {
                            Text(
                                text = "Bot: ${message.content}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.surface
                            )
                        }

                    }

                    if (message.sender == Sender.USER){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "You: ${message.content}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )

                            }
                        }
                    }


                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "Type your message...",
                        color = Color.White.copy(alpha = 0.5f)
                    )
                },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                )

            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (userInput.isNotBlank()) {
                    onSendMessage(userInput)
                    userInput = ""
                }
            }, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = Color.White
            )) {
                Text(
                    text = "Send",
                    color = Color.White
                )
            }
        }
    }
}