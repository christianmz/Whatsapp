package io.github.christianmz.whatsapp.commons

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.github.christianmz.whatsapp.R

/** Unique Instances **/

val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
val mStorageRef: StorageReference = FirebaseStorage.getInstance().reference
val mCollectionRef: FirebaseFirestore get() = FirebaseFirestore.getInstance()
val mUID: String = mAuth.uid.toString()


/** Constants **/

const val REQUEST_IMAGE_CAPTURE = 100
const val REQUEST_IMAGE_GALLERY = 200
const val FILE_IMAGE = "images"
const val FILE_PROFILE_IMAGES = "profile_images"
const val NAME_PROFILE = "profile.jpeg"


/** Request permissions at runtime. **/

fun mRequestPermissions(activity: Activity): Boolean {

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