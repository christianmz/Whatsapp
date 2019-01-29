package io.github.christianmz.whatsapp.objects

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object FireInstance {

    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val mStorageRef: StorageReference = FirebaseStorage.getInstance().reference
    val mFirestoreRef: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private val mUser: FirebaseUser? = mAuth.currentUser

    val mUid = mAuth.uid
    val mPhoneNumber = mUser?.phoneNumber
    val mNameUser = mUser?.displayName
    val mPhotoUrl = mUser?.photoUrl

    fun updateName(name: String) {
        val profile = UserProfileChangeRequest.Builder().setDisplayName(name).build()
        mUser?.updateProfile(profile)?.addOnCompleteListener {
            if (!it.isSuccessful) Log.d("Profile_Activity", "ERROR_UPDATE_NAME")
        }
    }

    fun updatePhoto(photoUrl: Uri) {
        val profile = UserProfileChangeRequest.Builder().setPhotoUri(photoUrl).build()
        mUser?.updateProfile(profile)?.addOnCompleteListener {
            if (!it.isSuccessful) Log.d("Profile_Activity", "ERROR_UPDATE_PHOTO")
        }
    }
}







