package com.example.myapplication.ui.theme.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.ui.theme.transaction.SellitemActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class mainMenu : AppCompatActivity(){

    private lateinit var some_id: TextView
    private lateinit var pieChart: PieChart
    private val maxPoints = 500 // 최대 포인트 설정
    private val pointsPerMinute = 10 // 분당 포인트 설정
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_home)
        supportActionBar?.hide()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.format(Date()) // 오늘 날짜를 "yyyy-MM-dd" 포맷으로 가져옵니다.
        pieChart = findViewById<PieChart>(R.id.pieChart)

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

        val pointsReference = FirebaseDatabase.getInstance().getReference("users/$userId/walkingTimeDates")
        pointsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 이 부분은 데이터가 변경될 때마다 호출됩니다.
                val allPoints = snapshot.getValue(object : GenericTypeIndicator<HashMap<String, Int>>() {})
                allPoints?.let {
                    val walkingTimeMillis =  it[date] ?: 0  //임시로 오늘 걸음으로 얻은 포인트 나타냄
                    // 밀리초를 분으로 변환합니다.
                    val walkingTimeMinutes = walkingTimeMillis / 1000 / 60
                    val todayWalkingTimePoints = walkingTimeMinutes*pointsPerMinute
                    val point = findViewById<TextView>(R.id.point)
                    point.text = "${todayWalkingTimePoints}pt"
                    setupPieChart(todayWalkingTimePoints,maxPoints)
                }
            }



            override fun onCancelled(error: DatabaseError) {
                // 데이터 가져오기 실패
                Log.w("DatabaseError", "loadPoints:onCancelled", error.toException())
            }
        })

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
            
        }

        goToOptionButton.setOnClickListener{//기타 설정 버튼
            
        }

        updateWalkingPoints()
    }

    private fun setupPieChart(todayWalkingTimePoints: Int, maxPoints: Int) {
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.holeRadius = 80f
        pieChart.transparentCircleRadius = 80f
        pieChart.centerText = "일일 한도 크레딧\n$todayWalkingTimePoints"+"/"+"$maxPoints"
        pieChart.legend.isEnabled = false
        pieChart.isRotationEnabled = false
        pieChart.isHighlightPerTapEnabled = false
        // 기타 차트 설정...
    }

    private fun updateWalkingPoints() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val pointsReference = FirebaseDatabase.getInstance().getReference("users/$userId/walkingTimeDates/$date")

        pointsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 밀리초 단위의 걷는 시간을 가져옵니다.
                val walkingTimeMillis = snapshot.getValue(Int::class.java) ?: 0
                // 밀리초를 분으로 변환합니다.
                val walkingTimeMinutes = walkingTimeMillis / 1000 / 60
                // 분당 포인트 설정에 따라 포인트를 계산합니다.
                val points = (walkingTimeMinutes * pointsPerMinute).coerceAtMost(maxPoints)

                updatePieChart(points)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("DatabaseError", "loadWalkingTime:onCancelled", error.toException())
            }
        })
    }

    private fun updatePieChart(points: Int) {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(points.toFloat())) // 현재 포인트
        entries.add(PieEntry((maxPoints - points).toFloat())) // 남은 포인트

        val dataSet = PieDataSet(entries, "Categories").apply {
            setColors(intArrayOf(R.color.lightGreen, R.color.lightGray),this@mainMenu)
            valueTextColor = Color.WHITE
            valueTextSize = 12f

        }

        val data = PieData(dataSet).apply {
            setDrawValues(false) // 값이 차트에 표시되지 않게 설정
        }

        pieChart.data = data
        pieChart.invalidate() // 차트 갱신
    }
}