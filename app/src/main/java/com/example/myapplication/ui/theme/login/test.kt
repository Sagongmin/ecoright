package com.example.myapplication.ui.theme.login


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class test : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.market) // 'market'은 market.xml 파일의 이름
    }
}

