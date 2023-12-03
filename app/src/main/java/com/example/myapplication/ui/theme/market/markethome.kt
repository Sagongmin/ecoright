package com.example.myapplication.ui.theme.market
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import android.widget.ImageView
import android.widget.Toast
import android.widget.EditText
import android.widget.TextView

import com.example.myapplication.ui.theme.login.join_membership
import com.example.myapplication.ui.theme.home.mainMenu
import com.example.myapplication.ui.theme.transaction.BuyitemActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class markethome:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.market)
        supportActionBar?.hide()


        val product1Button = findViewById<View>(R.id.product1)
        val searchButton = findViewById<ImageView>(R.id.magnifierview)


        product1Button.setOnClickListener {
            val intent = Intent(this, marketiphone::class.java)
            startActivity(intent)
        }

        searchButton.setOnClickListener {
            val intent = Intent(this, maketsearch::class.java)
            startActivity(intent)
        }
    }


}

