package edu.utap.tuneder3.api

import edu.utap.tuneder3.Song
import edu.utap.tuneder3.Song_Features
import edu.utap.tuneder3.User


class SpotifyRepository(private val spotifyApi: SpotifyApi) {

    // Get next song for discover (swiping) view
    suspend fun getSong(token: String?, seed_artists: String): Song {
        val response : SpotifyApi.RecommendationResponse?
        var finalToken = ""
        if (token != null) {
            finalToken = "Bearer $token"
        }

        response = spotifyApi.fetchRecommendations(finalToken, 1, "US", seed_artists)
        return unpackSong(response)
    }

    private fun unpackSong(response: SpotifyApi.RecommendationResponse): Song {
        var result: Song? = null
        val track = response.tracks[0]

        val songTitle = track.name
        val artistName = track.artists[0].name
        val artistId = track.artists[0].id
        val id = track.id

        val albumImageUrl = track.album.images[0].url
        val rawPreviewUrl = track.preview_url
        var previewUrl = ""
        if (rawPreviewUrl != null) {
            previewUrl = rawPreviewUrl
        }

        result = Song(id, songTitle, artistName, artistId, albumImageUrl, previewUrl )

        return result
    }

    // Get suggested songs for playlist
    suspend fun getSongsForPlaylist(token: String?, seed_artists: String, audioFeatures: Song_Features): List<Song> {
        val response : SpotifyApi.RecommendationResponse?
        var numSongs = 20

        val targetAcousticness = audioFeatures.acousticness
        val targetDanceability = audioFeatures.danceability
        val targetEnergy = audioFeatures.energy
        val targetInstrumentalness = audioFeatures.instrumentalness
        val targetLiveness = audioFeatures.liveness
        val targetLoudness = audioFeatures.loudness
        val targetSpeechiness = audioFeatures.speechiness
        val targetValence = audioFeatures.valence

        var finalToken = ""
        if (token != null) {
            finalToken = "Bearer $token"
        }
        response = spotifyApi.fetchRecommendations2(
            finalToken,
            numSongs,
            "US",
            seed_artists,
            targetAcousticness,
            targetDanceability,
            targetEnergy,
            targetInstrumentalness,
            targetLiveness,
            targetLoudness,
            targetSpeechiness,
            targetValence
            )
        return unpackSongsForPlaylist(response)
    }

    private fun unpackSongsForPlaylist(response: SpotifyApi.RecommendationResponse): List<Song> {
        var result = mutableListOf<Song>()

        for (track in response.tracks) {
            val songTitle = track.name
            val artistName = track.artists[0].name
            val artistId = track.artists[0].id
            val id = track.id

            val albumImageUrl = track.album.images[0].url
            val rawPreviewUrl = track.preview_url
            var previewUrl = ""
            if (rawPreviewUrl != null) {
                previewUrl = rawPreviewUrl
            }
            result.add(Song(id, songTitle, artistName, artistId, albumImageUrl, previewUrl))
        }

        return result
    }

    // Get all the audio features for the user's liked songs
    suspend fun getLikedSongsFeatures(token: String?, likedSongs: List<Song>): List<Song_Features> {
        var response : SpotifyApi.AudioFeatureResponse?

        var finalToken = ""
        if (token != null) {
            finalToken = "Bearer $token"
        }

        var result = mutableListOf<Song_Features>()
        for (likedSong in likedSongs) {
            var id = likedSong.id
            response = spotifyApi.fetchAudioFeatures(finalToken, id)
            result.add(unpackFeatures(response, id))
        }

        return result
    }

    private fun unpackFeatures(response: SpotifyApi.AudioFeatureResponse, id: String): Song_Features {
        val acousticness = response.acousticness
        val danceability = response.danceability
        val energy = response.energy
        val instrumentalness = response.instrumentalness
        val liveness = response.liveness
        val loudness = response.loudness
        val speechiness = response.speechiness
        // val tempo: Float,
        val valence = response.valence

        return Song_Features(id, acousticness, danceability, energy, instrumentalness, liveness, loudness, speechiness, valence)
    }

    // Get the current user
    suspend fun getCurrentUser(token: String?): User {
        var response : SpotifyApi.UserResponse?

        var finalToken = ""
        if (token != null) {
            finalToken = "Bearer $token"
        }

        response = spotifyApi.fetchCurrentUser(finalToken)
        val userId = response.id
        val userName = response.display_name
        var userImageUrl = ""
        val rawUserImage = response.images
        if (rawUserImage.isNotEmpty()){
            val clearestIndex = rawUserImage.size-1
            userImageUrl = rawUserImage[clearestIndex].url
        }

        return User(userId, userName, userImageUrl)
    }

    // Get user from user id
    suspend fun getUser(token: String?, userId:String): User {
        var response : SpotifyApi.UserResponse2?

        var finalToken = ""
        if (token != null) {
            finalToken = "Bearer $token"
        }

        response = spotifyApi.fetchUser(finalToken, userId)
        val userName = response.display_name ?: ""
        var userImageUrl = ""
        val rawUserImage = response.images
        if (rawUserImage != null) {
            val clearestPositionIndex = rawUserImage.size -1
            if (rawUserImage.isNotEmpty()){
                userImageUrl = rawUserImage[clearestPositionIndex].url ?: ""
            }
        }

        return User(userId, userName, userImageUrl)
    }


    // Create playlist in spotify
    suspend fun createPlaylist(token: String?, userId: String, playlistName: String): String {
        val response : SpotifyApi.PlaylistResponse?
        var finalToken = ""
        if (token != null) {
            finalToken = "Bearer $token"
        }

        val description = "Created by Tuneder"
        val body = SpotifyApi.CreatePlaylistBody(playlistName, description)

        response = spotifyApi.createPlaylist(finalToken, userId, body)

        return response.id
    }

    // Create playlist in spotify
    suspend fun addSongsToPlaylist(token: String?, playlistId: String, songList: List<Song>): String {
        val response : SpotifyApi.SnapshotResponse?
        var finalToken = ""
        if (token != null) {
            finalToken = "Bearer $token"
        }

        val uris = songList.map { "spotify:track:${it.id}" }

        val body = SpotifyApi.SongsToPlaylistBody(uris)
        response = spotifyApi.addSongsToPlaylist(finalToken, playlistId, body)

        return response.snapshot_id
    }


}
