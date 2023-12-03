package com.example.myapplication.ui.theme.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class co2Calculate : AppCompatActivity() {

    private lateinit var some_id: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.carboncalculate)
        supportActionBar?.hide()

        some_id = findViewById(R.id.some_id)
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid // 사용자의 UID를 가져옵니다.

        val databaseReference = FirebaseDatabase.getInstance().getReference("users/$userId")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("username").getValue(String::class.java)
                userName?.let {
                    // TextView에 텍스트 설정
                    some_id.text = "   ${userName}님, 안녕하세요!"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("DatabaseError", "userNameNotFounded", error.toException())
            }
        })

        val goTocalBtn = findViewById<Button>(R.id.button_1)

        goTocalBtn.setOnClickListener {
            val intent = Intent(this, co2Calculate_electricity::class.java)
            startActivity(intent)
            finish()
        }

        val goTohomeButton = findViewById<ImageView>(R.id.homebutton)
        val goToco2Button = findViewById<ImageView>(R.id.co2button)
        val goTomarketButton = findViewById<ImageView>(R.id.marketbutton)
        val goToOptionButton = findViewById<ImageView>(R.id.optionbutton)

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