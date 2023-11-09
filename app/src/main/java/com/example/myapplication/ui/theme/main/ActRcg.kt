package com.example.myapplication.ui.theme.main

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.ui.theme.main.receiver.ActivityTransitionReceiver
import com.example.myapplication.ui.theme.main.util.ActivityTransitionUtil
import com.example.myapplication.ui.theme.main.util.Constants
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.material.switchmaterial.SwitchMaterial
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

class ActRcg : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var client: ActivityRecognitionClient

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actrcg)

        client = ActivityRecognition.getClient(this)

        Timber.plant(Timber.DebugTree())

        findViewById<SwitchMaterial>(R.id.switchActivityTransition).setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    !ActivityTransitionUtil.hasActivityTransitionPermission(context = this)
                ) {
                    findViewById<SwitchMaterial>(R.id.switchActivityTransition).isChecked = false
                    requestActivityTransitionPermission()
                } else requestForUpdates()

            }else{
                removeUpdates()
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        findViewById<SwitchMaterial>(R.id.switchActivityTransition).isChecked = true
        requestForUpdates()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
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
    private fun getPendingIntent(): PendingIntent? {
        val intent = Intent(this, ActivityTransitionReceiver::class.java)
        intent.action = "com.example.myapplication.ui.theme.main.activity_intent_filter"
        return PendingIntent.getBroadcast(
            this,
            Constants.ACTIVITY_TRANSITION_REQUEST_CODE_RECEIVER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestActivityTransitionPermission() {

        EasyPermissions.requestPermissions(
            this,
            "you need to allow activity transition permissions in order to use this feature.",
            Constants.ACTIVITY_TRANSITION_REQUEST_CODE,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }



}