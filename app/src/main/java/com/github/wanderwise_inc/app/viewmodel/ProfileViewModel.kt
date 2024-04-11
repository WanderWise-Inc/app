package com.github.wanderwise_inc.app.viewmodel

import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel : ViewModel() {

    public fun sendResetPassword() {
        val firebaseAuth = FirebaseAuth.getInstance()
        try {
            firebaseAuth.sendPasswordResetEmail("ismaililekan@gmail.com")
            Log.d("PROFILE", "EMAIL FOR RESET SENT")
        } catch (e: Exception) {
            Log.w("PROFILE", e)
        }
    }

    public fun deleteUser() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser!!
        try {
            user.delete()
            Log.d("PROFILE", "USER DELETED FROM AUTH")
        } catch (e: Exception) {
            Log.w("PROFILE", e)
        }
    }
}