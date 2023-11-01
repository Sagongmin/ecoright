package com.example.myapplication.ui.theme.main.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.myapplication.ui.theme.main.util.ActivityTransitionUtil
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import io.karn.notify.Notify
import timber.log.Timber


class ActivityTransitionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            val mostProbableActivity = result.mostProbableActivity
            val activityType = mostProbableActivity.type
            val confidence = mostProbableActivity.confidence
            val activityName: String = getActivityName(activityType)
            Timber.d("Detected activity: %s, Confidence: %d%%", activityName, confidence)

            Notify
                .with(context)
                .content {
                    title = "Activity Detected"
                    text =
                        "I can see you are in ${ActivityTransitionUtil.toActivityString(activityType)} state"
                }
                .show(999)
            // 필요한 작업 수행
        }

        /*if (ActivityTransitionResult.hasResult(intent)){
            val result = ActivityTransitionResult.extractResult(intent)
            result?.let {
                result.transitionEvents.forEach { event ->

                    val info =
                        "Transition: ${ActivityTransitionUtil.toActivityString(event.activityType)} - ${
                            ActivityTransitionUtil.toTransitionType(
                                event.transitionType
                            )
                        }"

                    Timber.d(info)

                    Notify
                        .with(context)
                        .content {
                            title = "Activity Detected"
                            text =
                                "I can see you are in ${ActivityTransitionUtil.toActivityString(event.activityType)} state"
                        }
                        .show(999)
                }
            }
        }*/
    }

    private fun getActivityName(activityType: Int): String {
        return when (activityType) {
            DetectedActivity.IN_VEHICLE -> "In Vehicle"
            DetectedActivity.ON_BICYCLE -> "On Bicycle"
            DetectedActivity.ON_FOOT -> "On Foot"
            DetectedActivity.RUNNING -> "Running"
            DetectedActivity.STILL -> "Still"
            DetectedActivity.TILTING -> "Tilting"
            DetectedActivity.WALKING -> "Walking"
            DetectedActivity.UNKNOWN -> "Unknown"
            else -> "Unknown"
        }
    }
}