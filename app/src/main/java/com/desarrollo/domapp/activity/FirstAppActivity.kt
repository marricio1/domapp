package com.desarrollo.domapp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.desarrollo.domapp.R
import android.content.Intent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.companion.AssociationInfo
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.Context
import android.content.IntentFilter
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.ParcelUuid
import android.util.AttributeSet
import android.view.View
import java.util.UUID
import java.util.concurrent.Executor
import java.util.regex.Pattern

internal const val ACTION_PERMISSIONS_GRANTED = "BluetoothPermission.permissions_granted"
internal const val ACTION_PERMISSIONS_DENIED = "BluetoothPermission.permissions_denied"

class FirstAppActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_first_app)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        btnIngresar.setOnClickListener{
            connectDevices()
        }

        checkBluetoothEnabled()

    }

    @SuppressLint("MissingPermission")
    private fun connectDevices(){

        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()


        val deviceFilter: BluetoothDeviceFilter = BluetoothDeviceFilter.Builder()
            // Match only Bluetooth devices whose name matches the pattern.
            .setNamePattern(Pattern.compile("My device"))
            // Match only Bluetooth devices whose service UUID matches this pattern.
            .addServiceUuid(ParcelUuid(UUID(0x123abcL, -1L)), null)
            .build()

        val pairingRequest: AssociationRequest = AssociationRequest.Builder()
            // Find only devices that match this request filter.
           // .addDeviceFilter(deviceFilter)
            // Stop scanning as soon as one device matching the filter is found.
            .setSingleDevice(true)
            .build()



        val deviceManager = getSystemService(Context.COMPANION_DEVICE_SERVICE) as? CompanionDeviceManager

        val executor: Executor =  Executor { it.run() }

        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            deviceManager?.associate(pairingRequest,
                executor,
                object : CompanionDeviceManager.Callback() {
                    // Called when a device is found. Launch the IntentSender so the user
                    // can select the device they want to pair with.
                    override fun onAssociationPending(intentSender: IntentSender) {
                        intentSender?.let {
                            startIntentSenderForResult(it, SELECT_DEVICE_REQUEST_CODE, null, 0, 0, 0)
                        }
                    }

                    override fun onAssociationCreated(associationInfo: AssociationInfo) {
                        // The association is created.
                    }

                    override fun onFailure(errorMessage: CharSequence?) {
                        // Handle the failure.
                    }
                })
        } else {

            deviceManager?.associate(pairingRequest,
                object : CompanionDeviceManager.Callback() {
                    // Called when a device is found. Launch the IntentSender so the user
                    // can select the device they want to pair with.
                    override fun onDeviceFound(chooserLauncher: IntentSender) {
                        startIntentSenderForResult(chooserLauncher,
                            SELECT_DEVICE_REQUEST_CODE, null, 0, 0, 0)
                    }

                    override fun onFailure(error: CharSequence?) {
                        // Handle the failure.
                    }
                }, null)
        }
    }

    private fun navigateSecondActivity(){
        val intent = Intent(this, SecondAppActivity::class.java)
        startActivity(intent)
    }

    private val SELECT_DEVICE_REQUEST_CODE: Int = 42
    private val REQUEST_ENABLE_BT = 1

    private fun checkBluetoothEnabled() {

        when {
            checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED -> {
                sendBroadcast(Intent(ACTION_PERMISSIONS_GRANTED))
                //finish()
            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
            }
        }


        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()

        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            return
        }
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }


    }
/*
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendBroadcast(Intent(ACTION_PERMISSIONS_GRANTED))
           // finish()
        } else {
            sendBroadcast(Intent(ACTION_PERMISSIONS_DENIED))
           // finish()
        }
    }*/
}