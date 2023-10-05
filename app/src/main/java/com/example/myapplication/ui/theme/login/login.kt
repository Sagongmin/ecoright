package com.example.myapplication.ui.theme.login
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import android.widget.Toast
import android.widget.EditText

import com.example.myapplication.ui.theme.login.join_membership
import com.example.myapplication.ui.theme.main.MainPage
import com.example.myapplication.ui.theme.transaction.BuyitemActivity

class login : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    private lateinit var email_edittext: EditText
    private lateinit var password_edittext: EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)


        auth = FirebaseAuth.getInstance()

        email_edittext = findViewById(R.id.editid)
        password_edittext = findViewById(R.id.editpass)

        val goToJoinButton = findViewById<Button>(R.id.btn_joinMember)

        goToJoinButton.setOnClickListener {
            val intent = Intent(this, join_membership::class.java)
            startActivity(intent)
        }
        val loginButton = findViewById<Button>(R.id.btn_login) // 로그인 버튼 ID로 수정 필요
        loginButton.setOnClickListener {
            signinEmail()
        }
    }



    fun signinEmail() {
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful) {
                    // Login, 아이디와 패스워드가 맞았을 때
                    Toast.makeText(
                        this,
                        "로그인 성공",
                        Toast.LENGTH_SHORT
                    ).show()
                    moveMainPage(task.result?.user)
                } else {
                    Toast.makeText(
                        this,
                        "아이디와 비밀번호를 다시 한번 확인해 주세요",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
    fun moveMainPage(user:FirebaseUser?) {
        // 파이어베이스 유저 상태가 있을 경우 다음 페이지로 넘어갈 수 있음
        if(user != null) {
            startActivity(Intent(this, MainPage::class.java))
        }
    }
}