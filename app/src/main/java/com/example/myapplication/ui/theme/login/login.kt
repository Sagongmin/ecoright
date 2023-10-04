package com.example.myapplication.ui.theme.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.ui.theme.login.join_membership
import com.google.firebase.auth.FirebaseAuth

class login : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)



        val goToJoinButton = findViewById<Button>(R.id.btn_joinMember)
        val goToMainButton = findViewById<Button>(R.id.btn_login)

        goToJoinButton.setOnClickListener {
            val intent = Intent(this, join_membership::class.java)
            startActivity(intent)
        }

        goToMainButton.setOnClickListener {
            val intent = Intent(this, com.example.myapplication.ui.theme.home.mainMenu::class.java)
            startActivity(intent)
            finish()

            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // 사용자 정보를 Intent에 추가
                intent.putExtra("userId", user.uid)
                intent.putExtra("userName", user.displayName)
                intent.putExtra("userEmail", user.email)
            }
        }
    }
}