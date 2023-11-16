package com.example.myapplication.ui.theme.main

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.utils.ColorTemplate
import android.graphics.Color
import android.widget.ImageView
import android.widget.Toast





class MainPage : AppCompatActivity() {

    private lateinit var greetingTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.main_page)


        // TextView 찾기
        greetingTextView = findViewById(R.id.greeting_text_view)

        // 사용자 이름 가져오기 (이 부분은 앱의 인증 시스템에 맞게 구현해야 함)
        val currentUserName = getCurrentUserName()

        // TextView에 텍스트 설정
        greetingTextView.text = "   ${currentUserName}님, 안녕하세요!"


        val pointsReference = FirebaseDatabase.getInstance().getReference("path_to_points")
        pointsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 이 부분은 데이터가 변경될 때마다 호출됩니다.
                val points = snapshot.getValue(Int::class.java) ?: 0
                val pointsTextView = findViewById<TextView>(R.id.eco_point_text_view)
                pointsTextView.text = "   나의 에코포인트는                                                      ${points}pt"

            }

            override fun onCancelled(error: DatabaseError) {
                // 데이터 가져오기 실패
                Log.w("DatabaseError", "loadPoints:onCancelled", error.toException())
            }
        })

        val pointreturnReference = FirebaseDatabase.getInstance().getReference("path_to_points")
        pointsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 이 부분은 데이터가 변경될 때마다 호출됩니다.
                val points = snapshot.getValue(Int::class.java) ?: 0
                val pointsTextView = findViewById<TextView>(R.id.now_returnpoint)
                pointsTextView.text = "${points} pt"

            }

            override fun onCancelled(error: DatabaseError) {
                // 데이터 가져오기 실패
                Log.w("DatabaseError", "loadPoints:onCancelled", error.toException())
            }
        })

        val pieChart = findViewById<PieChart>(R.id.pieChart)

        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.centerText = "My Donut Chart"
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.holeRadius = 50f // 중앙 홀의 반지름을 백분율로 설정
        pieChart.transparentCircleRadius = 55f // 투명한 원의 반지름을 백분율로 설정

        pieChart.legend.isEnabled = false



        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(40f, "Category 1"))
        entries.add(PieEntry(30f, "Category 2"))
        entries.add(PieEntry(30f, "Category 3"))

        val dataSet = PieDataSet(entries, "Categories")
        dataSet.setColors(*ColorTemplate.MATERIAL_COLORS) // 다양한 색상 사용

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate() // 차트 갱신

        val imageViewButton = findViewById<ImageView>(R.id.co2Button)
        imageViewButton.setOnClickListener {
            // 여기에 버튼 클릭 시 수행할 동작을 추가합니다.
            Toast.makeText(this, "이미지 클릭됨", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, com.example.myapplication.ui.theme.main.ActRcg::class.java))
        }

    }

    private fun getCurrentUserName(): String {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.displayName ?: "Unknown User"
    }


}