package io.github.christianmz.whatsapp.commons

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
val mCollectionRef: FirebaseFirestore get() = FirebaseFirestore.getInstance()


/** Request permissions at runtime. **/

fun mRequestPermissions(activity: Activity) {

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
}