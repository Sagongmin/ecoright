package com.example.myapplication.ui.theme.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.ui.theme.market.markethome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class co2Calculate : AppCompatActivity() {

    private lateinit var some_id: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.carboncalculate)
        supportActionBar?.hide()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.format(Date()) // 오늘 날짜를 "yyyy-MM-dd" 포맷으로 가져옵니다.

        some_id = findViewById(R.id.some_id)
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid // 사용자의 UID를 가져옵니다.

        val databaseReference = FirebaseDatabase.getInstance().getReference("users/$userId")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("username").getValue(String::class.java)
                userName?.let {
                    // TextView에 텍스트 설정
                    some_id.text = "   ${userName}님, 안녕하세요!"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("DatabaseError", "userNameNotFounded", error.toException())
            }
        })

        val goTocalBtn = findViewById<Button>(R.id.button_1)

        goTocalBtn.setOnClickListener {
            val intent = Intent(this, co2Calculate_electricity::class.java)
            startActivity(intent)
            finish()
        }

        val goTohomeButton = findViewById<ImageView>(R.id.homebutton)
        val goToco2Button = findViewById<ImageView>(R.id.co2button)
        val goTomarketButton = findViewById<ImageView>(R.id.marketbutton)
        val goToOptionButton = findViewById<ImageView>(R.id.optionbutton)

        goTohomeButton.setOnClickListener {//홈버튼
            val intent = Intent(this, mainMenu::class.java)
            startActivity(intent)
            finish()
        }

        goToco2Button.setOnClickListener {//co2 관련 버튼
            val intent = Intent(this, co2Calculate::class.java)
            startActivity(intent)
            finish()
        }

        goTomarketButton.setOnClickListener{//마켓 거래 관련 버튼
            val intent = Intent(this, markethome::class.java)
            startActivity(intent)
            finish()
        }

        goToOptionButton.setOnClickListener{//기타 설정 버튼

        }

        val pointsReference = FirebaseDatabase.getInstance().getReference("users/$userId/walkingTimeDates")
        pointsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 이 부분은 데이터가 변경될 때마다 호출됩니다.
                val allPoints =
                    snapshot.getValue(object : GenericTypeIndicator<HashMap<String, Int>>() {})
                allPoints?.let {
                    val walkingTimeMillis = it[date] ?: 0
                    // 밀리초를 초단위로 변환
                    val walkingTimeSeconds = walkingTimeMillis / 1000

                    val hours = walkingTimeSeconds / 3600
                    val minutes = (walkingTimeSeconds % 3600) / 60
                    val seconds = walkingTimeSeconds % 60

                    val lowerCarbonAmount = walkingTimeSeconds * 0.275

                    val formattedTime = formatTime(hours, minutes, seconds)
                    val textView = findViewById<TextView>(R.id.stack_walking)
                    val lowerCarbon = findViewById<TextView>(R.id.lowerCarbon)
                    textView.text = formattedTime
                    lowerCarbon.text = "${String.format("%.1f", lowerCarbonAmount)}g"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 데이터 가져오기 실패
                Log.w("DatabaseError", "loadPoints:onCancelled", error.toException())
            }
        })
    }

    private fun formatTime(hours: Int, minutes: Int, seconds: Int): String {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}