package edu.utap.tuneder3

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import android.util.Log


class SongService(context: Context) {
    private var songs: ArrayList<Song> = arrayListOf()
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("SPOTIFY", 0)
    private var queue: RequestQueue = Volley.newRequestQueue(context)

    fun getSongs(): ArrayList<Song> = songs

    fun getRecentlyPlayedTracks(callBack: VolleyCallBack): ArrayList<Song> {
        val endpoint = "https://api.spotify.com/v1/me/player/currently-playing"
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET, endpoint, null, { response ->
            val gson = Gson()
            Log.d("HANNA", "HANNA 1")
            val trackObject = response.optJSONObject("item")  // "item" not "items"
            if (trackObject != null) {
                try {
                    val song = gson.fromJson(trackObject.toString(), Song::class.java)
                    songs.add(song)  // Assuming 'songs' is a list accessible here
                    callBack.onSuccess()  // Notify that we successfully got the song
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
//            val jsonArray = response.optJSONArray("item")
//            for (i in 0 until jsonArray.length()) {
//                try {
//                    var objectItem = jsonArray.getJSONObject(i)
//                    objectItem = objectItem.optJSONObject("track")
//                    val song = gson.fromJson(objectItem.toString(), Song::class.java)
//                    songs.add(song)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
            callBack.onSuccess()
        }, { error ->
            // TODO: Handle error
            Log.d("HANNA", "HANNA 3")
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val token = sharedPreferences.getString("token", "")
                val auth = "Bearer $token"
                headers["Authorization"] = auth
                return headers
            }
        }
        queue.add(jsonObjectRequest)
        return songs
    }

    fun addSongToLibrary(song: Song) {
        val payload = preparePutPayload(song)
        val jsonObjectRequest = prepareSongLibraryRequest(payload)
        queue.add(jsonObjectRequest)
    }

    private fun prepareSongLibraryRequest(payload: JSONObject): JsonObjectRequest =
        object : JsonObjectRequest(Method.PUT, "https://api.spotify.com/v1/me/tracks", payload, { }, { }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val token = sharedPreferences.getString("token", "")
                val auth = "Bearer $token"
                headers["Authorization"] = auth
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

    private fun preparePutPayload(song: Song): JSONObject {
        val idArray = JSONArray().apply { put(song.id) }
        return JSONObject().apply {
            put("ids", idArray)
        }
    }

    fun getRecommendation(callBack: VolleyCallBack): ArrayList<Song> {
        val endpoint = buildUrl()
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET, endpoint, null, { response ->
            val gson = Gson()
            val trackObject = response.optJSONObject("item")
            if (trackObject != null) {
                try {
                    val song = gson.fromJson(trackObject.toString(), Song::class.java)
                    songs.add(song)  // Assuming 'songs' is a list accessible here
                    callBack.onSuccess()  // Notify that we successfully got the song
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            callBack.onSuccess()
        }, { error ->
            // TODO: Handle error
            Log.d("HANNA", "HANNA 3")
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val token = sharedPreferences.getString("token", "")
                val auth = "Bearer $token"
                headers["Authorization"] = auth
                return headers
            }
        }
        queue.add(jsonObjectRequest)
        return songs
    }

    private fun buildUrl(): String {
        val seedArtists = "4NHQUGzhtTLFvgF5SZesLK"  // Example artist ID
        val seedGenres = "classical,country"        // Example genres
        val seedTracks = "0c6xIDDpzE81m2q797ordA"  // Example track ID
        val limit = 1
        val market = "US"

        return "https://api.spotify.com/v1/recommendations?limit=$limit&market=$market&seed_artists=$seedArtists&seed_genres=$seedGenres&seed_tracks=$seedTracks"
    }
}