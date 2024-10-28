package com.yjy.data.datastore.impl

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.yjy.data.datastore.api.ChallengePreferences
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class ChallengePreferencesSerializer @Inject constructor() : Serializer<ChallengePreferences> {
    override val defaultValue: ChallengePreferences = ChallengePreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ChallengePreferences {
        try {
            return ChallengePreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ChallengePreferences, output: OutputStream) {
        t.writeTo(output)
    }
}