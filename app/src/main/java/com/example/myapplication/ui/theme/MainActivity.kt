package com.example.myapplication.ui.theme

import com.example.myapplication.R
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {
    // 파이어베이스 데이터베이스 연동
    private val database = FirebaseDatabase.getInstance()

    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    //현재 연결은 데이터베이스에만 딱 연결해놓고
    //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
    private val databaseReference = database.reference
    var btn: Button? = null
    var edit1: EditText? = null
    var edit2: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn = findViewById<Button>(R.id.btn) //버튼 아이디 연결
        edit1 = findViewById<EditText>(R.id.edit1) //동물 이름 적는 곳
        edit2 = findViewById<EditText>(R.id.edit2) //동물 종류 적는 곳


        //버튼 누르면 값을 저장
        btn?.setOnClickListener(View.OnClickListener { //에딧 텍스트 값을 문자열로 바꾸어 함수에 넣어줍니다.
            addanimal(edit1?.text.toString(), edit2?.text.toString())
        })
    }

    //값을 파이어베이스 Realtime database로 넘기는 함수
    fun addanimal(name: String, kind: String) {

        //여기에서 직접 변수를 만들어서 값을 직접 넣는것도 가능합니다.
        // ex) 갓 태어난 동물만 입력해서 int age=1; 등을 넣는 경우

        //animal.java에서 선언했던 함수.
        val animal = animal(name, kind)

        //child는 해당 키 위치로 이동하는 함수입니다.
        //키가 없는데 "zoo"와 name같이 값을 지정한 경우 자동으로 생성합니다.
        databaseReference.child("zoo").push().setValue(animal)
    }
}