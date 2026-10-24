package com.tcltv.sahne.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.tcltv.sahne.data.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        Log.i("BootReceiver", "Received boot broadcast: $action")

        val appContext = context.applicationContext
        val store = SettingsStore(appContext)

        CoroutineScope(Dispatchers.Default).launch {
            val settings = store.settings.first()
            if (settings.autoStartProxy) {
                // Wait briefly for system network/Wi-Fi to initialize
                delay(3000)
                ProxyStarter.startIfInstalled(appContext)
            }
        }
    }
}
