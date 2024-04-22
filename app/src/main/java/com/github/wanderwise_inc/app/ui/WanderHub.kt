package com.github.wanderwise_inc.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.Tag
import com.github.wanderwise_inc.app.ui.itinerary.ItineraryBanner
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import java.lang.invoke.TypeDescriptor


@Composable
fun WanderHub(viewModel: MapViewModel) {
    //Gets all itineraries
    val itineraries by viewModel.getAllPublicItineraries().collectAsState(initial = listOf())


    //Scrollable Column that only composes items on Screen
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp, 8.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(10.dp),)
    {
        items(itineraries, key = {it}) {itinerary ->
            ItineraryBanner(itinerary)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0x00000000)
@Composable
//fun ItineraryBanner(itinerary: Itinerary) {
fun ItineraryBanner() {
    var hide by remember { mutableStateOf(false) }

    //for testing
    val itinerary = Foo("Wonderful London subway trip", "description",
        creator = "your mom", timeEstimate = 2, price = 200,
        tags = listOf(ItineraryTags.ACTIVE,
            ItineraryTags.ADVENTURE,
            ItineraryTags.NATURE,
            ItineraryTags.PHOTOGRAPHY))

        ElevatedCard(colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .fillMaxWidth()
                    .aspectRatio(1.34f)
                    .clip(RoundedCornerShape(16.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.underground_2725336_1280),
                    contentDescription = itinerary.description,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.55f)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp, 5.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.7f),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        //title of the Itinerary
                        Text(
                            text = itinerary.title,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(1.dp)
                        )

                        //additional fields
                        Text(
                            text = "Wandered by ${itinerary.creator}",
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(1.dp)
                        )

                        Text(
                            text = "Estimated time : ${itinerary.timeEstimate}",
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(1.dp)
                        )

                        Text(
                            text = "Average Expense : ${itinerary.price}$",
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(1.dp)
                        )

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center

                    ) {
                        itinerary.tags.forEach {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .fillMaxHeight()
                                    .padding(5.dp)
                            )
                            {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 2.sp,
                                    modifier = Modifier.padding(1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

}

//for testing
class Foo(var title: String, var description: String, var creator: String,
    var timeEstimate: Int, var price: Int, var tags : List<Tag>)

