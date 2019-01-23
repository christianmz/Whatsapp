package io.github.christianmz.whatsapp.objects

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.UserProfileChangeRequest
import io.github.christianmz.whatsapp.R
import java.io.ByteArrayOutputStream

object User {

    val uid = FireInstance.mAuth.uid.toString()
    val phoneNumber = FireInstance.mUser?.phoneNumber
    val name = FireInstance.mUser?.displayName
    val photoUrl = FireInstance.mUser?.photoUrl

    /** Update Info User **/

    fun updateName(name: String): String {
        val profile = UserProfileChangeRequest.Builder().setDisplayName(name).build()
        FireInstance.mUser?.updateProfile(profile)?.addOnCompleteListener {
            if (!it.isSuccessful) Log.d("Profile_Activity", "ERROR_UPDATE_NAME")
        }
        return name
    }

    fun updatePhoto(url: Uri): String {
        val profile = UserProfileChangeRequest.Builder().setPhotoUri(url).build()
        FireInstance.mUser?.updateProfile(profile)?.addOnCompleteListener {
            if (!it.isSuccessful) Log.d("Profile_Activity", "ERROR_UPDATE_PHOTO")
        }
        return url.toString()
    }

    /** Upload user's images **/

    fun uploadImageFromGallery(image: Bitmap, context: Context) {

        val imageData: ByteArray
        val baos = ByteArrayOutputStream()

        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        imageData = baos.toByteArray()

        FirePath.refProfileImage.putBytes(imageData)
            .addOnSuccessListener { TaskSnapshot ->
                TaskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    updatePhoto(it)
                }
            }.addOnFailureListener {
                Toast.makeText(context, R.string.error_upload_image, Toast.LENGTH_LONG).show()
            }
    }

    fun uploadImageFromCamera(uri: Uri, context: Context) {

        FirePath.refProfileImage.putFile(uri)
            .addOnSuccessListener { TaskSnapshot ->
                TaskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    updatePhoto(it)
                }
            }.addOnFailureListener {
                Toast.makeText(context, R.string.error_upload_image, Toast.LENGTH_LONG).show()
            }
    }
}