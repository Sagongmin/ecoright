package com.example.myapplication.ui.theme.login
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import android.widget.Toast
import android.widget.EditText
import android.widget.TextView

import com.example.myapplication.ui.theme.login.join_membership
import com.example.myapplication.ui.theme.home.mainMenu
import com.example.myapplication.ui.theme.transaction.BuyitemActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class login : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    private lateinit var email_edittext: EditText
    private lateinit var password_edittext: EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_home)
        supportActionBar?.hide()


        auth = FirebaseAuth.getInstance()

        email_edittext = findViewById(R.id.id_editText)
        password_edittext = findViewById(R.id.pass_editText)

        val goToJoinButton = findViewById<TextView>(R.id.signinbutton)

        goToJoinButton.setOnClickListener {
            val intent = Intent(this, join_membership::class.java)
            startActivity(intent)
        }
        val loginButton = findViewById<TextView>(R.id.loginbutton)
        loginButton.setOnClickListener {
            signinEmail()
        }

        val goToSearchid = findViewById<TextView>(R.id.search_id_pass)

        if (goToSearchid != null) {
            goToSearchid.setOnClickListener {
                val intent = Intent(this, search_id_pass::class.java)
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



    fun signinEmail() {
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful) {
                    // Login, 아이디와 패스워드가 맞았을 때
                    val user = FirebaseAuth.getInstance().currentUser
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val getDatabase = FirebaseDatabase.getInstance()
                    val database = getDatabase.reference
                    val userGetRef = getDatabase.getReference("users").child(userId!!)

                    if (user != null) {
                        userGetRef.child("username").addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val username = dataSnapshot.value.toString()
                                    Toast.makeText(this@login, // 반드시 현재 액티비티의 컨텍스트로 바꿔야 합니다.
                                        "로그인 완료\n 현재 사용자: $username",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // 데이터를 읽는 중 오류가 발생한 경우 처리할 내용을 여기에 추가할 수 있습니다.
                            }
                        })

                        val newPassword = password_edittext.text.toString()
                        val userReference = database.child("users").child(userId!!)
                        userReference.child("password").setValue(newPassword) // 사용자 데이터를 업데이트
                        moveMainPage(task.result?.user)
                    } else {
                        Toast.makeText(
                            this,
                            "사용자가 로그인되어 있지 않습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
            startActivity(Intent(this, mainMenu::class.java))
        }
    }

}