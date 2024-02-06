package com.example.clientbluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.clientbluetooth.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    val viewModel: BluetoothViewModel by lazy {
        ViewModelProvider(this)[BluetoothViewModel::class.java]
    }

    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        viewModel.status = {
            coroutineScope.launch {
                binding.tvStatus.text = it
            }
        }

        checkPermissions()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                findNavController(R.id.nav_host_fragment_content_main).navigateUp()
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.SecondFragment)
                true
            }

            R.id.secure_connect_scan -> {
                checkEnableBluetooth()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissions() {
        if (!checkBtPermission()) {
            registerPermissionListener()
            launchBtPermissions()
        }
    }

    private fun launchBtPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN
                )
            )
        } else {
            pLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN
                )
            )
        }
    }

    private fun checkBtPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun registerPermissionListener() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {

        }
    }

    private fun checkEnableBluetooth() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT))
            return

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

            startActivityForResult(enableBtIntent, MainActivity.REQUEST_ENABLE_BT)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    companion object {
        val REQUEST_ENABLE_BT: Int = 1
    }
}