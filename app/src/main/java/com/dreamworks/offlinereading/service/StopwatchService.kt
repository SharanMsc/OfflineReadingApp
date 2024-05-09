package com.dreamworks.offlinereading.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.dreamworks.offlinereading.R
import com.dreamworks.offlinereading.screens.MainActivity
import java.util.Timer
import java.util.TimerTask


class StopwatchService : Service() {

    companion object {

        const val channelID = "Stopwatch_Notifications"

        const val start = "START"
        const val pause = "PAUSE"
        const val reset = "RESET"
        const val getStatus = "GET_STATUS"
        const val moveToForeground = "MOVE_TO_FOREGROUND"
        const val moveToBackground = "MOVE_TO_BACKGROUND"

        //intent extras
        const val stopWatchAction = "STOPWATCH_ACTION"
        const val timeElapsedKey = "TIME_ELAPSED"
        var isStopwatchRunning = "IS_STOPWATCH_RUNNING"

        // Intent Actions
        const val stopwatchTick = "STOPWATCH_TICK"
        const val stopwatchStatus = "STOPWATCH_STATUS"

        var isPlaying = true
    }
    private lateinit var notificationManager: NotificationManager
    private var timeElapsed: Int = 0
    private var isStopWatchRun = false

    private var updateTimer = Timer()
    private var stopwatchTimer = Timer()
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent != null) {
            createChannel()
            getNotificationManager()

            if (intent.action == "com.example.ACTION_BUTTON_CLICK") {
                // Toggle the playback state
                isPlaying = !isPlaying
                updateNotification(isPlaying)
                if (isPlaying){
                    try {
                        startStopwatch()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }else{
                    try {
                        stopStopwatch()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            if (intent.action == "com.example.reset") {
                resetStopwatch()
                isPlaying = false
                isStopWatchRun = false
            }

            when(intent.getStringExtra(stopWatchAction)){
                start ->{
                    try {
                        startStopwatch()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    isPlaying = true
                }
                pause ->{
                    try {
                        stopStopwatch()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    isPlaying = false
                }
                reset ->{
                    resetStopwatch()
                }
                getStatus ->{
                    sendStatus()
                }
                moveToForeground ->{
                    moveToForegroundFn()
                }
                moveToBackground ->{
                    moveToBackgroundFn()
                }
            }

        }


        return START_STICKY

    }
    private fun createChannel() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                channelID,
                "Stopwatch",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.setSound(null,null)
            notificationChannel.setShowBadge(true)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
    private fun getNotificationManager() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun moveToBackgroundFn() {
        updateTimer.cancel()
        stopForeground(true)

    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun moveToForegroundFn() {



        if (isStopWatchRun){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(1, buildNotification(isPlaying), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            } else {
                startForeground(1, buildNotification(isPlaying))
            }
            updateTimer = Timer()
            updateTimer.scheduleAtFixedRate(object : TimerTask(){
                override fun run() {
                    updateNotification(isPlaying)
                }

            },0,1000)
        }
    }

    private fun updateNotification(isPlaying: Boolean) {
        notificationManager.notify(
            1,
            buildNotification(isPlaying)
        )
    }

    private fun buildNotification(isPlaying: Boolean): Notification {
        val title = if (isStopWatchRun){
            "Stopwatch is running"
        }else{
           "Stopwatch is paused"
        }
        val hours:Int = timeElapsed.div(60).div(60)
        val minutes = timeElapsed.div(60)
        val seconds = timeElapsed.rem(60)

        val buttonIntent = Intent(this,StopwatchService::class.java)
        buttonIntent.action = "com.example.ACTION_BUTTON_CLICK"
        val pendingIntent1 = PendingIntent.getService(this, 0, buttonIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val resetIntent = Intent(this,StopwatchService::class.java)
        resetIntent.action = "com.example.reset"
        val resetPendingIntent1 = PendingIntent.getService(this, 0, resetIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


        val newIcon = if (isPlaying) R.drawable.baseline_pause_circle_outline_24 else R.drawable.baseline_play_circle_outline_24

        val smallView = RemoteViews(packageName,R.layout.notification_small_view)
        val bigView = RemoteViews(packageName,R.layout.notification_big_view)

        smallView.setTextViewText(R.id.timer,"${"%02d".format(hours)}:${"%02d".format(minutes)}:${
            "%02d".format(seconds)
        }")
        smallView.setImageViewResource(R.id.icon, newIcon)
        smallView.setOnClickPendingIntent(R.id.play_button, pendingIntent1)
        smallView.setOnClickPendingIntent(R.id.reset_button,resetPendingIntent1)


        bigView.setTextViewText(R.id.timer,"${"%02d".format(hours)}:${"%02d".format(minutes)}:${
            "%02d".format(seconds)
        }")

        bigView.setImageViewResource(R.id.icon, newIcon)
        bigView.setOnClickPendingIntent(R.id.play_button, pendingIntent1)
        bigView.setOnClickPendingIntent(R.id.reset_button,resetPendingIntent1)





        val intent = Intent(this,MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
//


        return NotificationCompat.Builder(this, channelID)
            .setContentTitle(title)
            .setOngoing(true)
            .setCustomBigContentView(bigView)
            .setContent(smallView)
            .setContentIntent(pendingIntent)
            .setColorized(true)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()
    }

    private fun sendStatus() {
        val statusIntent = Intent()
        statusIntent.action = stopwatchStatus
        statusIntent.putExtra(isStopwatchRunning,isStopWatchRun)
        statusIntent.putExtra(timeElapsedKey,timeElapsed)
        sendBroadcast(statusIntent)
    }

    private fun resetStopwatch() {
        stopStopwatch()
        timeElapsed = 0
        sendStatus()
    }

    private fun stopStopwatch() {
        stopwatchTimer.cancel()
        isStopWatchRun = false
        sendStatus()
    }

    private fun startStopwatch() {
        isStopWatchRun = true
        sendStatus()
        stopwatchTimer = Timer()
        stopwatchTimer.scheduleAtFixedRate(object :TimerTask(){
            override fun run() {
                val stopwatchIntent = Intent()
                stopwatchIntent.action = stopwatchTick
                timeElapsed++
                stopwatchIntent .putExtra(timeElapsedKey,timeElapsed)
                sendBroadcast(stopwatchIntent)
            }

        },0,1000)
    }




}