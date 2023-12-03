package com.example.myapplication.ui.theme.home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.ui.theme.main.ocrCamera

class co2Calculate_gas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.carboncalculate_gas)
        supportActionBar?.hide()

        val backBtn = findViewById<ImageView>(R.id.gas_vectorback)
        val backsentencebtn = findViewById<TextView>(R.id.gas_return_btn)
        val elecbtn = findViewById<TextView>(R.id.electricity)
        val waterbtn = findViewById<TextView>(R.id.water)

        val camerabtn = findViewById<ImageView>(R.id.gas_cameravector)
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

        elecbtn.setOnClickListener {
            val intent = Intent(this, co2Calculate_electricity::class.java)
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