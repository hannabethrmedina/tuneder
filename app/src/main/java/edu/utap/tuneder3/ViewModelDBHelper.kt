package edu.utap.tuneder3

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.utap.tuneder3.Song

class ViewModelDBHelper {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "users"

    // Liked Songs section
    fun getUserLikedSongs(userId: String, resultListener: (List<Song>) -> Unit) {
        val songsRef = db.collection(rootCollection).document(userId).collection("likedSongs")
        songsRef.get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "userLikedSongs fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                resultListener(result.documents.mapNotNull {
                    it.toObject(Song::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "userLikedSongs fetch FAILED ", it)
                resultListener(listOf())
            }
    }
    fun addSongToUserLikedSongList(userId: String, newSong: Song, resultListener: (List<Song>) -> Unit) {
        val songsRef = db.collection(rootCollection).document(userId).collection("likedSongs")
        songsRef
            .add(newSong)
            .addOnSuccessListener {
                getUserLikedSongs(userId, resultListener)
            }
            .addOnFailureListener { e ->
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    // Friends section
    fun getUserFriendList(userId: String, resultListener: (List<User>) -> Unit) {
        val friendsRef = db.collection(rootCollection).document(userId).collection("friends")
        friendsRef.get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "userFriends fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                resultListener(result.documents.mapNotNull {
                    it.toObject(User::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "userFriends fetch FAILED ", it)
                resultListener(listOf())
            }
    }
    fun addFriend(userId: String, friendUser: User, resultListener: (List<User>) -> Unit) {
        val friendsRef = db.collection(rootCollection).document(userId).collection("friends")
        friendsRef
            .add(friendUser)
            .addOnSuccessListener {
                getUserFriendList(userId, resultListener)
            }
            .addOnFailureListener { e ->
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    // Friend's liked songs
    // Friends section
    fun getFriendLikedSongList(friendUserId: String, resultListener: (List<Song>) -> Unit) {
        val friendLikedSongsRef = db.collection(rootCollection).document(friendUserId).collection("likedSongs")
        friendLikedSongsRef.get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "friendsLikedSongs fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                resultListener(result.documents.mapNotNull {
                    it.toObject(Song::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "friendsLikedSongs fetch FAILED ", it)
                resultListener(listOf())
            }
    }

    // Add listeners for friends' liked songs count
    fun observeFriends(userId: String, resultListener: (userId: String, likedSongsCount: Int) -> Unit) {
        val friendsRef = db.collection(rootCollection).document(userId).collection("friends")
        friendsRef
            .addSnapshotListener{ snapshot, e ->
                if (e!= null) {
                    // Failed. log it
                    return@addSnapshotListener
                }

                snapshot?.documents?.forEach {
                    val friendUserId = it.data?.get("id").toString()
                    observeFriendsLikedSongs(friendUserId, resultListener)
                }
            }
    }

    private fun observeFriendsLikedSongs(friendUserId: String, resultListener: (userId: String, likedSongsCount: Int) -> Unit) {
        val friendUserRef = db.collection(rootCollection).document(friendUserId).collection("likedSongs")
        friendUserRef
            .addSnapshotListener { snapshot, e ->
                if (e!= null) {
                    // Failed. log it
                    return@addSnapshotListener
                }

                val likedSongsCount = snapshot?.size() ?: 0
                resultListener(friendUserId, likedSongsCount)
            }
    }

}