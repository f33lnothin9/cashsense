package ru.resodostudios.cashsense.core.network.retrofit

import androidx.tracing.trace
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.resodostudios.cashsense.core.network.CsNetworkDataSource
import ru.resodostudios.cashsense.core.network.model.NetworkCurrencyExchangeRate
import javax.inject.Inject
import javax.inject.Singleton

private interface RetrofitCsNetworkApi {

    @GET(value = "{base}")
    suspend fun getCurrencyExchangeRate(
        @Path("base") baseCurrencyCode: String,
        @Query("target") targetCurrencyCode: String,
    ): NetworkResponse<NetworkCurrencyExchangeRate>
}

private const val CS_BASE_URL = "https://hexarate.paikama.co/api/rates/latest/"

/**
 * Wrapper for data provided from the [CS_BASE_URL]
 */
@Serializable
private data class NetworkResponse<T>(
    val data: T,
)

/**
 * [Retrofit] backed [CsNetworkDataSource]
 */
@Singleton
internal class RetrofitCsNetwork @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : CsNetworkDataSource {

    private val networkApi = trace("RetrofitCsNetwork") {
        Retrofit.Builder()
            .baseUrl(CS_BASE_URL)
            // We use callFactory lambda here with dagger.Lazy<Call.Factory>
            // to prevent initializing OkHttp on the main thread.
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType()),
            )
            .build()
            .create(RetrofitCsNetworkApi::class.java)
    }

    override suspend fun getCurrencyExchangeRate(
        baseCurrencyCode: String,
        targetCurrencyCode: String,
    ): NetworkCurrencyExchangeRate =
        networkApi.getCurrencyExchangeRate(baseCurrencyCode, targetCurrencyCode).data
}