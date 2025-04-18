package com.pixelbrew.qredi.ui.components.services.invoice

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pixelbrew.qredi.data.local.entities.NewFeeEntity
import com.pixelbrew.qredi.ui.components.services.invoice.InvoiceGenerator.DayCloseData
import java.io.IOException
import java.util.UUID

object BluetoothPrinter {

    private const val TAG = "BluetoothPrinter"
    private const val SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB"
    private const val RECONNECTION_DELAY_MS = 1000L

    private var currentSocket: BluetoothSocket? = null
    private var currentDevice: BluetoothDevice? = null

    enum class DocumentType { PAYMENT, DAY_CLOSE }

    /**
     * Verifica si el adaptador Bluetooth está disponible y activado.
     */
    fun isBluetoothEnabled(): Boolean {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        return adapter != null && adapter.isEnabled
    }

    /**
     * Solicita al usuario que active el Bluetooth si está desactivado.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun requestEnableBluetooth(context: Context) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        if (context is Activity) {
            context.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            Toast.makeText(
                context,
                "No se puede solicitar activar Bluetooth desde este contexto.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Verifica si la aplicación tiene los permisos necesarios de Bluetooth.
     */
    fun hasBluetoothPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_SCAN
                    ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Solicita los permisos necesarios de Bluetooth al usuario.
     */
    fun requestBluetoothPermissions(activity: android.app.Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_BLUETOOTH_PERMISSIONS)
        }
    }

    /**
     * Imprime un documento en la impresora Bluetooth especificada.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun printDocument(
        context: Context,
        printerName: String,
        type: DocumentType,
        data: DayCloseData = DayCloseData("", "", 0.0, 0.0, emptyList()),
        feeEntity: NewFeeEntity = NewFeeEntity(
            feeId = "",
            loanId = "",
            paymentAmount = 0.0,
            dateDay = 0,
            dateMonth = 0,
            dateYear = 0,
            dateHour = 0,
            dateMinute = 0,
            dateSecond = 0,
            dateTimezone = "",
            number = 0,
            numberTotal = 0,
            companyName = "",
            companyNumber = "",
            clientName = "",
            id = 0
        )
    ): Boolean {
        return try {
            val socket = ensureConnection(printerName)
            if (socket != null && socket.isConnected) {
                val outputStream = socket.outputStream
                val content = when (type) {
                    DocumentType.PAYMENT -> InvoiceGenerator.generatePaymentContent(feeEntity)
                    DocumentType.DAY_CLOSE -> InvoiceGenerator.generateDayCloseContent(data)
                }
                PrinterUtils.sendDataPrinter(content, outputStream)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error de impresión", e)
            resetConnection()
            false
        }
    }

    /**
     * Asegura la conexión con la impresora Bluetooth especificada.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Throws(IOException::class)
    private fun ensureConnection(printerName: String): BluetoothSocket? {
        if (currentSocket?.isConnected == true) return currentSocket

        resetConnection()

        val adapter =
            BluetoothAdapter.getDefaultAdapter() ?: throw IOException("Bluetooth no disponible")
        if (!adapter.isEnabled) throw IOException("Bluetooth desactivado")

        val device =
            findPrinter(adapter, printerName) ?: throw IOException("Impresora no encontrada")

        return try {
            val socket = device.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID))
            socket.connect()
            Thread.sleep(RECONNECTION_DELAY_MS)
            currentDevice = device
            currentSocket = socket
            socket
        } catch (e: Exception) {
            try {
                val socket =
                    device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID))
                socket.connect()
                Thread.sleep(RECONNECTION_DELAY_MS)
                currentDevice = device
                currentSocket = socket
                socket
            } catch (e2: Exception) {
                resetConnection()
                throw IOException("No se pudo conectar a la impresora", e2)
            }
        }
    }

    /**
     * Busca la impresora Bluetooth emparejada por nombre.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun findPrinter(adapter: BluetoothAdapter, name: String): BluetoothDevice? {
        return adapter.bondedDevices.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }

    /**
     * Restablece la conexión Bluetooth actual.
     */
    private fun resetConnection() {
        try {
            currentSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error al cerrar socket", e)
        } finally {
            currentSocket = null
            currentDevice = null
        }
    }

    private const val REQUEST_ENABLE_BT = 1001
    private const val REQUEST_BLUETOOTH_PERMISSIONS = 1002
}