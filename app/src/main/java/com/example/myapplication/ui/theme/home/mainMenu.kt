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

    private var walkingTimeListenerRegistered = false

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
            
        }

        goToOptionButton.setOnClickListener{//기타 설정 버튼
            
        }

        lastWalkingTimeMinutes = 0 // 초기화
        setupWalkingTimeListener()
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

    private var lastWalkingTimeMinutes: Int = -1

    private fun setupWalkingTimeListener() {

        if (walkingTimeListenerRegistered) return

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val walkingTimeRef = FirebaseDatabase.getInstance().getReference("users/$userId/walkingTimeDates/$date")

        walkingTimeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val walkingTimeMillis = snapshot.getValue(Int::class.java) ?: 0
                val walkingTimeMinutes = walkingTimeMillis / 1000 / 60
                if (lastWalkingTimeMinutes == -1) {
                    // 처음 실행 시 현재 걸음 시간으로 초기화
                    lastWalkingTimeMinutes = walkingTimeMinutes
                } else if (walkingTimeMinutes != lastWalkingTimeMinutes) {
                    val additionalMinutes = walkingTimeMinutes - lastWalkingTimeMinutes
                    updateWalkingPoints(additionalMinutes)
                    lastWalkingTimeMinutes = walkingTimeMinutes
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류 처리
            }
        })

        walkingTimeListenerRegistered = true
    }

    private fun updateWalkingPoints(additionalMinutes: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val dailyPointsRef = FirebaseDatabase.getInstance().getReference("users/$userId/dailyPoints/$date")
        val totalPointsRef = FirebaseDatabase.getInstance().getReference("users/$userId/totalPoints")

        dailyPointsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dailySnapshot: DataSnapshot) {
                val currentDailyPoints = dailySnapshot.getValue(Int::class.java) ?: 0
                val additionalPoints = (additionalMinutes * pointsPerMinute).coerceAtMost(maxPoints)
                val newTotalPoints = currentDailyPoints + additionalPoints

                if (newTotalPoints <= maxPoints) {
                    dailyPointsRef.setValue(newTotalPoints)

                    totalPointsRef.runTransaction(object : Transaction.Handler {
                        override fun doTransaction(mutableData: MutableData): Transaction.Result {
                            var totalPoints = mutableData.getValue(Int::class.java) ?: 0
                            totalPoints += additionalPoints
                            mutableData.value = totalPoints
                            return Transaction.success(mutableData)
                        }

                        override fun onComplete(databaseError: DatabaseError?, b: Boolean, dataSnapshot: DataSnapshot?) {
                            // 추가 처리
                        }
                    })
                } else {
                    Toast.makeText(applicationContext, "오늘은 더 이상 포인트를 줄 수 없어요!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 오류 처리
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
        /*var detectionIntervalMillis = 0; // 10초

        client
            .requestActivityUpdates(
                detectionIntervalMillis.toLong(),
                getPendingIntent()
            )
            .addOnSuccessListener {
                Timber.d("Success - Request Updates")
            }
            .addOnFailureListener{
                Timber.d("Failure - Request Updates")
            }*/
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