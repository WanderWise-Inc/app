package com.github.wanderwise_inc.app.ui.itinerary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.Tag
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

//HomeScreen.kt + Itinerary.kt + this

@Preview(showBackground = true, backgroundColor = 0x00000000)
@Composable
fun ItineraryBanner(itinerary: Itinerary, onClick : (Itinerary) -> Unit) {
//fun ItineraryBanner() {
//    var hide by remember { mutableStateOf(false) }

//    for testing
//    val itinerary = Foo("Wonderful London subway trip", "description",
//        creator = "your mom", timeEstimate = 2, price = 200,
//        tags = listOf(ItineraryTags.ACTIVE,
//            ItineraryTags.ADVENTURE,
//            ItineraryTags.NATURE,
//            ItineraryTags.PHOTOGRAPHY,
//            ItineraryTags.CULTURAL,
//            ItineraryTags.FOOD,
//            ItineraryTags.ROMANCE,
//            ItineraryTags.ADVENTURE))

    val imageId = R.drawable.underground_2725336_1280

    ElevatedCard(
        colors = CardDefaults
            .cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant,),
        elevation = CardDefaults
            .cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(13.dp),
//        onClick = {onClick(itinerary)}
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .fillMaxWidth()
                .aspectRatio(1.34f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //Image of the itinerary
            Image(
                painter = painterResource(id = imageId),
                contentDescription = itinerary.description,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.55f)
                    .clip(RoundedCornerShape(13.dp)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.6f)
                        .padding(10.dp, 8.dp, 4.dp, 15.dp),
                    verticalArrangement = Arrangement.SpaceAround
                ) {

                    //Primary Text Field
                    Text(
                        text = itinerary.title,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(5.dp)
                        //textDecoration = TextDecoration.Underline
                    )

                    //Secondary indicator fields
                    Text(
                        text = "Wandered by - ",
                        color = MaterialTheme.colorScheme.secondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp, 0.dp)
                    )

                    Text(
                        text = "Estimated time : - hours",
                        color = MaterialTheme.colorScheme.secondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp, 0.dp)
                    )

                    Text(
                        text = "Average Expense : -",
                        color = MaterialTheme.colorScheme.secondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp, 0.dp)
                    )

                }
                Column(modifier = Modifier
                    .weight(0.3f)
                    .fillMaxSize()
                    .padding(4.dp, 8.dp, 10.dp, 15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                        //Composes Tags of the itinerary
//                        val tagsJoined = itinerary.tags.joinToString(separator = " ")
//
//                        Text(
//                            tefillMaxHeight(0.5f)xt = tagsJoined,
//                            softWrap = true,
//                            color = MaterialTheme.colorScheme.secondary,
//                            fontFamily = FontFamily.Monospace,
//                            fontSize = 10.sp,
//                            modifier = Modifier.padding(1.dp)
//                        )
                    LazyHorizontalStaggeredGrid(
                        rows = StaggeredGridCells.Adaptive(20.dp),
                        horizontalItemSpacing = 10.dp,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        content = {
                            items(itinerary.tags){
                                Card(
                                    colors = CardDefaults
                                        .cardColors(containerColor = MaterialTheme.colorScheme.secondary,),
                                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max)
                                ) {
                                    Text(
                                        text = it,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().weight(0.4f).padding(2.dp))
                        


//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier.weight(0.1f)
//                        ) {
//                            val weight: Float = 1f / itinerary.tags.size
//                            itinerary.tags.forEach {
//                                Text(
//                                    text = it,
//                                    softWrap = false,
//                                    color = MaterialTheme.colorScheme.secondary,
//                                    fontFamily = FontFamily.Monospace,
//                                    fontSize = 10.sp,
//                                    modifier = Modifier.weight(weight).padding(2.dp)
//                                )
//                            }
//                        }
                    Column(
                        modifier = Modifier.fillMaxWidth().weight(0.5f),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        //Like Icon
                        Icon(
                            painter = painterResource(id = R.drawable.liked_icon),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier =
                            Modifier.size(width = 40.dp, height = 40.dp)
                        )

                        Text(
                            text = " - Likes",
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }
            }
        }
    }
}

//for testing
class Foo(var title: String, var description: String, var creator: String,
          var timeEstimate: Int, var price: Int, var tags : List<Tag>)

