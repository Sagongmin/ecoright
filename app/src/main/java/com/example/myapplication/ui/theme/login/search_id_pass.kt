package com.example.myapplication.ui.theme.login


import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import android.widget.Toast
import android.widget.EditText


class search_id_pass : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_id)

        val user = auth.currentUser
        val newPassword = "SOME-SECURE-PASSWORD"

        val findPasswordButton = findViewById<Button>(R.id.find_password_button)
        findPasswordButton.setOnClickListener {
            if (user != null) {
                user.updatePassword(newPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            findPassword()
                        } else {
                            Toast.makeText(this, "Password update failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun findPassword() {
        val searchIdEditText = findViewById<EditText>(R.id.id_input)
        FirebaseAuth.getInstance().sendPasswordResetEmail(searchIdEditText.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "비밀번호 변경 메일을 전송했습니다", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
    }
}

