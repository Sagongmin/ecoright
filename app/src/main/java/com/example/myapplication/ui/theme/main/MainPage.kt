package com.example.myapplication.ui.theme.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.google.firebase.database.*
import com.example.myapplication.databinding.ActivityBuyBinding
import com.example.myapplication.ui.theme.login.join_membership
import com.example.myapplication.ui.theme.transaction.BuyitemActivity
import com.example.myapplication.ui.theme.transaction.CarbonCreditItem
import com.example.myapplication.ui.theme.transaction.SellitemActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainPage : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page)


        val goToBuyButton = findViewById<Button>(R.id.goto_buy)

        if (goToBuyButton != null) {
            goToBuyButton.setOnClickListener {
                val intent = Intent(this, BuyitemActivity::class.java)
                startActivity(intent)
            }
        }
        else {
            Toast.makeText(
                this,
                "안되는데요",
                Toast.LENGTH_SHORT
            ).show()
        }

        val goToSellButton = findViewById<Button>(R.id.goto_sell)

        if (goToSellButton != null) {
            goToSellButton.setOnClickListener {
                val intent = Intent(this, SellitemActivity::class.java)
                startActivity(intent)
            }
        }
        else {
            Toast.makeText(
                this,
                "안되는데요",
                Toast.LENGTH_SHORT
            ).show()
        }


    }
}