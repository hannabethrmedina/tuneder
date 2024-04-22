package edu.utap.tuneder3

data class Song_Features(
    var id: String,
    var acousticness: Float,
    var danceability: Float,
    var energy: Float,
    var instrumentalness: Float,
    var liveness: Float,
    var loudness: Float,
    var speechiness: Float,
    // val tempo: Float,
    var valence: Float,
)