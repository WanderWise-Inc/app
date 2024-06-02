package com.github.wanderwise_inc.app.viewmodel

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.github.wanderwise_inc.app.R

/**
 * `LocationService` is a class that extends the `Service` class.
 *
 * This class is used to perform long-running operations in the background.
 * It is a component that runs in the background to perform long-running operations without providing a user interface.
 * This manages the location notifications.
 */
class LocationService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Starts the service.
     *
     * This method creates a notification and starts the service in the foreground.
     */
    private fun start() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Wanderwise tracking")
            .setContentText("Running...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        startForeground(1, notification.build())

        notificationManager.notify(1, notification.build())
    }

    /**
     * Stops the service.
     *
     * This method stops the service by calling `stopSelf()`.
     */
    private fun stop() {
        stopSelf()
    }

    /**
     * A companion object is an object that is common to all instances of a class.
     *
     * This companion object contains constants that represent the actions that can be performed by the `LocationService`.
     */
    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}
