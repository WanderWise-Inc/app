package com.github.wanderwise_inc.app.ui.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel

@Composable
fun BottomNavigationMenu(
    navigationActions: NavigationActions,
    bottomNavigationViewModel: BottomNavigationViewModel
) {
  val _selectedIndex by bottomNavigationViewModel.getSelected().observeAsState()
  var selectedIndex = 0

  if (_selectedIndex != null) {
    selectedIndex = _selectedIndex as Int
  }

  NavigationBar(modifier = Modifier.testTag("Bottom navigation bar")) {
    TOP_LEVEL_DESTINATIONS.forEachIndexed { index, dest ->
      NavigationBarItem(
          selected = index == selectedIndex,
          onClick = {
            // bottomNavigationViewModel.setSelected(index)
            navigationActions.navigateTo(dest)
          },
          icon = {
            Icon(
                painter = painterResource(id = dest.icon),
                contentDescription = null,
                tint = Color(0xFF191C1E),
                modifier =
                    Modifier.size(width = 64.dp, height = 32.dp)
                        .padding(horizontal = 20.dp, vertical = 4.dp))
          },
          label = {
            /*Text(
                text = stringResource(id = dest.textId),
                modifier = Modifier
                    .padding(1.dp),
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    //fontFamily = FontFamily(Font(R.font.roboto)),
                    fontWeight = FontWeight(600),
                    color = Color(0xFF191C1E),

                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                )
            )*/
          },
          modifier = Modifier.testTag(dest.route))
    }
  }
}
