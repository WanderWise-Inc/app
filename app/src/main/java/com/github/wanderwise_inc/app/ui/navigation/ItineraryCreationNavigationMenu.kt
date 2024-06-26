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
      backgroundColor = MaterialTheme.colorScheme.primaryContainer,
      modifier = Modifier.testTag(TestTags.CREATION_SCREEN_NAV_BAR),
  ) {
    CREATION_STEPS_DESTINATIONS.forEachIndexed { index, dest ->
      Tab(
          selected = index == selectedIndex,
          onClick = {
            selectedIndex = index
            navigationActions.navigateTo(dest)
          },
          icon = {
            Icon(
                painter = painterResource(id = dest.icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(30.dp).padding(2.dp))
          },
          modifier = Modifier.testTag(dest.testTag) // Keep to mock input click
          )
    }
  }
}
