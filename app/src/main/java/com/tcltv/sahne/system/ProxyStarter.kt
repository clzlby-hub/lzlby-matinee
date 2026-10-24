package com.tcltv.sahne.system

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

object ProxyStarter {

    private const val TAG = "ProxyStarter"
    const val FLCLASH_PACKAGE = "com.follow.clash"
    private const val QUICK_ACTION_ACTIVITY = "com.follow.clash.QuickActionActivity"
    private const val ACTION_START = "com.follow.clash.action.START"

    fun isFlClashInstalled(context: Context): Boolean {
        return runCatching {
            context.packageManager.getPackageInfo(FLCLASH_PACKAGE, 0)
            true
        }.getOrDefault(false)
    }

    fun isVpnConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val activeNetwork = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(activeNetwork) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }

    /**
     * Starts FlClash proxy in the background if installed and not already connected.
     */
    fun startIfInstalled(context: Context, force: Boolean = false) {
        if (!isFlClashInstalled(context)) return

        if (!force && isVpnConnected(context)) {
            Log.d(TAG, "VPN is already connected, skipping auto-start")
            return
        }

        try {
            val intent = Intent(ACTION_START).apply {
                component = ComponentName(FLCLASH_PACKAGE, QUICK_ACTION_ACTIVITY)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
            }
            context.startActivity(intent)
            Log.i(TAG, "Successfully triggered FlClash background start")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to start FlClash: ${e.message}")
        }
    }
}
