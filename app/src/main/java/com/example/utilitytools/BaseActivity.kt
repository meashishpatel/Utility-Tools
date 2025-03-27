package com.example.utilitytools

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {


    private lateinit var networkReceiver: BroadcastReceiver
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the network change receiver
        networkReceiver = NetworkChangeReceiver(
            onInternetLost = { showNoInternetDialog(this) },
            onInternetRestored = { dismissNoInternetDialog() }
        )

        // Register the receiver for connectivity changes
        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

    }

    private fun showNoInternetDialog(context: Context) {
        if (alertDialog?.isShowing == true) return

        val builder = AlertDialog.Builder(context)
        builder.setTitle("No Internet")
        builder.setMessage("Please turn on the internet to continue.")
        builder.setCancelable(false)

        builder.setPositiveButton("Retry") { dialog, _ ->
            dialog.dismiss()
            if (!isInternetAvailable(context)) {
                showNoInternetDialog(context)
            }
        }

        builder.setNegativeButton("Cancel") { _, _ ->
            finish()
        }

        alertDialog = builder.create()
        alertDialog?.show()
    }

    private fun dismissNoInternetDialog() {
        alertDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver)
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
