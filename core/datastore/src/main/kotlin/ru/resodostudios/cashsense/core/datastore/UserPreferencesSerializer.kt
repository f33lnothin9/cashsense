package ru.resodostudios.cashsense.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * An [androidx.datastore.core.Serializer] for the [UserPreferences] proto.
 */
class UserPreferencesSerializer @Inject constructor() : Serializer<UserPreferences> {

    override val defaultValue: UserPreferences = getCustomDefaultValues()

    override suspend fun readFrom(input: InputStream): UserPreferences =
        try {
            UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}

internal fun getCustomDefaultValues(): UserPreferences =
    UserPreferences.newBuilder()
        .setCurrency(DEFAULT_CURRENCY)
        .build()

private const val DEFAULT_CURRENCY = "USD"