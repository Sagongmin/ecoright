package com.example.myapplication.ui.theme.main.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.myapplication.ui.theme.main.util.ActivityTransitionUtil
import com.google.android.gms.location.ActivityTransitionResult
import io.karn.notify.Notify
import timber.log.Timber

class ActivityTransitionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (ActivityTransitionResult.hasResult(intent)){
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
        }
    }
}