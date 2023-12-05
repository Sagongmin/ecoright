package com.example.myapplication.ui.theme.home

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.ui.theme.main.receiver.ActivityTransitionReceiver
import com.example.myapplication.ui.theme.main.util.ActivityTransitionUtil
import com.example.myapplication.ui.theme.main.util.Constants
import com.example.myapplication.ui.theme.market.markethome
import com.example.myapplication.ui.theme.transaction.SellitemActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class mainMenu : AppCompatActivity(), EasyPermissions.PermissionCallbacks{

    private var currentDate: Calendar = Calendar.getInstance()
    private lateinit var client: ActivityRecognitionClient
    private lateinit var some_id: TextView
    private lateinit var pieChart: PieChart
    private val maxPoints = 500 // 최대 포인트 설정
    private val pointsPerMinute = 10 // 분당 포인트 설정
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_home)
        supportActionBar?.hide()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.format(Date()) // 오늘 날짜를 "yyyy-MM-dd" 포맷으로 가져옵니다.
        pieChart = findViewById<PieChart>(R.id.pieChart)

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

        val dailyPointsRef = FirebaseDatabase.getInstance().getReference("users/$userId/dailyPoints/$date")
        val totalPointsRef = FirebaseDatabase.getInstance().getReference("users/$userId/totalPoints")
        totalPointsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Firebase에서 계산된 포인트 값을 가져와서 표시합니다.
                val points = snapshot.getValue(Int::class.java) ?: 0
                val pointTextView = findViewById<TextView>(R.id.point)
                pointTextView.text = "${points}pt"
            }

            override fun onCancelled(error: DatabaseError) {
                // 데이터 가져오기 실패
                Log.w("DatabaseError", "loadPoints:onCancelled", error.toException())
            }
        })

        dailyPointsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Firebase에서 계산된 포인트 값을 가져와서 표시합니다.
                val dailyPoints = snapshot.getValue(Int::class.java) ?: 0
                setupPieChart(dailyPoints, maxPoints)
                setupPieChart(dailyPoints, maxPoints)
                updatePieChart(dailyPoints)
            }

            override fun onCancelled(error: DatabaseError) {
                // 데이터 가져오기 실패
                Log.w("DatabaseError", "loadPoints:onCancelled", error.toException())
            }
        })

        client = ActivityRecognition.getClient(this)

        Timber.plant(Timber.DebugTree())

        findViewById<SwitchMaterial>(R.id.switchActivityTransition).setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                if(!ActivityTransitionUtil.hasActivityTransitionPermission(context = this)
                ) {
                    findViewById<SwitchMaterial>(R.id.switchActivityTransition).isChecked = false
                    requestActivityTransitionPermission()
                } else requestForUpdates()

            }else{
                removeUpdates()
            }
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
        val dailyPointsRef = FirebaseDatabase.getInstance().getReference("users/$userId/dailyPoints/$formattedDate")

        dailyPointsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dailyPoints = snapshot.getValue(Int::class.java) ?: 0
                updatePieChart(dailyPoints) // PieChart 업데이트
                setupPieChart(dailyPoints, maxPoints)
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
        pieChart.centerText = "일일 한도 포인트\n$todayWalkingTimePoints"+"/"+"$maxPoints"
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

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        findViewById<SwitchMaterial>(R.id.switchActivityTransition).isChecked = true
        requestForUpdates()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestActivityTransitionPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    private fun requestForUpdates() {
        client
            .requestActivityTransitionUpdates(
                ActivityTransitionUtil.getTransitionRequest(),
                getPendingIntent()
            )
            .addOnSuccessListener {
                Timber.d("success - Request Updates")
            }
            .addOnFailureListener{
                Timber.d("Failure - Request Updates")
            }
    }

    private fun removeUpdates() {
        client
            .removeActivityUpdates(getPendingIntent())
    }
    private fun getPendingIntent(): PendingIntent {
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
        ) {
            val intent = Intent(this, ActivityTransitionReceiver::class.java)
            intent.action = "com.example.myapplication.ui.theme.main.activity_intent_filter"
            return PendingIntent.getBroadcast(
                this,
                Constants.ACTIVITY_TRANSITION_REQUEST_CODE_RECEIVER,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            val intent = Intent(this, ActivityTransitionReceiver::class.java)
            intent.action = "com.example.myapplication.ui.theme.main.activity_intent_filter"
            return PendingIntent.getBroadcast(
                this,
                Constants.ACTIVITY_TRANSITION_REQUEST_CODE_RECEIVER,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        }
    }

    private fun requestActivityTransitionPermission() {
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
        ) {
            EasyPermissions.requestPermissions(
                this,
                "you need to allow activity transition permissions in order to use this feature.",
                Constants.ACTIVITY_TRANSITION_REQUEST_CODE,
                //Manifest.permission.ACTIVITY_RECOGNITION,
                "com.google.android.gms.permission.ACTIVITY_RECOGNITION",
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS,
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "you need to allow activity transition permissions in order to use this feature.",
                Constants.ACTIVITY_TRANSITION_REQUEST_CODE,
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS,
            )
        }
    }
}