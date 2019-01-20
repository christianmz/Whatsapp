package io.github.christianmz.whatsapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import io.github.christianmz.whatsapp.R
import io.github.christianmz.whatsapp.commons.*
import kotlinx.android.synthetic.main.activity_profile.*
import org.jetbrains.anko.longToast
import java.io.ByteArrayOutputStream
import java.lang.Exception

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setSupportActionBar(toolbar_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        iv_profile.setOnClickListener { openGallery() }

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
                    }
                    REQUEST_IMAGE_CAPTURE -> {
                        image = data?.extras?.get("data") as Bitmap
                    }
                }

                if (image != null) {

                    val imageData: ByteArray
                    val baos = ByteArrayOutputStream()
                    val mStorageRef =
                        mStorageRef.child(FILE_IMAGE).child(FILE_PROFILE_IMAGES).child(mUID).child(NAME_PROFILE)

                    iv_profile.setImageBitmap(image)
                    image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    imageData = baos.toByteArray()

                    mStorageRef.putBytes(imageData)
                        .addOnFailureListener {
                            longToast(R.string.error_upload_image)
                        }.addOnSuccessListener {
                            
                        }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
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

    private fun openGallery() {

        val openGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        if (openGalleryIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(openGalleryIntent, REQUEST_IMAGE_GALLERY)
        }

    }

    private fun openCamera() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }

    }

    private fun deletePhoto() {

    }
}
