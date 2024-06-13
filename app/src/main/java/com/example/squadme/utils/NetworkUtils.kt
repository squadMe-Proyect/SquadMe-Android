package com.example.squadme.utils

import android.content.Context
import android.net.ConnectivityManager

/**
 * Utility object to check network availability.
 */
object NetworkUtils {
    /**
     * Checks if network connectivity is available.
     *
     * @param context The context to access system services.
     * @return True if network connectivity is available, false otherwise.
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}