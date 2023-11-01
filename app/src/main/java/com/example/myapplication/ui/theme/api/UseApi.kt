package com.example.myapplication.ui.theme.api

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.example.myapplication.R
import com.google.android.gms.location.*

class UseApi : Activity() {
    private lateinit var activityRecognitionClient: ActivityRecognitionClient
    private lateinit var tvActivityStatus: TextView

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("UseApiReceiver", "Activity update received in UseApi")
            val detectedActivity = intent?.getIntExtra("activity_type", -1) ?: -1
            tvActivityStatus.text = getActivityNameFromType(detectedActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.use_api)
        Toast.makeText(this, "UseApi Activity created", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, UseApi.ACTIVITY_INTENT_FILTER, Toast.LENGTH_LONG).show()

        activityRecognitionClient = ActivityRecognition.getClient(this)
        tvActivityStatus = findViewById(R.id.tvActivity)

        findViewById<Button>(R.id.btnDetectActivity).setOnClickListener {

                requestActivityUpdates()
                Toast.makeText(this, "yeah", Toast.LENGTH_SHORT).show()


        }
    }


    private fun requestActivityUpdates() {
        val intent = Intent(this, ActivityDetectionBroadcastReceiver::class.java)
        intent.action = "com.example.myapplication.ui.theme.api.UseApi.activity_intent_filter"

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        try {
            ActivityRecognition.getClient(this)
                .requestActivityUpdates(1000, pendingIntent)
                .addOnSuccessListener {
                    Log.d("ActivityDetection", "Successfully requested activity updates")
                    Toast.makeText(this, "Successfully requested activity updates", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.d("ActivityDetection", "Requesting activity updates failed", e)
                    Toast.makeText(this, "Error: Failed to request activity updates", Toast.LENGTH_SHORT).show()
                }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Error: Permission denied!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getActivityNameFromType(activityType: Int): String {
        return when (activityType) {
            DetectedActivity.WALKING -> "Walking"
            DetectedActivity.RUNNING -> "Running"
            DetectedActivity.STILL -> "Still"
            DetectedActivity.ON_BICYCLE -> "On Bicycle"
            DetectedActivity.IN_VEHICLE -> "In Vehicle"
            DetectedActivity.TILTING -> "Tilting"
            DetectedActivity.UNKNOWN -> "Unknown"
            else -> "Unrecognized activity"
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(".ui.theme.api.UseApi.activity_intent_filter"))
        Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show()


    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
        Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show()

    }

    companion object {
        const val REQUEST_CODE = 1234
        const val ACTIVITY_INTENT_FILTER = "activity_intent_filter"
    }
}

class ActivityDetectionBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("ActivityDetection", "onReceive called!")
        Toast.makeText(context, "Successfully", Toast.LENGTH_SHORT).show()
        Log.d("ActivityDetection", "Received broadcast!")
        if (intent != null) {
            if (ActivityRecognitionResult.hasResult(intent)) {
                val result: ActivityRecognitionResult? = ActivityRecognitionResult.extractResult(intent)
                result?.let {
                    val detectedActivity = it.mostProbableActivity
                    Log.d("ActivityDetection", "Detected activity: ${detectedActivity.type}")
                    Toast.makeText(context, "Detected activity: ${detectedActivity.type}", Toast.LENGTH_SHORT).show()

                    val broadcastIntent = Intent("com.example.myapplication.ui.theme.api.UseApi.activity_intent_filter").apply {
                        putExtra("activity_type", detectedActivity.type)
                    }
                    context?.sendBroadcast(broadcastIntent)
                }
            } else {
                Log.d("ActivityDetection", "Intent does not have ActivityRecognitionResult.")
            }
        } else {
            Log.d("ActivityDetection", "Received null intent.")
        }
    }
}





