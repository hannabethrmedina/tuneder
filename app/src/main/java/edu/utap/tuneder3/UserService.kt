package com.spotify_api_example.Connectors

import android.content.SharedPreferences
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import edu.utap.tuneder3.User
import edu.utap.tuneder3.VolleyCallBack

// Created following this tutorial: https://towardsdatascience.com/using-the-spotify-api-with-your-android-application-the-essentials-1a3c1bc36b9e
class UserService(private val mQueue: RequestQueue, private val mSharedPreferences: SharedPreferences) {

    companion object {
        private const val ENDPOINT = "https://api.spotify.com/v1/me"
    }

    var user: User? = null
        private set

    fun get(callBack: VolleyCallBack) {
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET, ENDPOINT, null, { response ->
            val gson = Gson()
            user = gson.fromJson(response.toString(), User::class.java)
            callBack.onSuccess()
        }, { error ->
            get(callBack)
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val token = mSharedPreferences.getString("token", "") ?: ""
                val auth = "Bearer $token"
                headers["Authorization"] = auth
                return headers
            }
        }
        mQueue.add(jsonObjectRequest)
    }
}
