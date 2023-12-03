package com.example.myapplication.ui.theme.login

import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class join_membership : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        val btn_login = findViewById<TextView>(R.id.loginbutton)
        val join_editid = findViewById<EditText>(R.id.email_editText)
        val join_editpass = findViewById<EditText>(R.id.pass_editText)
        val btn_back = findViewById<TextView>(R.id.backbtn)
        //val join_re_editpass = findViewById<EditText>(R.id.join_re_editpass)

        btn_login.setOnClickListener {
            val email = join_editid.text.toString()
            val password = join_editpass.text.toString()
            //val repasswd = join_re_editpass.text.toString()

            /*if(password != repasswd){
                Toast.makeText(
                    this,
                    "비밀번호가 서로 다릅니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }*/
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // 회원가입 성공
                            val user = auth.currentUser
                            // 추가적인 작업 수행 (예: 회원 데이터베이스에 사용자 정보 추가)

                            val userId = user?.uid
                            if (userId != null) {
                                val databaseReference = FirebaseDatabase.getInstance().getReference("users")
                                val userData = HashMap<String, Any>()
                                userData["username"] = "temp_Nick"
                                userData["email"] = email
                                userData["password"] = password

                                databaseReference.child(userId).setValue(userData)

                                Toast.makeText(
                                    this,
                                    "회원가입 성공",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this, login::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            // 회원가입 실패
                            Toast.makeText(
                                this,
                                "회원가입 실패: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    this,
                    "이메일과 비밀번호를 입력하세요",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btn_back.setOnClickListener {
            Toast.makeText(
                this,
                "이전화면",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, login::class.java)
            startActivity(intent)
            finish()
        }
    }
}