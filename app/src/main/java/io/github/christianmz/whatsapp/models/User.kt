package io.github.christianmz.whatsapp.models

import com.google.firebase.firestore.Exclude
import io.github.christianmz.whatsapp.objects.FirePath

data class User(@get:Exclude val uid: String, val name: String, val phoneNumber: String, val photoUrl: String) {

    fun saveUser() {
        FirePath.refUsers.document(uid).set(this)
    }

}