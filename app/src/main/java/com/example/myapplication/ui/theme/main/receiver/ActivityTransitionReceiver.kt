package com.example.myapplication.ui.theme.main.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import io.karn.notify.Notify
import timber.log.Timber


class ActivityTransitionReceiver: BroadcastReceiver() {
    companion object {
        val activityStartTimes = mutableMapOf<Int, Long>()
    }
    override fun onReceive(context: Context, intent: Intent) {
        /*if (ActivityRecognitionResult.hasResult(intent)) {
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
        }*/

        if (ActivityTransitionResult.hasResult(intent)){
            val result = ActivityTransitionResult.extractResult(intent)
            result?.let {
                for (event in result.transitionEvents) {
                    // 현재 이벤트의 타입과 시간을 얻습니다.
                    val activityType = event.activityType
                    val eventTime = event.elapsedRealTimeNanos / 1_000_000 // 나노초를 밀리초로 변환

                    // 종료 이벤트일 경우 지속 시간을 계산합니다.
                    if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
                        val startTime = activityStartTimes.remove(activityType)
                        if (startTime != null) {
                            val timeSpent = eventTime - startTime
                            val activityName = getActivityName(activityType)

                            val info = "Transition: $activityName - Exit, Duration: $timeSpent ms"
                            Timber.d(info)

                            Toast.makeText(context, "$activityName 행동 탈출, 행동간 간격: $timeSpent ms", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // 진입 이벤트일 경우 시작 시간을 저장합니다.
                    if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                        activityStartTimes[activityType] = eventTime

                        val activityName = getActivityName(activityType)
                        val info = "Transition: $activityName - Enter"
                        Timber.d(info)

                        Toast.makeText(context, "현재 행동: $activityName", Toast.LENGTH_SHORT).show()

                        Notify
                            .with(context)
                            .content {
                                title = "Activity Detected"
                                text =
                                    "지금 행동 $activityName 상태"
                            }
                            .show(999)
                    }

                }
            }
        }
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