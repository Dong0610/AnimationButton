package com.example.animationbutton

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.animationbutton.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {
    val binding by lazy {
        ActivityMain2Binding.inflate(layoutInflater)
    }
    private var notificationId = 1 // Unique ID for the notification
    private lateinit var notificationLayout: RemoteViews
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManagerCompat
    private val handler = Handler(Looper.myLooper()!!)

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Create a notification channel (You can put this in your Application class)
        createNotificationChannel()

        notificationManager = NotificationManagerCompat.from(this)
        notificationLayout = createCustomNotificationLayout(R.layout.custom_notification)
        notificationBuilder = createNotificationBuilder()

        binding.btn.setOnClickListener {
            notificationManager.notify(notificationId, notificationBuilder.build())
            updateProgressContinuously()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancel(notificationId)
    }


    private fun updateProgressContinuously() {
        val totalFrames = 100
        val animationDuration = 1500
        val delay = animationDuration / totalFrames

        handler.post(object : Runnable {
            override fun run() {
                currentProgress+=1
                notificationLayout = createCustomNotificationLayout(R.layout.custom_notification)
                notificationLayout.setProgressBar(R.id.progressBar, 100, currentProgress, false)
                notificationLayout.setProgressBar(R.id.progressBar1, 100, currentProgress, false)
                notificationBuilder.setContent(notificationLayout).setSilent(true)
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                notificationManager.notify(notificationId, notificationBuilder.build())

                if (currentProgress == 100) {
                    currentProgress = 0
                }
                handler.postDelayed(this, delay.toLong())
            }
        })
    }
    var currentProgress = 0




    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "your_channel_id"
            val channelName = "Your Channel Name"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun createNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, "your_channel_id")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("...")
            .setOngoing(true)
            .setContent(notificationLayout)
    }
    private fun createCustomNotificationLayout(@LayoutRes layoutResId: Int): RemoteViews {
        val remoteViews = RemoteViews(packageName, layoutResId)
        val intent = Intent(this, MainActivity2::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        remoteViews.setOnClickPendingIntent(R.id.btn_item, pendingIntent)
        return remoteViews
    }
}