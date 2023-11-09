package com.example.myapplication.ui.theme.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.ui.theme.transaction.SellitemActivity
import com.google.firebase.auth.FirebaseAuth

class mainMenu : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homemenu)

        val goTotrButton = findViewById<Button>(R.id.TR_btn)
        val goTocmButton = findViewById<Button>(R.id.CM_btn)
        val goToseButton = findViewById<Button>(R.id.SE_btn)
        val goTologoutButton = findViewById<Button>(R.id.logout_btn)

        goTotrButton.setOnClickListener {
            val intent = Intent(this, SellitemActivity::class.java)
            startActivity(intent)
            finish()
        }

        goTocmButton.setOnClickListener {
            val intent = Intent(this, com.example.myapplication.ui.theme.community.ListActivity::class.java)
            startActivity(intent)
        }

        goTologoutButton.setOnClickListener{
            val auth = FirebaseAuth.getInstance()
            auth.signOut()

            val intent = Intent(this, com.example.myapplication.ui.theme.login.login::class.java)  //커뮤니티 기능 미완성 (임시로 로그아웃으로 변경)
            startActivity(intent)
            finish()
        }

        /*goToseButton.setOnClickListener{
            val intent = Intent{this,}  //기타 설정 기능 미완성
            startActivity(intent)
            finish()
        }*/
    }
}