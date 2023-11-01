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


class MainPage : AppCompatActivity() {

    companion object {
        const val TRANSITIONS_RECEIVER_ACTION = "TRANSITIONS_RECEIVER_ACTION"
    }
    private lateinit var sampleText: TextView
    private val transitions: List<ActivityTransition> by lazy {
        listOf(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        )
    }

    private val transitionReceiver by lazy {
        TransitionsReceiver()
    }
    private lateinit var request: ActivityTransitionRequest
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page)

        sampleText = findViewById(R.id.sample_text)

        checkAndRequestPermissions()

        request = ActivityTransitionRequest(transitions)
        /*val intent =  Intent().apply {
            action = TRANSITIONS_RECEIVER_ACTION
        }*/
        val intent = Intent(TRANSITIONS_RECEIVER_ACTION)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        val testBroadcastButton: Button = findViewById(R.id.testBroadcastButton)
        testBroadcastButton.setOnClickListener {
            sendTestBroadcast()
        }
    }

    private fun sendTestBroadcast() {
        val intent = Intent(TRANSITIONS_RECEIVER_ACTION)
        sendBroadcast(intent)
    }

    /*private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 0)
        }
    }*/

    private fun checkAndRequestPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        // ACTIVITY_RECOGNITION permission check for Android Q and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)) {
            permissionsNeeded.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }

        // READ_PHONE_STATE permission check
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            permissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
        }

        // ACCESS_FINE_LOCATION permission check
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // If any permissions are needed, request them
        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), 0)
        }
    }

    private fun printToScreen(message: String) {
        val appendMessage = "${sampleText.text}\n$message"
        sampleText.text = appendMessage
    }

    private fun registerActivityTransitionUpdates() {
        ActivityRecognition.getClient(this)
            .requestActivityTransitionUpdates(request, pendingIntent)
            .addOnSuccessListener {
                printToScreen("Transitions API was successfully registered")
                Log.d("MainPage", "API registered!")
            }.addOnFailureListener { e ->
                printToScreen("Transitions Api could not be registered : $e")
                Log.d("MainPage", "API not registered!")
            }
    }

    private fun unregisterActivityTransitionUpdates() {
        ActivityRecognition.getClient(this)
            .removeActivityTransitionUpdates(pendingIntent)
            .addOnSuccessListener {
                printToScreen("Transitions successfully unregistered.")
            }
            .addOnFailureListener { e ->
                printToScreen("Transitions could not be unregistered : $e")
            }
    }

    override fun onStart() {
        Log.d("TransitionsReceiver", "start!!!")
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)) {
            registerActivityTransitionUpdates()
            Log.d("MainPage", "Update complete")
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            registerReceiver(transitionReceiver, IntentFilter(TRANSITIONS_RECEIVER_ACTION), null, null, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(transitionReceiver, IntentFilter(TRANSITIONS_RECEIVER_ACTION))
        }*/
        registerReceiver(transitionReceiver, IntentFilter(TRANSITIONS_RECEIVER_ACTION))
        Log.d("MainPage", "Registered TransitionsReceiver")
    }

    override fun onPause() {
        unregisterActivityTransitionUpdates()
        super.onPause()
    }

    override fun onStop() {
        ActivityRecognition.getClient(this).removeActivityTransitionUpdates(pendingIntent)
        unregisterReceiver(transitionReceiver)
        super.onStop()
    }

    inner class TransitionsReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("TransitionsReceiver", "Received a broadcast!") // 브로드캐스트를 받았는지 확인

            if (intent?.action != TRANSITIONS_RECEIVER_ACTION) {
                Log.d("TransitionsReceiver", "NonTransition")
                return
            }

            if (ActivityTransitionResult.hasResult(intent)) {
                Log.d("TransitionsReceiver", "YesTransition") // 전환 결과가 있는 경우
                val result: ActivityTransitionResult =
                    ActivityTransitionResult.extractResult(intent) ?: return

                for (event in result.transitionEvents) {
                    val transitionType =
                        if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) "ENTER" else "EXIT"
                    val activityType = when (event.activityType) {
                        DetectedActivity.WALKING -> "WALKING"
                        DetectedActivity.STILL -> "STILL"
                        // 여기에 다른 활동 유형을 추가할 수 있습니다.
                        else -> "UNKNOWN"
                    }
                    val message = "Transition: $activityType ($transitionType)"
                    Log.d("TransitionsReceiver", message)
                    printToScreen(message)
                }
            } else {
                // ActivityTransitionResult.hasResult(intent)가 false를 반환하는 경우
                Log.d("TransitionsReceiver", "No transition result in the intent")
            }
        }
    }
}