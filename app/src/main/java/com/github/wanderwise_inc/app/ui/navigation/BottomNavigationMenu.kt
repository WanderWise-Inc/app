package com.github.wanderwise_inc.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel

@Composable
fun BottomNavigationMenu(
    navigationActions: NavigationActions,
    bottomNavigationViewModel: BottomNavigationViewModel
) {

  val _selectedIndex by bottomNavigationViewModel.selected.observeAsState()
  val selectedIndex = _selectedIndex ?: 0

  NavigationBar(
      modifier = Modifier.testTag(TestTags.BOTTOM_NAV)) {
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
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier =
                        Modifier.size(width = 64.dp, height = 32.dp)
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                            .testTag(dest.testTag))
              },
              label = {
                Text(
                    text = stringResource(id = dest.textId),
                    modifier = Modifier.padding(1.dp),
                    style =
                        TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            // fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(600),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp,
                        ))
              },
              modifier = Modifier.testTag(dest.route),
          )
        }
      }
}
