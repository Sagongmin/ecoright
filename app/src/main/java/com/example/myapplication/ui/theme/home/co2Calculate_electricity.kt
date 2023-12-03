package com.example.myapplication.ui.theme.home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.ui.theme.main.ocrCamera

class co2Calculate_electricity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.carboncalculate_electricity)
        supportActionBar?.hide()


        val backBtn = findViewById<ImageView>(R.id.electricity_vectorback)
        val backsentencebtn = findViewById<TextView>(R.id.electricity_return_btn)
        val gasbtn = findViewById<TextView>(R.id.gas)
        val waterbtn = findViewById<TextView>(R.id.water)

        val camerabtn = findViewById<ImageView>(R.id.electricity_cameravector)
        camerabtn.setOnClickListener{
            Toast.makeText(this, "카메라 클릭됨", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ocrCamera::class.java))
        }



        backBtn.setOnClickListener {
            val intent = Intent(this, co2Calculate::class.java)
            startActivity(intent)
            finish()
        }

        backsentencebtn.setOnClickListener {
            val intent = Intent(this, co2Calculate::class.java)
            startActivity(intent)
            finish()
        }

        gasbtn.setOnClickListener {
            val intent = Intent(this, co2Calculate_gas::class.java)
            startActivity(intent)
            finish()
        }

        waterbtn.setOnClickListener {
            val intent = Intent(this, co2Calculate_water::class.java)
            startActivity(intent)
            finish()
        }
    }
}