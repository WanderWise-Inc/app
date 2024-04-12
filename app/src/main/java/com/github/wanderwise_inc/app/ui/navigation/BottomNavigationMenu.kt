package com.github.wanderwise_inc.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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

@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination> = TOP_LEVEL_DESTINATIONS,
    selectedItem: TopLevelDestination,
) {
  BottomNavigation(
      modifier =
          Modifier.width(412.dp)
              .height(80.dp)
              .background(color = Color(0xFFEDEEF0), shape = RoundedCornerShape(size = 0.dp))
              .padding(start = 8.dp, end = 8.dp)
              .testTag("Bottom navigation bar"),
  ) {
    Row(
        modifier = Modifier.background(color = Color(0xFFEDEEF0)),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalAlignment = Alignment.Top) {
          tabList.forEach { screen ->
            BottomNavigationItem(
                modifier =
                    Modifier.width(194.dp)
                        .height(80.dp)
                        .padding(top = 12.dp, bottom = 16.dp)
                        .testTag(screen.route + " button"),
                icon = {
                  Icon(
                      painter = painterResource(id = screen.icon),
                      contentDescription = null,
                      tint = Color(0xFF191C1E),
                      modifier =
                          Modifier.size(width = 64.dp, height = 32.dp)
                              .padding(horizontal = 20.dp, vertical = 4.dp))
                },
                label = {
                  Text(
                      text = screen.route,
                      modifier = Modifier.padding(4.dp),
                      style =
                          TextStyle(
                              fontSize = 12.sp,
                              lineHeight = 16.sp,
                              // fontFamily = FontFamily(Font(R.font.roboto)),
                              fontWeight = FontWeight(600),
                              color = Color(0xFF191C1E),
                              textAlign = TextAlign.Center,
                              letterSpacing = 0.5.sp,
                          ))
                }, // stringResource(id = screen.textId)) },
                selected = selectedItem == screen,
                onClick = { onTabSelect(screen) })
          }
        }
  }
}
