package com.example.myapplication.ui.theme

import com.example.myapplication.R

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.unit.sp

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val sacramentoFont = GoogleFont("Sacramento")
private val aliceFont = GoogleFont("Alice")

val sacramento = FontFamily(
    Font(googleFont = sacramentoFont, fontProvider = provider)
)

val alice = FontFamily(
    Font(googleFont = aliceFont, fontProvider = provider)
)

val CozyTextColor = Color(0xFF50312F)
val CozyAccentDark = Color(0xFF743233)

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = sacramento,
        fontWeight = FontWeight.Normal,
        fontSize = 42.sp,
        color = CozyTextColor,
        lineHeight = 36.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = alice,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = CozyTextColor,
        lineHeight = 24.sp
    ),
    labelSmall = TextStyle(
        fontFamily = alice,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = CozyAccentDark,
        lineHeight = 16.sp
    )
)
