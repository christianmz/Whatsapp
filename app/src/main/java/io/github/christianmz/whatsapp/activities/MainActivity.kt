package io.github.christianmz.whatsapp.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import io.github.christianmz.whatsapp.R
import io.github.christianmz.whatsapp.commons.mAuth
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.mn_search -> toast("")
            R.id.mn_new_group -> toast("")
            R.id.mn_profile -> startActivity<ProfileActivity>()
            R.id.mn_sign_out -> mAuth.signOut()
        }
        return super.onOptionsItemSelected(item)
    }
}
