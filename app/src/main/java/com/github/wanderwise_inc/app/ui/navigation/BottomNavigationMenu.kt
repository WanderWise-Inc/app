package com.github.wanderwise_inc.app.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController

@Composable
fun BottomNavigationMenu (
    navigationActions: NavigationActions
) {
    var selectedIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar {
        TOP_LEVEL_DESTINATIONS.forEachIndexed { index, dest ->
            NavigationBarItem(
                selected = index == selectedIndex,
                onClick = {
                    selectedIndex = index
                    navigationActions.navigateTo(dest)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = dest.icon),
                        contentDescription = null,
                        tint = Color(0xFF191C1E),
                        modifier = Modifier
                            .size(width = 64.dp, height = 32.dp)
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                },
                label = {
                    Text(
                        text = dest.name,
                        modifier = Modifier
                            .padding(4.dp),
                        style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            //fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(600),
                            color = Color(0xFF191C1E),

                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp,
                        )
                    )
                }
            )
        }
    }
}