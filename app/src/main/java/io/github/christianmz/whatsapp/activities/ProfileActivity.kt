package io.github.christianmz.whatsapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot
import io.github.christianmz.whatsapp.R
import io.github.christianmz.whatsapp.commons.*
import io.github.christianmz.whatsapp.objects.FireInstance
import io.github.christianmz.whatsapp.objects.FirePath
import kotlinx.android.synthetic.main.activity_profile.*
import org.jetbrains.anko.longToast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    private val mPhotoSelectedUri by lazy { addPictureToGallery(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setSupportActionBar(toolbar_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUserInfo()

        ib_edit_name.setOnClickListener { setUpAlertEditName() }
        iv_user_photo.setOnClickListener { openGallery() }
        fab_photo_profile.setOnClickListener { if (isAllPermissionsGranted(this)) setUpAlertChangeImage() }
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
                        image = MediaStore.Images.Media.getBitmap(contentResolver, mPhotoSelectedUri)
                        uploadImageFromCamera(mPhotoSelectedUri)
                    }
                }

                if (image != null) iv_user_photo.setImageBitmap(image)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setUserInfo() {

        tv_name_user.text = FireInstance.mNameUser
        tv_phone_number.text = FireInstance.mPhoneNumber

        if (FireInstance.mPhotoUrl != null) {
            Glide.with(this).load(FireInstance.mPhotoUrl).into(iv_user_photo)
        } else {
            Glide.with(this).load(R.drawable.placeholder_profile).into(iv_user_photo)
        }
    }

    private fun setUpAlertChangeImage() {

        val selectView = LayoutInflater.from(this).inflate(R.layout.alert_change_image, null)
        val galleryButton: FloatingActionButton = selectView.findViewById(R.id.fab_gallery_change)
        val cameraButton: FloatingActionButton = selectView.findViewById(R.id.fab_camera_change)
        val deleteButton: FloatingActionButton = selectView.findViewById(R.id.fab_delete)

        AlertDialog
            .Builder(this)
            .setView(selectView)
            .create()
            .show()

        galleryButton.setOnClickListener { openGallery() }
        cameraButton.setOnClickListener { openCamera() }
        deleteButton.setOnClickListener { deleteImage() }
    }

    private fun setUpAlertEditName() {

        val layoutAlert = LayoutInflater.from(this).inflate(R.layout.alert_edit_name, null)

        AlertDialog
            .Builder(this)
            .setTitle(R.string.edit_your_name)
            .setView(layoutAlert)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val editTextName: EditText = layoutAlert.findViewById(R.id.et_name_profile)
                val newUserName = editTextName.text.toString()

                FireInstance.updateName(newUserName)
                tv_name_user.text = newUserName

                longToast(R.string.update_name)
            }
            .create()
            .show()
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
                photoFile = createImageFile(this)
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

    private fun deleteImage() {

        FirePath.refProfileImage.delete()
            .addOnSuccessListener {
                Glide.with(this).load(R.drawable.placeholder_profile).into(iv_user_photo)
            }.addOnFailureListener {
                longToast(R.string.delete_error)
            }
    }

    private fun uploadImageFromGallery(image: Bitmap) {

        val imageData: ByteArray
        val baos = ByteArrayOutputStream()

        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        imageData = baos.toByteArray()

        FirePath.refProfileImage.putBytes(imageData)
            .addOnSuccessListener { TaskSnapshot ->
                TaskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    FireInstance.updatePhoto(it)
                }
            }.addOnFailureListener {
                longToast(R.string.error_upload_image)
            }
    }

    private fun uploadImageFromCamera(uri: Uri) {

        FirePath.refProfileImage.putFile(uri)
            .addOnSuccessListener { TaskSnapshot ->
                TaskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    FireInstance.updatePhoto(it)
                }
            }.addOnFailureListener {
                longToast(R.string.error_upload_image)
            }
    }
}
