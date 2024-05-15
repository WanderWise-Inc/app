package com.github.wanderwise_inc.app.model.location

object FakeLocation {
  val STATUE_OF_LIBERTY =
      Location(
          lat = 40.689253199999996,
          long = -74.04454817144321,
          title = "Statue of Liberty",
          address =
              "Flagpole Plaza, Manhattan Community Board 1, Manhattan, New York County, New York, 10004, United States",
          googleRating = 0.8714324f // this value correspond to importance in openstreetmap
          )

  val EMPIRE_STATE_BUILDING =
      Location(
          lat = 40.74844205,
          long = -73.98565890160751,
          title = "Empire State Building",
          address =
              "350, 5th Avenue, Manhattan Community Board 5, Manhattan, New York County, New York, 10118, United States",
          googleRating = 0.8515868f // this value correspond to importance in openstreetmap
          )
}
