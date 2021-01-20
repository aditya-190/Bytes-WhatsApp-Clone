package com.bytes.messenger.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bytes.messenger.LastSeen
import com.bytes.messenger.R
import com.bytes.messenger.adapter.ContactsAdapter
import com.bytes.messenger.databinding.ActivityContactsBinding
import com.bytes.messenger.model.Contact
import com.bytes.messenger.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ContactsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactsBinding
    private var contactList: ArrayList<Contact> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.recycler.also {
            it.layoutManager = LinearLayoutManager(this@ContactsActivity)
            it.adapter = ContactsAdapter(contactList, this@ContactsActivity)
        }
        fetchData()
    }

    private fun fetchData() {
        FirebaseDatabase.getInstance().reference.child("Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    contactList.clear()
                    for (data in snapshot.children) {
                        val contacts: User? = data.getValue(User::class.java)
                        if (contacts != null && contacts.userID != FirebaseAuth.getInstance().currentUser!!.uid) {
                            contactList.add(Contact(contacts.userID,
                                contacts.userName,
                                contacts.bio,
                                contacts.profileImage,
                                contacts.status))
                        }
                    }
                    binding.recycler.adapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
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