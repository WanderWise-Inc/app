package com.github.wanderwise_inc.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.wanderwise_inc.app.ui.TestTags

@Composable
fun ItineraryCreationNavigationMenu(
    navigationActions: NavigationActions,
) {
  var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

  TabRow(
      selectedTabIndex = selectedIndex,
      backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
      modifier = Modifier.testTag(TestTags.CREATION_SCREEN_NAV_BAR),
  ) {
    CREATION_STEPS_DESTINATIONS.forEachIndexed { index, dest ->
      Tab(
          selected = index == selectedIndex,
          onClick = {
            selectedIndex = index
            navigationActions.navigateTo(dest)
          },
          /*text = {
           */
          /*Text(
          text = stringResource(id = dest.textId),
          modifier = Modifier
              .padding(0.dp, 2.dp),
          style = TextStyle(
              fontSize = 9.sp,
              lineHeight = 16.sp,
              //fontFamily = FontFamily(Font(R.font.roboto)),
              fontWeight = FontWeight(600),
              color = Color(0xFF191C1E),

              textAlign = TextAlign.Center,
              letterSpacing = 0.5.sp,
          ))*/
          /*
          },*/
          icon = {
            Icon(
                painter = painterResource(id = dest.icon),
                contentDescription = null,
                tint = Color(0xFF191C1E),
                modifier = Modifier.size(30.dp).padding(2.dp))
          },
          modifier = Modifier.testTag(dest.route) // Keep to mock input click
          )
    }
  }
}
