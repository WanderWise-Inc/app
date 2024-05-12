package com.github.wanderwise_inc.app.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.github.wanderwise_inc.app.model.profile.ProfileProto
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

/**
 * Serializes the `ProfilePersistent` class which is mapped to protobuf
 */
object ProfileProtoSerializer: Serializer<ProfileProto> {
  override val defaultValue: ProfileProto = ProfileProto.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): ProfileProto {
    try {
      return ProfileProto.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto.", exception)
    }
  }

  override suspend fun writeTo(t: ProfileProto, output: OutputStream) {
    t.writeTo(output)
  }
}
