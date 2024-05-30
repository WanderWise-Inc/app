package com.github.wanderwise_inc.app.disk

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.github.wanderwise_inc.app.proto.location.SavedItineraries
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

/** Serializer for the `SavedItineraries` protobuf. */
object SavedItinerariesSerializer : Serializer<SavedItineraries> {
  override val defaultValue: SavedItineraries = SavedItineraries.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): SavedItineraries {
    try {
      return SavedItineraries.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto", exception)
    }
  }

  override suspend fun writeTo(t: SavedItineraries, output: OutputStream) {
    t.writeTo(output)
  }
}
