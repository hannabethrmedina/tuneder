package edu.utap.tuneder3.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Header
import retrofit2.http.POST


interface SpotifyApi {

    @GET("/v1/audio-features/{id}")
    suspend fun fetchAudioFeatures(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): AudioFeatureResponse

    data class AudioFeatureResponse(
        val acousticness: Float,
        val danceability: Float,
        val energy: Float,
        val instrumentalness: Float,
        val liveness: Float,
        val loudness: Float,
        val speechiness: Float,
        // val tempo: Float,
        val valence: Float,

    )

    @GET("/v1/recommendations")
    suspend fun fetchRecommendations2(
        @Header("Authorization") authHeader: String,
        @Query("limit") limit: Int,
        @Query("market") market: String,
        @Query("seed_artists") seedArtists: String,
        @Query("target_acousticness") targetAcousticness: Float,
        @Query("target_danceability") targetDanceability: Float,
        @Query("target_energy") targetEnergy: Float,
        @Query("target_instrumentalness") targetInstrumentalness: Float,
        @Query("target_liveness") targetLiveness: Float,
        @Query("target_loudness") targetLoudness: Float,
        @Query("target_speechiness") targetSpeechiness: Float,
        @Query("target_valence") targetValence: Float,
    ): RecommendationResponse


    @GET("/v1/recommendations")
    suspend fun fetchRecommendations(
        @Header("Authorization") authHeader: String,
        @Query("limit") limit: Int,
        @Query("market") market: String,
        @Query("seed_artists") seedArtists: String,
        ): RecommendationResponse


    data class RecommendationResponse(
        val tracks: List<Track>
    )

    data class Track(
        val name: String,
        val artists: List<Artist>,
        val album: Album,
        val id: String,
        val preview_url: String
    )

    data class Artist(
        val name: String,
        val id: String
    )

    data class Album(
        val name: String,
        val images: List<SpotifyImage>
    )

    data class SpotifyImage(
        val url: String
    )

    @GET("/v1/me")
    suspend fun fetchCurrentUser(
        @Header("Authorization") authHeader: String,
    ): UserResponse

    data class UserResponse(
        val id: String,
        val display_name: String,
        val images: List<SpotifyImage>
        )

    data class UserResponse2(
        val id: String?,
        val display_name: String?,
        val images: List<SpotifyImage>?,
        val error: ErrorResponse?
    )

    data class ErrorResponse(
        val status: Int,
        val message: String
    )

    @GET("/v1/users/{userId}")
    suspend fun fetchUser(
        @Header("Authorization") authHeader: String,
        @Path("userId") id: String
    ): UserResponse2

    @POST("/v1/users/{userId}/playlists")
    suspend fun createPlaylist(
        @Header("Authorization") authHeader: String,
        @Path("userId") id: String,
        @Body body: CreatePlaylistBody
    ): PlaylistResponse

    data class CreatePlaylistBody (
        val name: String,
        val description: String
    )

    data class PlaylistResponse(
        val id: String
    )

    @POST("/v1/playlists/{playlistId}/tracks")
    suspend fun addSongsToPlaylist(
        @Header("Authorization") authHeader: String,
        @Path("playlistId") id: String,
        @Body body: SongsToPlaylistBody
    ): SnapshotResponse

    data class SongsToPlaylistBody(
        val uris: List<String>
    )

    data class SnapshotResponse(
        val snapshot_id: String
    )


    companion object Factory {
        fun create(): SpotifyApi {
            val retrofit: Retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                // Must end in /!
                .baseUrl("https://api.spotify.com/")
                .build()
            return retrofit.create(SpotifyApi::class.java)
        }
    }
}