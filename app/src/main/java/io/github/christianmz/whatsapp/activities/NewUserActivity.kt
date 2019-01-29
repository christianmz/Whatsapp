package io.github.christianmz.whatsapp.activities

import android.app.Activity
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
import io.github.christianmz.whatsapp.R
import io.github.christianmz.whatsapp.commons.*
import io.github.christianmz.whatsapp.models.User
import io.github.christianmz.whatsapp.objects.FireInstance
import io.github.christianmz.whatsapp.objects.FirePath
import kotlinx.android.synthetic.main.activity_new_user.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class NewUserActivity : AppCompatActivity() {

    private val nameUser by lazy { et_new_user_name.text.toString() }
    private val mPhotoSelectedUri by lazy { addPictureToGallery(this) }
    private val phoneNumber by lazy { intent.getStringExtra(USER_PHONE_NUMBER) }

    private var photoUrl: Uri? = null
    private lateinit var newUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)

        iv_new_user_photo.setOnClickListener { if (isAllPermissionsGranted(this)) setUpAlertChoseImage() }

        btn_next_new_user.setOnClickListener {
            when {
                nameUser.isEmpty() -> longToast(R.string.please_add_your_name)
                photoUrl == null -> longToast(R.string.please_add_a_image)
                photoUrl == null && nameUser.isEmpty() -> longToast(R.string.please_add_your_info)
                photoUrl != null && nameUser.isNotEmpty() -> {
                    newUser = User(FireInstance.mUid.toString(), nameUser, phoneNumber, photoUrl.toString())
                    newUser.saveUser()
                    FireInstance.updateName(nameUser)
                    startActivity<MainActivity>(USER_NAME to nameUser)
                }
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
                        image = MediaStore.Images.Media.getBitmap(contentResolver, mPhotoSelectedUri)
                        uploadImageFromCamera(mPhotoSelectedUri)
                    }
                }

                if (image != null) iv_new_user_photo.setImageBitmap(image)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setUpAlertChoseImage() {

        val selectView = LayoutInflater.from(this).inflate(R.layout.alert_select_image, null)
        val galleryButton: FloatingActionButton = selectView.findViewById(R.id.fab_gallery_select)
        val cameraButton: FloatingActionButton = selectView.findViewById(R.id.fab_camera_select)

        AlertDialog
            .Builder(this)
            .setView(selectView)
            .create()
            .show()

        galleryButton.setOnClickListener { openGallery() }
        cameraButton.setOnClickListener { openCamera() }
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

    private fun uploadImageFromGallery(image: Bitmap) {

        val imageData: ByteArray
        val baos = ByteArrayOutputStream()

        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        imageData = baos.toByteArray()

        FirePath.refProfileImage.putBytes(imageData)
            .addOnSuccessListener { TaskSnapshot ->
                TaskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    photoUrl = it
                    FireInstance.updatePhoto(photoUrl!!)
                }
            }.addOnFailureListener {
                longToast(R.string.error_upload_image)
            }
    }

    private fun uploadImageFromCamera(uri: Uri) {

        FirePath.refProfileImage.putFile(uri)
            .addOnSuccessListener { TaskSnapshot ->
                TaskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    photoUrl = it
                    FireInstance.updatePhoto(photoUrl!!)
                }
            }.addOnFailureListener {
                longToast(R.string.error_upload_image)
            }
    }
}
