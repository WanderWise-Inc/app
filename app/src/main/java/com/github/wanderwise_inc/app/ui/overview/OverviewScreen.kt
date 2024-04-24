import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.ui.WanderHub
import com.github.wanderwise_inc.app.ui.home.HomeScreen
import com.github.wanderwise_inc.app.ui.home.SearchBar
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.navigation.Route
import com.github.wanderwise_inc.app.ui.navigation.TopNavigationMenu
import com.github.wanderwise_inc.app.ui.navigation.graph.OverviewNavGraph
import com.github.wanderwise_inc.app.ui.search.ChipsScreen
import com.github.wanderwise_inc.app.ui.search.Slider
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

@Composable
fun OverviewScreen(
    mapViewModel: MapViewModel,
    navController: NavHostController = rememberNavController()
) {
  // Text(text = "Welcome to the itinerary screen", Modifier.testTag("Itinerary Screen"))
  val navigator = NavigationActions(navController)
  Scaffold(
      topBar = {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()) {
            SearchBar()
            ChipsScreen()
            //Slider()
            //Text(text = "WHERE ARE WE")

            //TopNavigationMenu(navigator, Route.OVERVIEW)
            }
      },
      modifier = Modifier.testTag("Overview screen")) { innerPadding ->
        OverviewNavGraph(
            mapViewModel = mapViewModel, navController = navController, innerPadding = innerPadding)
      }
}
