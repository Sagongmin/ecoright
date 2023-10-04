package com.example.myapplication.ui.theme.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.ui.theme.login.join_membership

class login : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)



        val goToJoinButton = findViewById<Button>(R.id.btn_joinMember)

        goToJoinButton.setOnClickListener {
            val intent = Intent(this, join_membership::class.java)
            startActivity(intent)
        }
    }
}