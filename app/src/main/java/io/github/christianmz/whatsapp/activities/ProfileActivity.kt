package io.github.christianmz.whatsapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.google.firebase.auth.UserProfileChangeRequest
import io.github.christianmz.whatsapp.R
import io.github.christianmz.whatsapp.commons.*
import kotlinx.android.synthetic.main.activity_profile.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private val etName by lazy { et_name_profile.text.toString() }
    private val refProfileImage = mStorageRef.child(FILE_IMAGE).child(FILE_PROFILE_IMAGES).child("$mUID.jpeg")

    private lateinit var mPhotoSelectedUri: Uri
    private var mCurrentPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setSupportActionBar(toolbar_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUserInfo()

        iv_profile.setOnClickListener { openGallery() }

        ib_edit_name.setOnClickListener {
            updateName(etName)
            longToast(R.string.update_name)
        }

        fab_photo_profile.setOnClickListener {
            if (mRequestPermissions(this)) {
                setUpAlertDialog()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            var image: Bitmap? = null

            try {
                when (requestCode) {
                    REQUEST_IMAGE_GALLERY -> {
                        image = MediaStore.Images.Media.getBitmap(contentResolver, data?.data)
                        uploadImageFromGallery(image)
                    }
                    REQUEST_IMAGE_CAPTURE -> {
                        mPhotoSelectedUri = addPictureToGallery()
                        image = MediaStore.Images.Media.getBitmap(this.contentResolver, mPhotoSelectedUri)
                        uploadImageFromCamera(mPhotoSelectedUri)
                    }
                }

                if (image != null) iv_profile.setImageBitmap(image)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImageFromGallery(image: Bitmap) {

        val imageData: ByteArray
        val baos = ByteArrayOutputStream()

        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        imageData = baos.toByteArray()

        refProfileImage.putBytes(imageData)
            .addOnSuccessListener { TaskSnapshot ->
                TaskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    updatePhoto(it)
                }
            }.addOnFailureListener {
                longToast(R.string.error_upload_image)
            }
    }

    private fun uploadImageFromCamera(uri: Uri){
        refProfileImage.putFile(uri)
            .addOnSuccessListener { TaskSnapshot ->
                TaskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    updatePhoto(it)
                }
            }.addOnFailureListener {
                longToast(R.string.error_upload_image)
            }
    }

    private fun setUpAlertDialog() {

        val selectView = LayoutInflater.from(this).inflate(R.layout.alert_select_imagen, null)
        val galleryButton: FloatingActionButton = selectView.findViewById(R.id.fab_gallery)
        val cameraButton: FloatingActionButton = selectView.findViewById(R.id.fab_camera)
        val deleteButton: FloatingActionButton = selectView.findViewById(R.id.fab_delete)

        AlertDialog
            .Builder(this)
            .setView(selectView)
            .create()
            .show()

        galleryButton.setOnClickListener { openGallery() }
        cameraButton.setOnClickListener { openCamera() }
        deleteButton.setOnClickListener { deletePhoto() }
    }

    private fun setUserInfo() {

        val photoUrl = mUser?.photoUrl
        val nameUser = mUser?.displayName

        et_name_profile.setText(nameUser)

        if (photoUrl != null) {
            Glide.with(this).load(photoUrl).into(iv_profile)
        } else {
            iv_profile.setImageResource(R.drawable.placeholder_profile)
        }
    }

    private fun openGallery() {

        val openGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        if (openGalleryIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(openGalleryIntent, REQUEST_IMAGE_GALLERY)
        }
    }

    private fun openCamera() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(packageManager) != null) {

            var photoFile: File? = null

            try {
                photoFile = createImageFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (photoFile != null) {
                val photoUri = FileProvider.getUriForFile(this, "io.github.christianmz.whatsapp", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun deletePhoto() {
        refProfileImage.delete()
            .addOnSuccessListener {
                iv_profile.setImageResource(R.drawable.placeholder_profile)
            }.addOnFailureListener {
                longToast(R.string.delete_error)
            }
    }

    private fun createImageFile (): File? {

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(Date())
        val imageFileName = "JPEG${timeStamp}_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var image: File? = null

        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir)
            mCurrentPath = image.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return image
    }

    private fun addPictureToGallery(): Uri {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val newFile = File(mCurrentPath)
        val contentUri = Uri.fromFile(newFile)
        mediaScanIntent.data = contentUri
        this.sendBroadcast(mediaScanIntent)
        mCurrentPath = null.toString()
        return contentUri
    }

    private fun updateName(name: String) {
        val profile = UserProfileChangeRequest.Builder().setDisplayName(name).build()
        mUser?.updateProfile(profile)?.addOnCompleteListener {
            if (!it.isSuccessful) {
                Log.d("Profile_Activity", "ERROR_UPDATE_NAME")
            }
        }
    }

    private fun updatePhoto(url: Uri) {
        val profile = UserProfileChangeRequest.Builder().setPhotoUri(url).build()
        mUser?.updateProfile(profile)?.addOnCompleteListener {
            if (!it.isSuccessful) {
                Log.d("Profile_Activity", "ERROR_UPDATE_PHOTO")
            }
        }
    }
}
