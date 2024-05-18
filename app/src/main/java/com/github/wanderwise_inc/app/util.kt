package com.github.wanderwise_inc.app

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

fun Context.isNetworkAvailable(): Boolean {
  val connectivityManager =
    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  val networkInfo = connectivityManager.activeNetwork
  val networkCapabilities = connectivityManager.getNetworkCapabilities(networkInfo)
  val ret =
    networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
  Log.d("ItineraryRepositoryImpl", "Network available: $ret")
  return ret
}