package io.github.christianmz.whatsapp.objects

import io.github.christianmz.whatsapp.commons.COLLECTION_USERS
import io.github.christianmz.whatsapp.commons.FILE_IMAGE
import io.github.christianmz.whatsapp.commons.FILE_PROFILE_IMAGES

object FirePath {

    val refProfileImage =
        FireInstance.mStorageRef.child(FILE_IMAGE).child(FILE_PROFILE_IMAGES).child("${User.uid}.jpeg")

    val refUsers = FireInstance.mFirestoreRef.collection(COLLECTION_USERS).document(User.uid)
}