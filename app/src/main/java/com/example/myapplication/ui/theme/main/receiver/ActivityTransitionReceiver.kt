package com.example.myapplication.ui.theme.main.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.myapplication.ui.theme.main.util.ActivityTransitionUtil
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.karn.notify.Notify
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

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
                        if ((activityType == DetectedActivity.ON_FOOT) ||
                            (activityType == DetectedActivity.WALKING) ||
                            (activityType == DetectedActivity.RUNNING)) {
                            val startTime = activityStartTimes.remove(activityType)
                            startTime?.let {
                                val timeSpent = eventTime - it
                                val activityName = getActivityName(activityType)

                                val info = "Transition: $activityName - Exit, Duration: $timeSpent ms"
                                Timber.d(info)
                                Toast.makeText(context, "$activityName 행동 탈출, 행동간 간격: $timeSpent ms", Toast.LENGTH_SHORT).show()

                                // 사용자 ID를 가져옵니다. 이 예에서는 임시로 "userId"를 사용합니다.
                                val user = FirebaseAuth.getInstance().currentUser
                                val userId = user?.uid // 사용자의 UID를 가져옵니다.
                                userId?.let { uid ->
                                    updateWalkingTimeInFirebase(uid, timeSpent)
                                }
                            }
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

    private fun updateWalkingTimeInFirebase(userId: String, timeSpentWalking: Long) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.format(Date()) // 오늘 날짜를 "yyyy-MM-dd" 포맷으로 가져옵니다.

        val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        val walkingTimeRef = databaseRef.child("walkingTimeDates").child(date)

        // 현재 저장된 걸은 시간을 가져옵니다.
        walkingTimeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 기존에 저장된 걸음 시간을 가져옵니다. 없다면 0으로 시작합니다.
                val currentWalkingTime = dataSnapshot.getValue(Long::class.java) ?: 0L
                // 걸은 시간을 추가합니다.
                val updatedWalkingTime = currentWalkingTime + timeSpentWalking
                // 데이터베이스를 업데이트합니다.
                walkingTimeRef.setValue(updatedWalkingTime).addOnSuccessListener {
                    Timber.d("Updated walking time successfully: $updatedWalkingTime ms")
                }.addOnFailureListener {
                    Timber.e(it, "Failed to update walking time")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Timber.e("Failed to read walking time: ${databaseError.toException()}")
            }
        })

    }
}