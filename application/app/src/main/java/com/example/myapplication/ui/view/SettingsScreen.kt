package com.example.myapplication.ui.view

import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.entities.ChatBotMood
import com.example.myapplication.data.entities.ThemePreference
import com.example.myapplication.ui.viewmodel.SettingsViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScaffold(
    settingsViewModel: SettingsViewModel,
    content: @Composable (paddingValues: PaddingValues) -> Unit
) {

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

    Scaffold (
        modifier = Modifier.background(MaterialTheme.colorScheme.tertiary),
        containerColor = MaterialTheme.colorScheme.secondary

    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                flingBehavior = PagerDefaults.flingBehavior(pagerState)
            ) { page ->
                when (page) {
                    0 -> {
                        content(innerPadding)
                    }
                    1 -> {
                        SettingsScreen(
                            settingsViewModel = settingsViewModel
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel
) {

    val user by settingsViewModel.user.collectAsState()

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.secondary),
        containerColor = MaterialTheme.colorScheme.secondary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            TopBar(
                name = user.name,
                image = user.profilePicture
            )


            SettingsSection(title = "Personal Information") {
                InfoSection(
                    name = user.name,
                    setName = {
                        settingsViewModel.updateUser(
                            user.copy(name = it)
                        )
                    },
                    email = user.email,
                    setEmail = {
                        settingsViewModel.updateUser(
                            user.copy(email = it)
                        )
                    },
                    dateOfBirth = user.dateOfBirth,
                    setBirthday = {
                        settingsViewModel.updateUser(
                            user.copy(dateOfBirth = it)
                        )
                    },
                )

            }

            SettingsSection(title = "Reminders") {
                ReminderSettings(
                    remindersEnabled = user.remindersEnabled,
                    setReminderEnabled = {
                        settingsViewModel.updateUser(
                            user.copy(remindersEnabled = it)
                        )
                    },
                    time = user.remindersTime,
                    setTime = {
                        settingsViewModel.updateUser(
                            user.copy(remindersTime = it)
                        )
                    }
                )
            }

            SettingsSection(title = "Theme Preference") {
                ThemeSettings(
                    currentTheme = user.themePreference,
                    setTheme = {
                        settingsViewModel.updateUser(
                            user.copy(themePreference = it)
                        )
                    }
                )
            }

            SettingsSection(title = "ChatBot Mood") {
                ChatBotMoodSettings(
                    currentMood = user.chatBotMood,
                    setMood = {
                        settingsViewModel.updateUser(
                            user.copy(chatBotMood = it)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun TopBar(
    name: String,
    image: Uri,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
            ,

            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 70.sp),
                modifier = Modifier.padding(end = 36.dp)
            )

            // Image
            val painter = rememberAsyncImagePainter(
                model = image,
                onSuccess = { Log.d("ImageLoading", "Image loaded successfully!") },
                onError = { Log.d("ImageLoading", "Image loading failed!") }
            )

            Image(
                painter = painter,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
        }
    }
}


@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 8.dp)
        )

        AnimatedVisibility(visible = isExpanded) {
            Column {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoSection(
    name: String,
    setName: (String) -> Unit,
    email: String,
    setEmail: (String) -> Unit,
    dateOfBirth: LocalDate,
    setBirthday: (LocalDate) -> Unit,
) {

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dateOfBirth
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selected = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        setBirthday(selected)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = setName,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = setEmail,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dateOfBirth.format(DateTimeFormatter.ISO_DATE),
            onValueChange = {},
            label = { Text("Date of Birth") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
        )
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSettings(
    remindersEnabled: Boolean = true,
    setReminderEnabled: (Boolean) -> Unit,
    time: LocalTime = LocalTime.now(),
    setTime: (LocalTime) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = time.hour,
        initialMinute = time.minute,
        is24Hour = true,
    )

    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        setTime(LocalTime.of(timePickerState.hour, timePickerState.minute))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            TimeInput(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = MaterialTheme.colorScheme.background,
                    clockDialSelectedContentColor = MaterialTheme.colorScheme.primary,
                    clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    selectorColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface,
                    periodSelectorBorderColor = MaterialTheme.colorScheme.secondary,
                    periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                    periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surface,
                    periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                    timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surface,
                    timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Switch(
                checked = remindersEnabled,
                onCheckedChange = setReminderEnabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.surface,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettings(
    currentTheme: ThemePreference = ThemePreference.SYSTEM,
    setTheme : (ThemePreference) -> Unit,
) {

    Column {
        ThemePreference.entries.forEach { theme ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentTheme == theme,
                    onClick = {setTheme(theme)}
                )
                Text(text = theme.name)
            }
        }
    }
}

@Composable
fun ChatBotMoodSettings(
    currentMood: ChatBotMood = ChatBotMood.NEUTRAL,
    setMood: (ChatBotMood) -> Unit,
) {
    Column {
        ChatBotMood.entries.forEach { mood ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentMood == mood,
                    onClick = { setMood(mood) }
                )
                Text(text = mood.name)
            }
        }
    }
}
