package edu.utap.tuneder3

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify_api_example.Connectors.UserService

class SplashActivity : AppCompatActivity() {

    private lateinit var editor: SharedPreferences.Editor
    private lateinit var mSharedPreferences: SharedPreferences

    private lateinit var queue: RequestQueue

    companion object {
        private const val CLIENT_ID = "183fa8c353be4ea4965ed1b5fb65dce9"
        private const val CLIENT_ID_HANNA = "8eee78021080498b89e2c8620760e493"
        private const val CLIENT_ID_NOEL = "227b1d2303a54211900eed9753bcad65"
        private const val REDIRECT_URI = "edu.utap.tuneder3://callback"
        private const val REQUEST_CODE = 1337

        private val SCOPES = arrayOf("user-read-playback-state",
            "user-modify-playback-state",
            "user-read-currently-playing",
            "streaming",
            "playlist-read-private",
            "playlist-read-collaborative",
            "playlist-modify-private",
            "playlist-modify-public",
            "user-follow-modify",
            "user-follow-read",
            "user-read-playback-position",
            "user-top-read",
            "user-read-recently-played",
            "user-library-modify",
            "user-library-read",
            "user-read-email",
            "user-read-private"
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()

        authenticateSpotify()

        mSharedPreferences = this.getSharedPreferences("SPOTIFY", 0)
        queue = Volley.newRequestQueue(this)
    }

    private fun waitForUserInfo() {

        val userService = UserService(queue, mSharedPreferences)
        userService.get {
            val user = userService.user
            editor = getSharedPreferences("SPOTIFY", 0).edit()
            if (user != null) {
                editor.putString("userid", user.id)
            }
            // Commit instead of apply because we need the information stored immediately
            editor.commit()
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        val newIntent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(newIntent)
    }

    private fun authenticateSpotify() {
        val builder = AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI)
        builder.setScopes(SCOPES)
        // builder.setScopes(arrayOf("streaming"));
        val request = builder.build()
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)

            when (response.type) {
                // Response was successful and contains auth token
                AuthorizationResponse.Type.TOKEN -> {
                    editor = getSharedPreferences("SPOTIFY", 0).edit()
                    editor.putString("token", response.accessToken)
                    Log.d("STARTING", "GOT AUTH TOKEN")
                    editor.apply()
                    waitForUserInfo()
                }

                // Auth flow returned an error
                AuthorizationResponse.Type.ERROR -> {
                    // Handle error response
                }

                // Most likely auth flow was cancelled
                else -> {
                    // Handle other cases
                }
            }
        }
    }
}
