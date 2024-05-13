package com.github.wanderwise_inc.app.disk

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.proto.location.ItineraryProto
import com.github.wanderwise_inc.app.proto.location.LocationProto
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

/** Serializer for the `ItineraryProto` protobuf */
object ItineraryProtoSerializer : Serializer<ItineraryProto> {
  override val defaultValue: ItineraryProto = ItineraryProto.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): ItineraryProto {
    try {
      return ItineraryProto.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto.", exception)
    }
  }

  override suspend fun writeTo(t: ItineraryProto, output: OutputStream) {
    t.writeTo(output)
  }
}

/** simple conversion class from protobuf representation to `Location` */
fun LocationProto.toModel(): Location {
  return Location(
      lat = lat, long = long, title = title, address = address, googleRating = googleRating)
}

/** simple conversion class from protobuf representation to `Itinerary` */
fun ItineraryProto.toModel(): Itinerary {
  val builder = Itinerary.Builder(userUid = userUid)
  locationsList.forEach { locationProto -> builder.addLocation(locationProto.toModel()) }
  tagsList.forEach { tag -> builder.addTag(tag) }
  builder.title = title
  builder.description = description
  builder.visible = visible
  builder.time = time
  builder.price = price
  builder.uid = uid
  return builder.build()
}
