package com.example.myapplication.ui.theme.home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.ui.theme.main.ocrCamera

class co2Calculate_water : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.carboncalculate_water)
        supportActionBar?.hide()

        val goTohomeButton = findViewById<ImageView>(R.id.homebutton)
        val goToco2Button = findViewById<ImageView>(R.id.co2button)
        val goTomarketButton = findViewById<ImageView>(R.id.marketbutton)
        val goToOptionButton = findViewById<ImageView>(R.id.optionbutton)

        val camerabtn = findViewById<ImageView>(R.id.water_cameravector)
        camerabtn.setOnClickListener{
            Toast.makeText(this, "카메라 클릭됨", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ocrCamera::class.java))
        }

        goTohomeButton.setOnClickListener {//홈버튼
            val intent = Intent(this, mainMenu::class.java)
            startActivity(intent)
            finish()
        }

        goToco2Button.setOnClickListener {//co2 관련 버튼
            val intent = Intent(this, co2Calculate::class.java)
            startActivity(intent)
            finish()
        }

        goTomarketButton.setOnClickListener{//마켓 거래 관련 버튼

        }

        goToOptionButton.setOnClickListener{//기타 설정 버튼

        }
    }
}