package io.github.christianmz.whatsapp.commons

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.github.christianmz.whatsapp.activities.LoginActivity
import io.github.christianmz.whatsapp.activities.MainActivity
import io.github.christianmz.whatsapp.objects.FireInstance
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class MainEmpty : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FireInstance.mAuth.currentUser == null) {
            startActivity(intentFor<LoginActivity>().newTask().clearTask())
        } else {
            startActivity(intentFor<MainActivity>().newTask().clearTask())
        }
        finish()
    }
}
