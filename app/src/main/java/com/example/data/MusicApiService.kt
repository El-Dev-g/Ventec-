package com.example.data

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ITunesSearchResult(
    @Json(name = "results") val results: List<ITunesTrack>
)

@JsonClass(generateAdapter = true)
data class ITunesTrack(
    @Json(name = "trackId") val trackId: Long,
    @Json(name = "trackName") val trackName: String,
    @Json(name = "artistName") val artistName: String,
    @Json(name = "artworkUrl100") val artworkUrl: String?,
    @Json(name = "previewUrl") val previewUrl: String?
)

interface ITunesApiService {
    @GET("search")
    suspend fun searchTracks(
        @Query("term") term: String,
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 20
    ): ITunesSearchResult

    companion object {
        private const val BASE_URL = "https://itunes.apple.com/"

        fun create(): ITunesApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(ITunesApiService::class.java)
        }
    }
}
