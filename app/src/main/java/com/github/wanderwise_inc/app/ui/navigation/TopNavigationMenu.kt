package com.github.wanderwise_inc.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopNavigationMenu (
    navigationActions: NavigationActions,
    parent: String
) {
    var selectedIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    
    val tabList = when (parent) {
        Route.OVERVIEW -> OVERVIEW_LEVEL_DESTINATIONS
        Route.LIKED -> LIKED_LEVEL_DESTINATIONS
        else -> OVERVIEW_LEVEL_DESTINATIONS
    }

    TabRow(
        selectedTabIndex = selectedIndex,
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        tabList.forEachIndexed { index, dest ->
            Tab(
                text = {
                    Text(
                    text = stringResource(id = dest.textId),
                    modifier = Modifier
                        .padding(1.dp, 4.dp),
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        //fontFamily = FontFamily(Font(R.font.roboto)),
                        fontWeight = FontWeight(600),
                        color = Color(0xFF191C1E),

                        textAlign = TextAlign.Center,
                        letterSpacing = 0.5.sp,
                    ))
                },
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
                            .size(width = 32.dp, height = 16.dp)
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }
            )
        }
    }
}