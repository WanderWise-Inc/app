package com.github.wanderwise_inc.app.model.location

object FakeItinerary {
    val TOKYO = Itinerary(
        userUid = "Sophia Reynolds",
        locations = listOf(
            Location(lat = 35.7101, long = 139.8107, title = "Tokyo Skytree", address = "1 Chome-1-2 Oshiage, Sumida City, Tokyo 131-0045, Japan", googleRating = 4.6f),
            Location(lat = 35.6764, long = 139.6993, title = "Meiji Shrine", address = "1-1 Yoyogikamizonocho, Shibuya City, Tokyo 151-8557, Japan", googleRating = 4.7f),
            Location(lat = 35.6654, long = 139.7707, title = "Tsukiji Fish Market", address = "5 Chome-2-1 Tsukiji, Chuo City, Tokyo 104-0045, Japan", googleRating = 4.5f),
            Location(lat = 35.7147, long = 139.7732, title = "Ueno Park", address = "5 Chome-20 Uenokoen, Taito City, Tokyo 110-0007, Japan", googleRating = 4.7f),
            Location(lat = 35.6852, long = 139.7103, title = "Shinjuku Gyoen National Garden", address = "11 Naitomachi, Shinjuku City, Tokyo 160-0014, Japan", googleRating = 4.7f),
            Location(lat = 35.6329, long = 139.8805, title = "Tokyo Disneyland", address = "1-1 Maihama, Urayasu, Chiba 279-0031, Japan", googleRating = 4.7f),
            Location(lat = 35.6619, long = 139.7041, title = "Shibuya Crossing", address = "2 Chome-2-1 Dogenzaka, Shibuya City, Tokyo 150-0043, Japan", googleRating = 4.8f),
            Location(lat = 35.7146, long = 139.7967, title = "Asakusa Temple", address = "2 Chome-3-1 Asakusa, Taito City, Tokyo 111-0032, Japan", googleRating = 4.6f),
            Location(lat = 35.6190, long = 139.7795, title = "Odaiba", address = "1 Chome-10 Aomi, Koto City, Tokyo 135-0064, Japan", googleRating = 4.7f)
        ),
        title = "Tokyo Highlights Tour: Explore Iconic Landmarks and Hidden Gems",
        tags = listOf(ItineraryTags.URBAN, ItineraryTags.CULTURAL),
        description = "Discover the beauty of Tokyo with this tour, visiting iconic landmarks such as Tokyo Skytree, Meiji Shrine, Tsukiji Fish Market, Ueno Park, Shinjuku Gyoen National Garden, Tokyo Disneyland, Shibuya Crossing, Asakusa Temple, and Odaiba, and more!",
        visible = true
    )

    val SAN_FRANCISCO = Itinerary(
        userUid = "Elena Cruz",
        locations = listOf(
            Location(lat = 37.80764569999999, long = -122.4195251, title = "San Francisco Bicycle Rentals", address = "425 Jefferson Street, San Francisco", googleRating = 4.5f),
            Location(lat = 37.7757292, long = -122.4119508, title = "Mike's Bikes of San Francisco", address = "1233 Howard Street, San Francisco", googleRating = 4.0f),
            Location(lat = 37.8060487, long = -122.4206076, title = "Blazing Saddles Bike Rentals & Tours", address = "2715 Hyde Street, San Francisco", googleRating = 4.1f),
            Location(lat = 37.7809098, long = -122.4117142, title = "Huckleberry Bicycles", address = "1073 Market Street, San Francisco", googleRating = 4.7f),
            Location(lat = 37.7665228, long = -122.4532875, title = "American Cyclery", address = "510 Frederick Street, San Francisco", googleRating = 4.5f)
        ),
        title = "San Francisco Bike Adventure: Explore the City on Two Wheels!",
        tags = listOf(ItineraryTags.URBAN, ItineraryTags.ACTIVE, ItineraryTags.ADVENTURE),
        description = "Explore the vibrant city of San Francisco with this exciting itinerary! Discover the best bicycle rental shops and cycling tours in the city, including San Francisco Bicycle Rentals, Mike's Bikes of San Francisco, Blazing Saddles Bike Rentals & Tours, Huckleberry Bicycles, and American Cyclery. Whether you're a casual rider or an avid cyclist, these top-rated locations offer a variety of bike options and guided tours to suit every preference. Get ready to pedal through the iconic streets of San Francisco and experience its stunning scenery and lively atmosphere like never before!",
        visible = true
    )

    val SWITZERLAND = Itinerary(
        userUid = "Liam Bennett",
        locations = listOf(
            Location(lat = 46.0207, long = 7.7491, title = "Zermatt", address = "Zermatt, Switzerland"),
            Location(lat = 46.8037, long = 8.2275, title = "Swiss Alps", address = "Swiss Alps, Switzerland"),
            Location(lat = 46.2044, long = 6.1432, title = "Lake Geneva", address = "Lake Geneva, Switzerland"),
            Location(lat = 46.6865, long = 7.8635, title = "Interlaken", address = "Interlaken, Switzerland"),
            Location(lat = 47.3769, long = 8.5417, title = "Zurich", address = "Zurich, Switzerland")
        ),
        title = "Swiss Adventure: Explore the Best of Switzerland",
        tags = listOf(ItineraryTags.ADVENTURE, ItineraryTags.NATURE, ItineraryTags.WILDLIFE),
        description = "Embark on an unforgettable journey through Switzerland's breathtaking landscapes and exhilarating experiences. From skiing in Zermatt to paragliding in Interlaken, and indulging in chocolate tasting in Zurich, this tour offers a perfect blend of adventure and relaxation in one of Europe's most picturesque destinations.",
        visible = true
    )
}