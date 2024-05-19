package com.github.wanderwise_inc.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.R

// Set of Material typography styles to start with
val Typography =
    Typography(
        bodyLarge =
            TextStyle(
                fontFamily =
                    FontFamily(
                        Font(R.font.dm_sans_regular, FontWeight.Normal),
                        Font(R.font.dm_sans_medium, FontWeight.Medium),
                        Font(R.font.dm_sans_bold, FontWeight.Bold)),
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp))
