package net.paceapp.core.garmin

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * Fires [onBluetoothOn] when the phone's Bluetooth adapter turns back ON.
 *
 * Used to auto-reconnect to the remembered Garmin watch: after Bluetooth returns, the app
 * re-runs the ConnectIQ register + status-poll sequence so the connection is restored without
 * the user re-pairing. Context-register this from an Activity's onResume and unregister in
 * onPause (a context-registered receiver, not a manifest one, per modern Android limits).
 */
class BluetoothStateReceiver(
    private val onBluetoothOn: () -> Unit,
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != BluetoothAdapter.ACTION_STATE_CHANGED) return
        val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
        if (state == BluetoothAdapter.STATE_ON) onBluetoothOn()
    }

    companion object {
        val intentFilter: IntentFilter
            get() = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
    }
}
