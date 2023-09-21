package com.example.myapplication.ui.theme.transaction
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.myapplication.R
import android.widget.Button
import android.widget.EditText
class SellitemActivity : AppCompatActivity() {

    // Firebase Database 참조 설정
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell)

        // Firebase Realtime Database 초기화
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.reference.child("탄소배출권") // "탄소배출권" 노드 설정

        val sellerIDEditText = findViewById<EditText>(R.id.sellerIDEditText)
        val itemCountEditText = findViewById<EditText>(R.id.itemCountEditText)
        val sellButton = findViewById<Button>(R.id.sellButton)
        val itemPriceEditText = findViewById<EditText>(R.id.itemPriceEditText)

        // 판매 버튼 클릭 리스너 설정
        sellButton.setOnClickListener {
            // 입력값 가져오기
            val sellerID = sellerIDEditText.text.toString()
            val itemCount = itemCountEditText.text.toString().toInt()
            val itemPrice = itemPriceEditText.text.toString().toDouble()

            // 데이터를 Map 형태로 만듭니다.
            val carbonCreditData = HashMap<String, Any>()
            carbonCreditData["판매자ID"] = sellerID
            carbonCreditData["판매물품개수"] = itemCount
            carbonCreditData["판매물품가격"] = itemPrice

            // 데이터를 Firebase에 삽입
            databaseReference.push().setValue(carbonCreditData) // 고유한 키를 생성하여 데이터를 쓰기

            // 데이터 입력 후 화면 초기화 또는 다른 작업 수행
            sellerIDEditText.text.clear()
            itemCountEditText.text.clear()
            itemPriceEditText.text.clear()
        }

        val goToBuyButton = findViewById<Button>(R.id.goToBuyButton)

        goToBuyButton.setOnClickListener {
            val intent = Intent(this, BuyitemActivity::class.java)
            startActivity(intent)
        }
    }
}