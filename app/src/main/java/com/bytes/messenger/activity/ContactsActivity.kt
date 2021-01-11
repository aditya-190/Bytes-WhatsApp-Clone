package com.bytes.messenger.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bytes.messenger.R
import com.bytes.messenger.adapter.ContactsAdapter
import com.bytes.messenger.databinding.ActivityContactsBinding
import com.bytes.messenger.model.Contact
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ContactsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactsBinding
    private var contactList: ArrayList<Contact> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fetchData()
    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.IO) {
            FirebaseFirestore.getInstance().collection("Users").get().await().forEach {
                if (FirebaseAuth.getInstance().uid != it.data["userID"].toString()) {
                    contactList.add(Contact(it.data["userID"].toString(),
                        it.data["userName"].toString(),
                        it.data["bio"].toString(),
                        it.data["profileImage"].toString(), it.data["status"].toString()))
                }
            }
            withContext(Dispatchers.Main) {
                binding.recycler.also {
                    it.layoutManager = LinearLayoutManager(this@ContactsActivity)
                    it.adapter = ContactsAdapter(contactList, this@ContactsActivity)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.contacts, menu)
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
        }
        return super.onOptionsItemSelected(item)
    }
}