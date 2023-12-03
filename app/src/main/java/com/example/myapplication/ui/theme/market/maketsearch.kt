package com.example.myapplication.ui.theme.market
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import android.widget.Toast
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.ui.theme.home.co2Calculate

import com.example.myapplication.ui.theme.login.join_membership
import com.example.myapplication.ui.theme.home.mainMenu
import com.example.myapplication.ui.theme.transaction.BuyitemActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class maketsearch: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.marketsearch)
        supportActionBar?.hide()

        val backButton = findViewById<ImageView>(R.id.vector2)

        backButton.setOnClickListener {//co2 관련 버튼
            val intent = Intent(this, markethome::class.java)
            startActivity(intent)
            finish()
        }
    }
}