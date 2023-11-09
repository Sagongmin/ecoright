package com.example.myapplication.ui.theme.login

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import android.widget.Button
import android.widget.EditText

class join_membership : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_join)

        auth = FirebaseAuth.getInstance()

        val btn_login = findViewById<Button>(R.id.btn_login)
        val join_editid = findViewById<EditText>(R.id.join_editid)
        val join_editpass = findViewById<EditText>(R.id.join_editpass)
        val join_re_editpass = findViewById<EditText>(R.id.join_re_editpass)

        btn_login.setOnClickListener {
            val email = join_editid.text.toString()
            val password = join_editpass.text.toString()
            val repasswd = join_re_editpass.text.toString()

            if(password != repasswd){
                Toast.makeText(
                    this,
                    "비밀번호가 서로 다릅니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else if (email.isNotEmpty() && password.isNotEmpty()) {
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
    }
}