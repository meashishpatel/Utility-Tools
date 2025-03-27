package com.example.utilitytools

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.utilitytools.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket


class test : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val scope = CoroutineScope(Dispatchers.Main)
    private var checkJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkInternetConnection()
        binding.next.setOnClickListener{
            val intent = Intent(this, ActivityTwo::class.java)
            startActivity(intent)
        }

    }

    private fun checkInternetConnection() {
        checkJob = scope.launch {
            val isConnected = isInternetAvailable(this@test)
            if (isConnected) {
                // Proceed with the app
            } else {
                showNoInternetDialog()
            }
        }
    }

    private suspend fun isInternetAvailable(context: Context): Boolean {
        return try {
            // Check network capabilities first
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(network) ?: return false

            if (!capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) return false

            isInternetReachable()
        } catch (e: Exception) {
            Log.e("NetworkCheck", "Error checking internet: ${e.message}")
            false
        }
    }

    private suspend fun isInternetReachable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val timeoutMs = 5000
            Socket().use { socket ->
                socket.connect(InetSocketAddress("8.8.8.8", 53), timeoutMs)
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("No Internet")
            .setMessage("Please turn on the internet to continue.")
            .setCancelable(false)
            .setPositiveButton("Retry") { dialog, _ ->
                dialog.dismiss()
                checkInternetConnection()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                finish()
            }

        val alertDialog = builder.create()
        alertDialog.show()

        // Start periodic checking
        startConnectionCheck(alertDialog)
    }

    private fun startConnectionCheck(dialog: AlertDialog) {
        checkJob = scope.launch {
            while (true) {
                delay(2000)
                val isConnected = isInternetAvailable(this@test)
                if (isConnected) {
                    withContext(Dispatchers.Main) {
                        dialog.dismiss()
                        // Proceed with app
                    }
                    break
                }
                else{
                    withContext(Dispatchers.Main) {
                        showNoInternetDialog()
                        // Proceed with app
                    }
                    break
                }
            }
        }
    }
}