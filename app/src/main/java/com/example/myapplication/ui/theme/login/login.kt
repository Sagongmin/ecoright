package com.example.myapplication.ui.theme.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.ui.theme.home.mainMenu
import com.google.firebase.auth.FirebaseAuth

class login : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)

        emailEditText = findViewById(R.id.editid) // 이메일 입력 필드
        passwordEditText = findViewById(R.id.editpass) // 비밀번호 입력 필드

        val goToJoinButton = findViewById<Button>(R.id.btn_joinMember)
        val loginButton = findViewById<Button>(R.id.btn_login)

        goToJoinButton.setOnClickListener {
            val intent = Intent(this, join_membership::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Firebase 인증을 사용하여 사용자 로그인 시도
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 로그인 성공 시
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null) {
                            Toast.makeText(
                                this,
                                "로그인 성공. 현재 로그인된 사용자: " + user.uid,
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this, mainMenu::class.java)
                            intent.putExtra("userId", user.uid)
                            intent.putExtra("userEmail", user.email)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        // 로그인 실패 시
                        Toast.makeText(
                            this,
                            "로그인 실패. 이메일과 비밀번호를 확인하세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}