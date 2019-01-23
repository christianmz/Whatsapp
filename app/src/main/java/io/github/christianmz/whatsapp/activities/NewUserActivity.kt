package io.github.christianmz.whatsapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import io.github.christianmz.whatsapp.R
import io.github.christianmz.whatsapp.commons.*
import io.github.christianmz.whatsapp.objects.FirePath
import io.github.christianmz.whatsapp.objects.User
import kotlinx.android.synthetic.main.activity_new_user.*
import kotlinx.android.synthetic.main.activity_profile.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import java.io.File
import java.io.IOException

class NewUserActivity : AppCompatActivity() {

    private val nameUser by lazy { et_new_user_name.text.toString() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)

        setUserInfo()

        iv_new_user_photo.setOnClickListener { if (isAllPermissionsGranted(this)) setUpAlertChoseImage() }

        btn_next_new_user.setOnClickListener {
            if (nameUser.isNotEmpty()) {
                saveUser()
                startActivity<MainActivity>()
            } else {
                longToast(R.string.please_add_your_name)
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
                        User.uploadImageFromGallery(image, this)
                    }
                    REQUEST_IMAGE_CAPTURE -> {
                        val mPhotoSelectedUri = addPictureToGallery(this)
                        image = MediaStore.Images.Media.getBitmap(this.contentResolver, mPhotoSelectedUri)
                        User.uploadImageFromCamera(mPhotoSelectedUri, this)
                    }
                }

                if (image != null) iv_new_user_photo.setImageBitmap(image)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setUserInfo() {

        User.name?.let { et_new_user_name.setText(User.name) }

        if (User.photoUrl != null) {
            Glide.with(this).load(User.photoUrl).into(iv_new_user_photo)
        } else {
            Glide.with(this).load(R.drawable.placeholder_profile).into(iv_new_user_photo)
        }
    }

    private fun saveUser() {

        val newUser = HashMap<String, Any>()

        newUser[USER_UID] = User.uid
        newUser[USER_PHONE_NUMBER] = intent.getStringExtra("phone_number")
        newUser[USER_NAME] = User.updateName(nameUser)
        newUser[USER_PROFILE_IMAGE_URL] = User.updatePhoto(addPictureToGallery(this))

        FirePath.refUsers.set(newUser)
            .addOnCompleteListener {
            }.addOnFailureListener {
                longToast(R.string.error_create_user)
                startActivity<LoginActivity>()
                finish()
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
}
