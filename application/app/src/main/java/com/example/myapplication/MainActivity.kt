package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.systemBars
import com.example.myapplication.ui.view.CalendarScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.view.SettingsScaffold
import com.example.myapplication.ui.viewmodel.SettingsViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {

                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModel.getFactory(LocalContext.current)
                )

                val userSettings by settingsViewModel.user.collectAsState()

                Scaffold(
                    contentWindowInsets = WindowInsets.systemBars
                ) { innerPadding ->
                    Box(modifier =
                        Modifier.padding(innerPadding)
                            .background(MaterialTheme.colorScheme.surface))
                    {
                        SettingsScaffold(
                            settingsViewModel = settingsViewModel
                        ) {
                            padding ->
                            CalendarScreen(
                                modifier = Modifier.padding(padding),
                                user = userSettings,
                            )
                        }
                    }
                }
            }
        }
    }
}

