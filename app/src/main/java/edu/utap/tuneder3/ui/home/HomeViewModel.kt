package edu.utap.tuneder3.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.utap.tuneder3.Song
import edu.utap.tuneder3.Song_Features
import edu.utap.tuneder3.User
import edu.utap.tuneder3.ViewModelDBHelper
import edu.utap.tuneder3.api.SpotifyApi
import edu.utap.tuneder3.api.SpotifyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeViewModel : ViewModel() {

    private var friendsToLikedSongs = MutableLiveData<Map<String, String>>()

    // private var songTitle = MutableLiveData<String>()
    // private var artistName = MutableLiveData<String>()
    var currentUser = MutableLiveData<User>()
    fun observeCurrentUser(): LiveData<User> {
        return currentUser
    }

    var currentlyPlayingPosition = -1
    var generatedPlaylistPageTitle = "Date with a Playlist"


    // Current generated playlist song list
    private var generatedPlaylist = MutableLiveData<List<Song>>()


    // All stuff that I'll need to update
    private var seedArtistsDefault = "4NZvixzsSefsNiIqXn0NDe,0oSGxfWSnnOXhD2fKuz2Gy"  // Back up is maggie rogers, david bowie
    private var seedArtistsFromUser = MutableLiveData<String>()

    // Api and repo
    private var spotifyApi = SpotifyApi.create()
    private var spotifyRepository = SpotifyRepository(spotifyApi)


    // Current song
    private var currentSong = MutableLiveData<Song>()
    fun observeCurrentSong(): LiveData<Song> {
        return currentSong
    }

    fun fetchNewSong(token: String) {
        // Make call to fetch new one, postValue to currentSong.
        viewModelScope.launch(
            context = viewModelScope.coroutineContext + Dispatchers.IO) {
            var artists = seedArtistsDefault
            if (!seedArtistsFromUser.value.isNullOrEmpty()){
                artists = seedArtistsFromUser.value!!
            }
            val test = spotifyRepository.getSong(token, artists)
            currentSong.postValue(test)
        }
    }


    // Friends List and Liked Songs (db helper)
    private var friendsList = MutableLiveData<List<User>>()
    private var likedSongsList = MutableLiveData<List<Song>>()
    private val dbHelper = ViewModelDBHelper()

    fun observeFriendsList(): LiveData<List<User>> {
        return friendsList
    }

    fun initializeFriendList(token: String, userId: String) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext + Dispatchers.IO) {

            dbHelper.getUserFriendList(userId) {
                friendsList.postValue(it)
            }
        }
    }

    fun addUserToFriendList(token: String, userId: String, friendId: String) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext + Dispatchers.IO) {
            val user = spotifyRepository.getUser(token, friendId)

            dbHelper.addFriend(userId, user) {
                friendsList.postValue(it)
            }
        }
    }

    fun observeLikedSongsList(): LiveData<List<Song>> {
        return likedSongsList
    }

    fun initializeLikedSongList(token: String, userId: String) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext + Dispatchers.IO) {

            dbHelper.getUserLikedSongs(userId) {
                likedSongsList.postValue(it)
                findTopArtists(it, 2)
            }
        }
    }

    fun addSongToLikedSongList(token: String, userId: String) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext + Dispatchers.IO) {

            val song = currentSong.value
            if (song != null) {
                dbHelper.addSongToUserLikedSongList(userId, song) {
                    likedSongsList.postValue(it)
                    findTopArtists(it, 2)
                }
            }
        }
    }


    fun setCurrentUser(token: String) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext + Dispatchers.IO) {
            val test = spotifyRepository.getCurrentUser(token)
            currentUser.postValue(test)
        }
    }



    private fun calculateAverageFeatures(songs: List<Song_Features>): Song_Features {
        var totalAcousticness = 0.0
        var totalDanceability = 0.0
        var totalEnergy = 0.0
        var totalInstrumentalness = 0.0
        var totalLiveness = 0.0
        var totalLoudness = 0.0
        var totalSpeechiness = 0.0
        var totalValence = 0.0

        songs.forEach {
            totalDanceability += it.danceability
            totalAcousticness += it.acousticness
            totalEnergy += it.energy
            totalInstrumentalness += it.instrumentalness
            totalLiveness += it.liveness
            totalLoudness += it.loudness
            totalSpeechiness += it.speechiness
            totalValence += it.valence
        }

        val averageDanceability = totalDanceability / songs.size
        val averageAcousticness = totalAcousticness / songs.size
        val averageEnergy = totalEnergy / songs.size
        val averageInstrumentalness = totalInstrumentalness / songs.size
        val averageLiveness = totalLiveness / songs.size
        val averageLoudness = totalLoudness / songs.size
        val averageSpeechiness = totalSpeechiness / songs.size
        val averageValence = totalValence / songs.size

        return Song_Features(
            id = "average",
            danceability = averageDanceability.toFloat(),
            acousticness = averageAcousticness.toFloat(),
            energy = averageEnergy.toFloat(),
            instrumentalness = averageInstrumentalness.toFloat(),
            liveness = averageLiveness.toFloat(),
            loudness = averageLoudness.toFloat(),
            speechiness = averageSpeechiness.toFloat(),
            valence = averageValence.toFloat()
        )
    }

//    private fun calculateArtistFrequency(songs: List<Song>): Map<String, Int> {
//        return songs.groupingBy { it.artistId }.eachCount()
//    }

    private fun findTopArtists(likedSongs: List<Song>, num: Int) {

        val artistFrequency = likedSongs.groupingBy { it.artistId }.eachCount()
        val topArtists = artistFrequency
            .toList()
            .sortedByDescending { it.second }
            .take(num)

        val artists = topArtists.joinToString(separator = ",") {
            it.first
        }

        seedArtistsFromUser.postValue(artists)
    }



    fun fetchSongsForPlaylist(token: String) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext + Dispatchers.IO) {
            if (likedSongsList.value != null) {
                var likedSongsAudioFeatures =
                    spotifyRepository.getLikedSongsFeatures(token, likedSongsList.value!!)

                var averageAudioFeatures = calculateAverageFeatures(likedSongsAudioFeatures)


                var artists = seedArtistsDefault
                if (!seedArtistsFromUser.value.isNullOrEmpty()){
                    artists = seedArtistsFromUser.value!!
                }
                val test = spotifyRepository.getSongsForPlaylist(
                    token,
                    artists,
                    averageAudioFeatures
                )
                generatedPlaylist.postValue(test)
            }
        }
    }

    fun observeGeneratedPlaylist(): LiveData<List<Song>> {
        return generatedPlaylist
    }

    fun createPlaylistInSpotify(token: String, userId: String, playlistName: String) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext + Dispatchers.IO) {

            val songsToAdd = generatedPlaylist.value
            if (!songsToAdd.isNullOrEmpty()) {
                // Actually create the playlist
                val playlistId = spotifyRepository.createPlaylist(token, userId, playlistName)

                // Add the songs to the playlist
                val snapshotId = spotifyRepository.addSongsToPlaylist(token, playlistId, songsToAdd)
            }
        }
    }

    fun fetchSongsForPlaylistWithFriend(token: String, friendUserId: String) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext + Dispatchers.IO) {
            if (likedSongsList.value != null) {
                var friendLikedSongList = emptyList<Song>()
                dbHelper.getFriendLikedSongList(friendUserId) {
                    friendLikedSongList = it
                }

                var joinedLikedSongsList = likedSongsList.value!!.toMutableList()
                joinedLikedSongsList.addAll(friendLikedSongList)

                var likedSongsAudioFeatures =
                    spotifyRepository.getLikedSongsFeatures(token, joinedLikedSongsList)
                var averageAudioFeatures = calculateAverageFeatures(likedSongsAudioFeatures)


                var artists = seedArtistsDefault
                if (!seedArtistsFromUser.value.isNullOrEmpty()){
                    artists = seedArtistsFromUser.value!!
                }
                val test = spotifyRepository.getSongsForPlaylist(
                    token,
                    artists,
                    averageAudioFeatures
                )
                generatedPlaylist.postValue(test)
            }
        }
    }

    fun startObservingFriendsLikedSongs(userId: String) {
        dbHelper.observeFriends(userId) { friendId, likedSongsCount ->
            val currentMap = friendsToLikedSongs.value ?: emptyMap()
            val newMap = currentMap.toMutableMap()
            newMap[friendId] = likedSongsCount.toString()

            friendsToLikedSongs.value = newMap
        }
    }

    fun observeFriendsToLikedSongs(): LiveData<Map<String, String>> {
        return friendsToLikedSongs
    }
}