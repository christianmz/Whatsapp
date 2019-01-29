package io.github.christianmz.whatsapp.activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.github.christianmz.whatsapp.R
import io.github.christianmz.whatsapp.adapters.PagerAdapter
import io.github.christianmz.whatsapp.commons.USER_NAME
import io.github.christianmz.whatsapp.commons.USER_PHONE_NUMBER
import io.github.christianmz.whatsapp.fragments.ChatsFragment
import io.github.christianmz.whatsapp.fragments.ContactsFragment
import io.github.christianmz.whatsapp.objects.FireInstance
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setUpViewPager(getPagerAdapter())
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
            R.id.mn_sign_out -> signOut()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getPagerAdapter(): PagerAdapter {
        val adapter = PagerAdapter(supportFragmentManager)
        adapter.addFragment(ChatsFragment())
        adapter.addFragment(ContactsFragment())
        return adapter
    }

    private fun setUpViewPager(adapter: PagerAdapter){
        view_pager.adapter = adapter
        view_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout))
        tab_layout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(view_pager))
    }

    private fun signOut(){
        FireInstance.mAuth.signOut()
        startActivity<LoginActivity>()
        finish()
    }
}
