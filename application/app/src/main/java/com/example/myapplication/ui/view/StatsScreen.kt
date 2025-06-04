package com.example.myapplication.ui.view

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.entities.User
import com.example.myapplication.ui.viewmodel.StatsViewModel
import java.time.LocalDate
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.aay.compose.radarChart.RadarChart
import com.aay.compose.radarChart.model.NetLinesStyle
import com.aay.compose.radarChart.model.Polygon
import com.aay.compose.radarChart.model.PolygonStyle
import com.example.myapplication.data.entities.Mood
import com.example.myapplication.data.entities.Statistics

@Composable
fun StatsScreen(
    user: User,
    modifier: Modifier = Modifier,
    date: LocalDate,
) {
    val context = LocalContext.current

    val statsViewModel: StatsViewModel = viewModel(
        key = date.toString(),
        factory = StatsViewModel.getFactory(context = context, date = date, user = user)
    )

    val dailyStatistics by statsViewModel.dailyStatistics.collectAsState()
    val monthlyStatistics by statsViewModel.monthlyStatistics.collectAsState()
    val imageMoodUri by statsViewModel.imageMoodUri.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.secondary,
                            disabledContainerColor = MaterialTheme.colorScheme.secondary,
                            disabledContentColor = MaterialTheme.colorScheme.secondary,
                        ),
                        onClick = { statsViewModel.performDailyAnalysis() },

                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Perform Daily Analysis")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                DailyReflection(reflection = dailyStatistics?.reflection)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .padding(horizontal = 0.dp)
                        .fillMaxWidth()
                        .height(300.dp)

                ) {
                    RadarChartSample(dailyStatistics?.moodScores)
                }
            }

            imageMoodUri?.let {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    MoodImage(
                        imageUri = imageMoodUri!!,
                        motivationalMessage = dailyStatistics?.motivationalMessage
                    )
                }
            }



            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}



@Composable
fun DailyReflection(reflection: String?) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = reflection ?: "",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


@Composable
fun MoodImage(imageUri: Uri, motivationalMessage: String?) {
    val painter = rememberAsyncImagePainter(
        model = imageUri,
        onSuccess = { Log.d("ImageLoading", "Image loaded successfully!") },
        onError = { Log.d("ImageLoading", "Image loading failed!") }
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Image(
            painter = painter,
            contentDescription = "Mood image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = motivationalMessage ?: "Motivational message not available.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
    }
}


@Composable
fun RadarChartSample(moodScores: Map<Mood, Float>?) {
    if (moodScores.isNullOrEmpty()) {
        return
    }

    val radarLabels = moodScores.keys.map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } }
    val values = moodScores.values.map { it.toDouble() }

    val labelsStyle = TextStyle(
        color = Color.Black,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp
    )

    val scalarValuesStyle = TextStyle(
        color = Color.Black,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp
    )

    RadarChart(
        modifier = Modifier.fillMaxSize(),
        radarLabels = radarLabels,
        labelsStyle =  MaterialTheme.typography.bodyLarge,
        netLinesStyle = NetLinesStyle(
            netLineColor = Color.Black,
            netLinesStrokeWidth = 2f,
            netLinesStrokeCap = StrokeCap.Butt
        ),
        scalarSteps = 5,
        scalarValue = 1.0,
        scalarValuesStyle = scalarValuesStyle,
        polygons = listOf(
            Polygon(
                values = values,
                unit = "",
                style = PolygonStyle(
                    fillColor = Color(0xffc2ff86),
                    fillColorAlpha = 0.5f,
                    borderColor = Color(0xffe6ffd6),
                    borderColorAlpha = 0.5f,
                    borderStrokeWidth = 2f,
                    borderStrokeCap = StrokeCap.Butt,
                )
            )
        )
    )
}

