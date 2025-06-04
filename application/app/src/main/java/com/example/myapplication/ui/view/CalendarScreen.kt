package com.example.myapplication.ui.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.entities.User
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun BottomPager(
    modifier: Modifier = Modifier,
    date: LocalDate,
    user: User,
) {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 }) // Middle = Camera

    LaunchedEffect(date) {
        Log.d("BottomPager", "date: $date")
    }

    HorizontalPager(
        state = pagerState,
        flingBehavior = PagerDefaults.flingBehavior(pagerState),
        modifier = modifier.fillMaxSize()
    ) { page ->
        when (page) {
            0 -> ChatScreen(
                user = user,
                date = date
            )
            1 -> CameraScreen(
                user = user,
                date = date
            )
            2 -> StatsScreen(
                user = user,
                date = date
            )
        }
    }
}

@Composable
fun CalendarScreen(
    user: User,
    modifier: Modifier = Modifier,
) {
    val currentDate = remember { LocalDate.now() }
    val currentMonth = remember { YearMonth.now() }
    val startDate = remember { currentMonth.minusMonths(24).atDay(1) }
    val endDate = remember { currentMonth.plusMonths(24).atEndOfMonth() }
    val daysOfWeek = remember { daysOfWeek() }

    val calendarState = rememberWeekCalendarState(
        startDate = startDate,
        endDate = endDate,
        firstVisibleWeekDate = currentDate,
        firstDayOfWeek = daysOfWeek.first()
    )

    val selectedDay = remember { mutableStateOf<WeekDay?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 24.dp, horizontal = 16.dp)
    ) {
        Text(
            text = "Your Weekly Reflection",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        DaysOfWeekTitle(daysOfWeek)

        Spacer(modifier = Modifier.height(12.dp))

        WeekCalendar(
            state = calendarState,
            dayContent = {
                Day(
                    day = it,
                    selectedDay = selectedDay.value,
                    onClick = { clickedDay -> selectedDay.value = clickedDay }
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        BottomPager(
            date = selectedDay.value?.date?: currentDate,
            user = user
        )
    }
}

@Composable
fun Day(
    day: WeekDay,
    selectedDay: WeekDay? = null,
    onClick: (WeekDay) -> Unit
) {
    val today = remember { LocalDate.now() }
    val isSelected = day.date == selectedDay?.date
    val isToday = day.date == today

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    else -> Color.Transparent
                }
            )
            .clickable { onClick(day) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isToday -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onBackground
                }
            )
        )
    }
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
