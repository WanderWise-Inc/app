package com.github.wanderwise_inc.app.disk

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.github.wanderwise_inc.app.model.location.ItineraryProto
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object ItineraryProtoSerializer: Serializer<ItineraryProto> {
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
