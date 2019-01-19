package io.github.christianmz.whatsapp.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.github.christianmz.whatsapp.R
import io.github.christianmz.whatsapp.commons.mRequestPermissions
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setSupportActionBar(toolbar_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fab_photo_profile.setOnClickListener { mRequestPermissions(this) }
    }
}
