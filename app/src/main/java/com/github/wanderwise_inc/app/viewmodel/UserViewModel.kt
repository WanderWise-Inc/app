package com.github.wanderwise_inc.app.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.wanderwise_inc.app.model.user.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

const val DB_ITINERARY_PATH: String = "users"

class UserViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection(DB_ITINERARY_PATH)

    /**
     * @return a freshly generated user id
     */
    public fun genUserId() : String {
        return usersCollection.document().id
    }

    /**
     * Create a new User and stores it in the database
     */
    public suspend fun setUser(user: User) : Boolean {

        // Creating a hashmap between fields of database and actual user fields
        val u = hashMapOf(
            "userid" to user.userid,
            "username" to user.username,
            "age" to user.age
        )

        // Adding the user to the collection, returning true if success and false if failure
        return try {
            usersCollection
                .document(user.userid)
                .set(u)
                .await()
            true
        } catch (e : Exception) {
            Log.w(TAG, "Error adding document", e)
            false
        }
    }

    /**
     * @return a hash map of a user
     */
    private fun userToHashMap(user: User) {

    }

    /**
     * Get a user from a userID
     */
    public suspend fun getUser(uid : String) : User?{
        return try {
            // Get first the document with the given uid
            val document = usersCollection.document(uid).get().await()
            // Access each field of the document and create a User accordingly
            val userid = document.get("userid").toString()
            val username = document.get("username").toString()
            val age = document.get("age").toString().toInt()
            User(userid, username, age)
        } catch (e: Exception) {
            Log.d(TAG, "get failed with ", e)
            null
        }
    }

    /**
     * @return the list of all the users
     */
    public suspend fun getAllUser() : List<User> {
        val userList = mutableListOf<User>()

        try {
            val querySnapshot = usersCollection.get().await()
            for (document in querySnapshot.documents) {
                val userid = document.get("userid").toString()
                val username = document.get("username").toString()
                val age = document.get("age").toString().toInt()
                val user = User(userid, username, age)
                userList.add(user)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error adding document", e)
        }

        return userList
    }

}