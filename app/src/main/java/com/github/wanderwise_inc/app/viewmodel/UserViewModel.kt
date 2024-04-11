package com.github.wanderwise_inc.app.viewmodel

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.github.wanderwise_inc.app.model.user.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

const val DB_ITINERARY_PATH: String = "users"

class UserViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection(DB_ITINERARY_PATH)
    private val storage = FirebaseStorage.getInstance()

    /**
     * @return a freshly generated user id
     */
    public fun genUserId() : String {
        return usersCollection.document().id
    }

    /**
     * @return the current user connected
     */
    public fun getUserId() : String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    /**
     * @return the profile picture of the user connected
     */
    public fun getUserProfilePicture() : Uri? {
        return FirebaseAuth.getInstance().currentUser!!.photoUrl
    }

    /**
     * add a user to the database
     * @return true if the user was added, false if not
     */
    public suspend fun setUser(user: User) : Boolean {

        // Creating a hashmap between fields of database and actual user fields
        val u = hashMapOf(
            "userid" to user.userid,
            "username" to user.username,
            "email" to user.email,
            "phoneNumber" to user.phoneNumber,
            "country" to user.country,
            "description" to user.description,
            "upVotes" to user.upVotes
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
     * @return the user in the database with the given uid
     */
    public suspend fun getUser(uid : String) : User?{
        return try {
            // Get first the document with the given uid
            val document = usersCollection.document(uid).get().await()

            if (document.exists()) {
                val userid = document.get("userid").toString()
                val username = document.get("username").toString()
                val email = document.get("email").toString()
                val phoneNumber = document.get("phoneNumber").toString()
                val country = document.get("country").toString()
                val description = document.get("description").toString()
                val upVotes = 0

                User(userid, username, email, phoneNumber, country, description, upVotes)
            } else {
                null
            }
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
                val user = getUser(userid)
                if (user != null) {
                    userList.add(user)
                } else {
                    // TODO ERROR
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error adding document", e)
        }
        return userList
    }

    /**
     * delete a user from the database
     * @return true if successful delete, false if an error happened
     */
    public fun deleteUser(user : User) : Boolean {
        return try {
            usersCollection.document(user.userid)
                .delete()
            true
        } catch(e : Exception) {
            Log.d(TAG, "Error deleting document", e)
            false
        }
    }

    /**
     * update the userName field in the database
     * @return true if update is successful, false if not
     */
    public fun updateUserName(user : User, newUserName : String) : Boolean {
        return try {
            usersCollection.document(user.userid)
                .update("username", newUserName)
            true
        } catch(e : Exception) {
            Log.d(TAG, "Error changing userName", e)
            false
        }
    }

    /**
     * update the phoneNumber field in the database
     * @return true if update is successful, false if not
     */
    public fun updatePhoneNumber(user : User, newPhoneNumber : String) : Boolean {
        return try {
            usersCollection.document(user.userid)
                .update("phoneNumber", newPhoneNumber)
            true
        } catch(e : Exception) {
            Log.d(TAG, "Error changing phoneNumber", e)
            false
        }
    }

    /**
     * update the origin country of the user
     * @return true if update is successful, false if not
     */
    public fun updateCountry(user : User, newCountry : String) : Boolean {
        return try {
            usersCollection.document(user.userid)
                .update("country", newCountry)
            true
        } catch(e : Exception) {
            Log.d(TAG, "Error changing country", e)
            false
        }
    }

    /**
     * update the description of the user
     * @return true if update is successful, false if not
     */
    public fun updateDescription(user : User, newDescription : String) : Boolean {
        return try {
            usersCollection.document(user.userid)
                .update("description", newDescription)
            true
        } catch(e : Exception) {
            Log.d(TAG, "Error changing country", e)
            false
        }
    }

    /**
     * update the email field in the database
     * @return true if update is successful, false if not
     */
    public fun updateEmail(user : User, newEmail : String) : Boolean {
        return try {
            usersCollection.document(user.userid)
                .update("email", newEmail)
            true
        } catch(e : Exception) {
            Log.d(TAG, "Error changing email", e)
            false
        }
    }

    /**
     * update the upvotes in the database
     * @return true if update is successful, false if not
     */
    public fun updateUpVotes(user : User) : Boolean {
        return try {
            usersCollection.document(user.userid)
                .update("upVotes", user.upVotes+1)
            true
        } catch(e : Exception) {
            Log.d(TAG, "Error changing email", e)
            false
        }
    }

    public suspend fun getUpVotes(uid : String) : Int {
        val user = getUser(uid)
        return if (user != null) user.upVotes else 0
    }

    private suspend fun getBitMap(context: Context, url : Uri) : Bitmap {
        val loading = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(url)
            .build()

        Log.d("STORAGE", "REQUEST OK")
        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }

    public suspend fun storeImage(userViewModel: UserViewModel, context: Context, profilePicture : Uri) {
        val storageRef = storage.reference
        val testRef = storageRef.child("images/${userViewModel.getUserId()}")
        val bitMap = getBitMap(context, profilePicture)
        val baos = ByteArrayOutputStream()
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = testRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Log.d("STORAGE", "FAIL ON UPLOAD")
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            Log.d("STORAGE", "SUCCESS ON UPLOAD")
        }
    }

    public fun fetchImage(bitMapState : MutableState<Bitmap?>, userViewModel: UserViewModel) : Bitmap? {
        val storageRef = storage.reference
        val profilePictureRef = storageRef.child("images/${userViewModel.getUserId()}")
        profilePictureRef.getBytes(1024*1024)
            .addOnSuccessListener {
                val bitMap = BitmapFactory.decodeByteArray(it, 0, it.size)
                bitMapState.value = bitMap
            }
            .addOnFailureListener { e ->
                Log.w("BITMAP", e)
            }
        return bitMapState.value
    }

}