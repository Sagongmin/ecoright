    package com.example.myapplication.ui.theme.transaction
    import android.content.Intent
    import android.os.Bundle
    import android.util.Log
    import androidx.appcompat.app.AppCompatActivity
    import com.google.firebase.database.DatabaseReference
    import com.google.firebase.database.FirebaseDatabase
    import com.example.myapplication.R
    import android.widget.Button
    import android.widget.EditText
    import android.widget.Toast
    import com.example.myapplication.ui.theme.home.mainMenu
    import com.example.myapplication.ui.theme.login.login
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.ValueEventListener

    class SellitemActivity : AppCompatActivity() {

        // Firebase Database 참조 설정
        private lateinit var databaseReference: DatabaseReference

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_sell)

            // Firebase Realtime Database 초기화
            val database = FirebaseDatabase.getInstance()
            databaseReference = database.reference.child("탄소배출권") // "탄소배출권" 노드 설정

            val itemCountEditText = findViewById<EditText>(R.id.itemCountEditText)
            val sellButton = findViewById<Button>(R.id.sellButton)
            val itemPriceEditText = findViewById<EditText>(R.id.itemPriceEditText)

            sellButton.setOnClickListener {
                // 입력값 가져오기
                val currentUser = FirebaseAuth.getInstance().currentUser

                if (currentUser != null) {
                    val currentUserUid = currentUser.uid
                    var sellerID = ""

                    val nameref = FirebaseDatabase.getInstance().getReference("users").child(currentUserUid)
                    nameref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // dataSnapshot에서 username을 가져와서 사용할 수 있습니다.
                                val sellerUsername = dataSnapshot.child("username").getValue(String::class.java)

                                if (sellerUsername != null) {
                                    sellerID = sellerUsername

                                    // 판매자 ID를 가져온 후에 판매 물품 데이터를 Firebase에 쓰도록 코드를 이곳에 배치
                                    val itemCount = itemCountEditText.text.toString().toInt()
                                    val itemPrice = itemPriceEditText.text.toString().toDouble()

                                    // 데이터를 Firebase에 삽입
                                    val newItemReference = databaseReference.child("users").child(currentUserUid).push()
                                    val newItemKey = newItemReference.key

                                    // 데이터를 Map 형태로 만듭니다.
                                    val carbonCreditData = HashMap<String, Any>()
                                    if (newItemKey != null) {
                                        carbonCreditData["key"] = newItemKey // 생성된 키를 판매 물품 데이터에 저장
                                    } else {
                                        // 키가 null이면 처리 방법을 정의하세요. 예를 들어 오류 메시지 표시 등
                                    }
                                    carbonCreditData["판매자ID"] = currentUserUid
                                    carbonCreditData["username"] = sellerID
                                    carbonCreditData["판매물품개수"] = itemCount
                                    carbonCreditData["판매물품가격"] = itemPrice

                                    newItemReference.setValue(carbonCreditData) // 고유한 키를 생성하여 데이터를 쓰기
                                    //databaseReference.child(key).setValue(carbonCreditData) // 고유한 키를 생성하여 데이터를 쓰기

                                    // 데이터 입력 후 화면 초기화 또는 다른 작업 수행
                                    itemCountEditText.text.clear()
                                    itemPriceEditText.text.clear()
                                } else {
                                    // username이 없는 경우 처리
                                    Toast.makeText(this@SellitemActivity, "사용자 이름을 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // 데이터가 없는 경우 처리
                                Toast.makeText(this@SellitemActivity, "데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // 데이터베이스에서 데이터를 불러오지 못한 경우 처리
                            Toast.makeText(this@SellitemActivity, "데이터를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this, "사용자가 로그인되어 있지 않습니다. 로그인을 해주세요.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, login::class.java) // 로그인 화면으로 리디렉션
                    startActivity(intent)
                    finish()
                }
            }

            val goToBuyButton = findViewById<Button>(R.id.goToBuyButton)

            goToBuyButton.setOnClickListener {
                val intent = Intent(this, BuyitemActivity::class.java)
                startActivity(intent)
                finish()
            }

            val goToHomeButton = findViewById<Button>(R.id.goToHomeButton)

            goToHomeButton.setOnClickListener {
                val intent = Intent(this, mainMenu::class.java)
                startActivity(intent)
                finish()
            }
        }
    }