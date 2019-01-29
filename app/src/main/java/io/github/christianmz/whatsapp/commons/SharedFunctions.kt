package io.github.christianmz.whatsapp.commons

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.github.christianmz.whatsapp.R
import io.github.christianmz.whatsapp.objects.FireInstance
import io.github.christianmz.whatsapp.objects.FirePath
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/** Constants **/

const val REQUEST_IMAGE_CAPTURE = 100
const val REQUEST_IMAGE_GALLERY = 200
const val FILE_IMAGE = "images"
const val FILE_PROFILE_IMAGES = "profile_images"
const val COLLECTION_USERS = "users"
const val USER_PHONE_NUMBER = "phone_number"
const val USER_NAME = "name"
const val USER_PROFILE_IMAGE_URL = "image_url"


/** Request permissions at runtime **/

fun isAllPermissionsGranted(activity: Activity): Boolean {

    var isPermissionsGranted = false

    val dialog = DialogOnAnyDeniedMultiplePermissionsListener
        .Builder.withContext(activity)
        .withTitle(R.string.permissions_denied)
        .withMessage(R.string.is_important_permissions)
        .withButtonText(R.string.ok)
        .build()

    val permissions = object : MultiplePermissionsListener {

        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
            report?.let {
                if (report.areAllPermissionsGranted()) {
                    isPermissionsGranted = true
                }
            }
        }

        override fun onPermissionRationaleShouldBeShown(
            permissions: MutableList<PermissionRequest>?,
            token: PermissionToken?
        ) {
            token?.continuePermissionRequest()
        }
    }

    val composite = CompositeMultiplePermissionsListener(permissions, dialog)

    Dexter.withActivity(activity)
        .withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        )
        .withListener(composite)
        .check()

    return isPermissionsGranted
}


/** Create a photo file from camera **/

fun createImageFile(context: Context): File {

    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(Date())
    val imageFileName = "JPEG${timeStamp}_"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    return File.createTempFile(imageFileName, ".jpg", storageDir)
}

fun addPictureToGallery(context: Context): Uri {

    val mCurrentPath = createImageFile(context).absolutePath
    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    val newFile = File(mCurrentPath)
    val contentUri = Uri.fromFile(newFile)

    mediaScanIntent.data = contentUri
    context.sendBroadcast(mediaScanIntent)

    return contentUri
}
