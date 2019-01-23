package io.github.christianmz.whatsapp.objects

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object FireInstance {

    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val mStorageRef: StorageReference = FirebaseStorage.getInstance().reference
    val mFirestoreRef: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    val mUser: FirebaseUser? = mAuth.currentUser
}







