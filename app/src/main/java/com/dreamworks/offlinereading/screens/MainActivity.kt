package com.dreamworks.offlinereading.screens

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamworks.offlinereading.R
import com.dreamworks.offlinereading.adapter.CountriesAdapter
import com.dreamworks.offlinereading.databinding.ActivityMainBinding
import com.dreamworks.offlinereading.model.CountriesModel
import com.dreamworks.offlinereading.receiver.ReminderReceiver
import com.dreamworks.offlinereading.service.StopwatchService
import java.util.Calendar


class MainActivity : AppCompatActivity(),TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: ActivityMainBinding
    private val sectionList:MutableList<CountriesModel> = arrayListOf()
    private lateinit var statusReceiver :BroadcastReceiver
    private lateinit var timeReceiver: BroadcastReceiver
    private var isStopwatchRunning = false
    private var handlerAnimation = Handler(Looper.getMainLooper())

    private var hours = 0
    private var minutes = 0
    // variables to store the selected time
    private var myHours : Int = 0
    private var myMinutes : Int= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val countries = resources.getStringArray(R.array.countries)
        var lastHeader = ""
        for (i in countries){

            val header = i[0].uppercaseChar().toString()
            if (!TextUtils.equals(lastHeader, header)) {
                lastHeader = header
                sectionList.add(CountriesModel(header, true))
            }
            sectionList.add(CountriesModel(i,false))


        }


        val adapter = CountriesAdapter()
        adapter.setData(sectionList)
        binding.countryList.layoutManager = LinearLayoutManager(this)
        binding.countryList.setHasFixedSize(true)
        binding.countryList.adapter = adapter

        binding.toggleButton.setOnClickListener {
            if (isStopwatchRunning) {
                pauseStopwatch()
                stopPulse()
            } else {
                startStopwatch()
                startPulse()
            }
        }

        binding.resetImageView.setOnClickListener {
            resetStopwatch()
            stopPulse()
        }

//        Log.e("data","$countriesModel")

        setAlarm()

        binding.clockButton.setOnClickListener {
            val cal = Calendar.getInstance()
            // getting the current time
            hours = cal.get(Calendar.HOUR)
            minutes = cal.get(Calendar.MINUTE)
            // creating a new time picker dialog
            val timePickerDialog = TimePickerDialog(this,this,hours,minutes,true)
            timePickerDialog.show()
        }
    }


    private fun startPulse() {
        runnable.run()
    }

    private fun stopPulse() {
        handlerAnimation.removeCallbacks(runnable)
    }

    private var runnable = object : Runnable {
        override fun run() {

            binding.imgAnimation1.animate().scaleX(3f).scaleY(3f).alpha(0f).setDuration(1000)
                .withEndAction {
                    binding.imgAnimation1.scaleX = 1f
                    binding.imgAnimation1.scaleY = 1f
                    binding.imgAnimation1.alpha = 1f
                }

            binding.imgAnimation2.animate().scaleX(3f).scaleY(3f).alpha(0f).setDuration(700)
                .withEndAction {
                    binding.imgAnimation2.scaleX = 1f
                    binding.imgAnimation2.scaleY = 1f
                    binding.imgAnimation2.alpha = 1f
                }

            handlerAnimation.postDelayed(this, 1500)
        }
    }






    private fun resetStopwatch() {
        val stopwatchService = Intent(this,StopwatchService::class.java)
        stopwatchService.putExtra(StopwatchService.stopWatchAction,StopwatchService.reset)
        startService(stopwatchService)
    }

    private fun startStopwatch() {
        val stopwatchService = Intent(this,StopwatchService::class.java)
        stopwatchService.putExtra(StopwatchService.stopWatchAction,StopwatchService.start)
        startService(stopwatchService)
    }

    private fun pauseStopwatch() {
        val stopwatchService = Intent(this,StopwatchService::class.java)
        stopwatchService.putExtra(StopwatchService.stopWatchAction,StopwatchService.pause)
        startService(stopwatchService)
    }
    private fun getStopwatchStatus() {
        val stopwatchService = Intent(this,StopwatchService::class.java)
        stopwatchService.putExtra(StopwatchService.stopWatchAction,StopwatchService.getStatus)
        startService(stopwatchService)
    }

    override fun onStart() {
        super.onStart()
        moveToBackground()
    }



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()

        NotificationManagerCompat.from(this).cancel(1)
        getStopwatchStatus()
        val statusFilter = IntentFilter()
        statusFilter.addAction(StopwatchService.stopwatchStatus)
        statusReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                val isRunning = p1?.getBooleanExtra(StopwatchService.isStopwatchRunning,false)
                isStopwatchRunning = isRunning!!
                val timeElapsed = p1.getIntExtra(StopwatchService.timeElapsedKey,0)
                updateLayout()
                updateStopwatchValue(timeElapsed)
            }

        }

        if (Build.VERSION.SDK_INT<= Build.VERSION_CODES.O){
            registerReceiver(statusReceiver,statusFilter, RECEIVER_NOT_EXPORTED)
        }else{
            registerReceiver(statusReceiver,statusFilter,RECEIVER_EXPORTED)
        }


        val timeFilter = IntentFilter()
        timeFilter.addAction(StopwatchService.stopwatchTick)
        timeReceiver = object : BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                val timeElapsed = p1?.getIntExtra(StopwatchService.timeElapsedKey,0)
                updateStopwatchValue(timeElapsed!!)
            }

        }
        if (Build.VERSION.SDK_INT<= Build.VERSION_CODES.O){
            registerReceiver(timeReceiver,timeFilter,RECEIVER_NOT_EXPORTED)
        }else{
            registerReceiver(timeReceiver,timeFilter, RECEIVER_EXPORTED)

        }



    }

    private fun setAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Set the alarm to trigger at the specified time
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, myHours)
        calendar.set(Calendar.MINUTE, myMinutes)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(statusReceiver)
        unregisterReceiver(timeReceiver)
        moveToForeground()
    }

    private fun moveToForeground() {
        val stopwatchService = Intent(this,StopwatchService::class.java)
        stopwatchService.putExtra(StopwatchService.stopWatchAction,StopwatchService.moveToForeground)
        startService(stopwatchService)
    }

    private fun moveToBackground() {

        val stopwatchService = Intent(this,StopwatchService::class.java)
        stopwatchService.putExtra(
            StopwatchService.stopWatchAction,
            StopwatchService.moveToBackground
        )
        startService(stopwatchService)
    }

    @SuppressLint("SetTextI18n")
    private fun updateStopwatchValue(timeElapsed: Int) {
        val hours:Int = (timeElapsed / 60) / 60
        val minutes:Int = (timeElapsed / 60)
        val seconds:Int = timeElapsed % 60
        binding.stopwatchValueTextView.text =
            "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"

    }

    private fun updateLayout() {
        if (isStopwatchRunning){
            binding.toggleButton.icon = ContextCompat.getDrawable(this,
                R.drawable.baseline_pause_circle_outline_24
            )
            binding.resetImageView.visibility = View.INVISIBLE
        }else{
            binding.toggleButton.icon = ContextCompat.getDrawable(this,R.drawable.baseline_play_circle_outline_24
            )
            binding.resetImageView.visibility = View.VISIBLE
        }
    }


    inner class NotificationListener:BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            when (p1!!.action){
                "PAUSE"->{
                    pauseStopwatch()
                    Toast.makeText(this@MainActivity,"PAUSE",Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        myHours = p1
        myMinutes = p2
    }


}