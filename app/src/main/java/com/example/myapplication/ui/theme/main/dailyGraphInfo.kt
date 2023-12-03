package com.example.myapplication.ui.theme.main

import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
class dailyGraphInfo : AppCompatActivity() {
    private var currentDate: Calendar = Calendar.getInstance()
    private lateinit var pieChart: PieChart
    private val maxPoints = 500 // 최대 포인트 설정
    private val pointsPerMinute = 10 // 분당 포인트 설정
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.dailyinfo)

        pieChart = findViewById<PieChart>(R.id.pieChart)

        //val dateTextView = findViewById<TextView>(R.id.dateTextView)
        val prevButton = findViewById<ImageView>(R.id.prevButton)
        val nextButton = findViewById<ImageView>(R.id.nextButton)

        updateDateDisplay() // 현재 날짜를 표시
        loadDataForDate(currentDate.time) // 초기 데이터 로드

        prevButton.setOnClickListener {
            currentDate.add(Calendar.DAY_OF_MONTH, -1)
            updateDateDisplay()
            loadDataForDate(currentDate.time)
        }

        nextButton.setOnClickListener {
            currentDate.add(Calendar.DAY_OF_MONTH, 1)
            updateDateDisplay()
            loadDataForDate(currentDate.time)
        }
    }

    private fun updateDateDisplay() {
        val dateTextView = findViewById<TextView>(R.id.dateTextView)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateTextView.text = dateFormat.format(currentDate.time)
    }

    private fun loadDataForDate(date: Date) {
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        // 파이어베이스에서 formattedDate에 해당하는 데이터 로드 및 UI 업데이트
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val pointsReference = FirebaseDatabase.getInstance().getReference("users/$userId/walkingTimeDates/$formattedDate")

        pointsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 데이터 로드 및 처리
                val walkingTimeMillis = snapshot.getValue(Int::class.java) ?: 0
                val walkingTimeMinutes = walkingTimeMillis / 1000 / 60
                val points = (walkingTimeMinutes * pointsPerMinute).coerceAtMost(maxPoints)
                updatePieChart(points) // PieChart 업데이트
                setupPieChart(points,maxPoints)
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류 처리
            }
        })
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
    private fun updatePieChart(points: Int) {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(points.toFloat())) // 현재 포인트
        entries.add(PieEntry((maxPoints - points).toFloat())) // 남은 포인트

        val dataSet = PieDataSet(entries, "Categories").apply {
            setColors(intArrayOf(R.color.lightGreen, R.color.lightGray),this@dailyGraphInfo)
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