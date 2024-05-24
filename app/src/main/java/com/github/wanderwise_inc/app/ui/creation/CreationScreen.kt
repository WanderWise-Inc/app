package com.github.wanderwise_inc.app.ui.creation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.navigation.ItineraryCreationNavigationMenu
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.navigation.graph.CreationNavGraph
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun CreationScreen(
    createItineraryViewModel: CreateItineraryViewModel,
    profileViewModel: ProfileViewModel,
    onFinished: () -> Unit,
    navController: NavHostController = rememberNavController(),
    imageRepository: ImageRepository
) {
    val userUid = profileViewModel.getUserUid()

    var isNewItineraryNull by remember {
        mutableStateOf(createItineraryViewModel.getNewItinerary() == null)
    }

    if (isNewItineraryNull) {
        NoNewItinerary {
            createItineraryViewModel.startNewItinerary(userUid)
            isNewItineraryNull = false
        }
    } else {
        Scaffold(
            topBar = {
                ItineraryCreationNavigationMenu(navigationActions = NavigationActions(navController))
            },
            modifier = Modifier.testTag(TestTags.NEW_CREATION_SCREEN)) { padding ->
            CreationNavGraph(
                createItineraryViewModel,
                navController,
                padding,
                profileViewModel,
                onFinished,
                imageRepository)
        }
    }
}

/** the screen that is displayed when the user isn't actively creating a mapscreen */
@Composable
fun NoNewItinerary(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
        Modifier.fillMaxSize()
            .testTag(TestTags.NO_NEW_CREATION_SCREEN)
            .fillMaxWidth()
            .height(100.dp)) {
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 5000)
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Text(
                    text = "You aren't currently creating an itinerary...",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(5.dp, 10.dp))

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onClick,
                ) {
                    Text(
                        text = "Press here to get started!",
                        color = MaterialTheme.colorScheme.onSecondary,
                        textAlign = TextAlign.Center,
                        modifier =
                        Modifier.padding(5.dp, 10.dp)
                            .testTag(TestTags.START_ITINERARY_CREATION_BUTTON))
                }
            }
        }
    }
}
