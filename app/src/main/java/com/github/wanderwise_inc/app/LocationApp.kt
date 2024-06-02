package com.github.wanderwise_inc.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class LocationApp : Application() {

  override fun onCreate() {
    super.onCreate()

    // Create a new notification channel
    val channel =
        NotificationChannel(
            "location", // The id of the channel.
            "Location", // The user-visible name of the channel.
            NotificationManager
                .IMPORTANCE_LOW // The importance of the channel. This controls how interruptive
                                // notifications posted to this channel are.
            )

    // Get the notification manager
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Create the notification channel
    notificationManager.createNotificationChannel(channel)
  }
}
