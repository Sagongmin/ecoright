package com.example.myapplication.ui.theme.login


import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import android.widget.Toast
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase



class search_id_pass : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    private var newPassword: String = "" // 클래스 내부 변수로 newPassword 선언


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_in)
        supportActionBar?.hide()


        val findPasswordButton = findViewById<TextView>(R.id.changePass)
        val cancleButton = findViewById<TextView>(R.id.cancel_button)
        findPasswordButton.setOnClickListener {

            val editTextPassword = findViewById<EditText>(R.id.search_email)
            val newPassword = editTextPassword.text.toString()

            if (user != null) {
                user.updatePassword(newPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            findPassword()

                        } else {
                            Toast.makeText(this, "Password update failed.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }

        cancleButton.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun findPassword() {
        val searchIdEditText = findViewById<EditText>(R.id.search_email)
        FirebaseAuth.getInstance().sendPasswordResetEmail(searchIdEditText.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "비밀번호 변경 메일을 전송했습니다", Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(this, "비밀번호 변경에 실패했습니다.", Toast.LENGTH_LONG).show()
                }
            }
    }


}

