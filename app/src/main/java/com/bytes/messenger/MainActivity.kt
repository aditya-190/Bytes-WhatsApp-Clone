package com.bytes.messenger

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bytes.messenger.activity.ContactsActivity
import com.bytes.messenger.activity.SettingsActivity
import com.bytes.messenger.databinding.ActivityMainBinding
import com.bytes.messenger.fragment.CallsFragment
import com.bytes.messenger.fragment.ChatsFragment
import com.bytes.messenger.fragment.StatusFragment
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private var fragmentPosition: Int = 0
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        binding.tab.setupWithViewPager(binding.viewPager)
        binding.viewPager.setCurrentItem(0, true)

        clickListeners()
    }

    private fun clickListeners() {
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
            }

            override fun onPageSelected(position: Int) {
                fragmentPosition = position
                binding.fab.hide()
                Handler(Looper.myLooper()!!).postDelayed({
                    when (position) {
                        0 -> binding.fab.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@MainActivity,
                                R.drawable.icon_chat
                            )
                        )
                        1 -> binding.fab.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@MainActivity,
                                R.drawable.icon_camera
                            )
                        )
                        else -> binding.fab.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@MainActivity,
                                R.drawable.icon_phone
                            )
                        )
                    }
                    binding.fab.show()
                }, 100)
            }
        })

        binding.fab.setOnClickListener {
            when (fragmentPosition) {
                0 -> {
                    startActivity(Intent(this@MainActivity, ContactsActivity::class.java))
                }
                1 -> Snackbar.make(findViewById(android.R.id.content), "1",
                    Snackbar.LENGTH_SHORT).show()
                else -> Snackbar.make(findViewById(android.R.id.content), "2",
                    Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> {
                Snackbar.make(findViewById(android.R.id.content), "Search",
                    Snackbar.LENGTH_SHORT).show()
            }
            R.id.menu_more -> {
                Snackbar.make(findViewById(android.R.id.content), "More",
                    Snackbar.LENGTH_SHORT).show()
            }
            R.id.menu_group -> {
                Snackbar.make(findViewById(android.R.id.content), "New Group",
                    Snackbar.LENGTH_SHORT).show()
            }
            R.id.menu_broadcast -> {
                Snackbar.make(findViewById(android.R.id.content), "Broadcast",
                    Snackbar.LENGTH_SHORT).show()
            }
            R.id.menu_web -> {
                Snackbar.make(findViewById(android.R.id.content), "Web",
                    Snackbar.LENGTH_SHORT).show()
            }
            R.id.menu_starred -> {
                Snackbar.make(findViewById(android.R.id.content), "Starred",
                    Snackbar.LENGTH_SHORT).show()
            }
            R.id.menu_settings -> {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private class ViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val tabTitles = arrayOf("Chats", "Status", "Calls")

        override fun getCount(): Int {
            return tabTitles.size
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> ChatsFragment()
                1 -> StatusFragment()
                else -> CallsFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return tabTitles[position]
        }
    }
}
