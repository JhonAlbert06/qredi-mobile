package com.pixelbrew.qredi.invoice

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.pixelbrew.qredi.data.entities.NewFeeEntity
import java.io.IOException
import java.util.UUID

object BluetoothPrinter {

    private const val TAG = "BluetoothPrinter"
    private const val SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB"
    private const val RECONNECTION_DELAY_MS = 1000L

    private var currentSocket: BluetoothSocket? = null
    private var currentDevice: BluetoothDevice? = null

    enum class DocumentType { PAYMENT, DAY_CLOSE }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun printDocument(
        context: Context,
        printerName: String,
        type: DocumentType,
        data: InvoiceGenerator.DocumentData = InvoiceGenerator.DocumentData(),
        feeEntity: NewFeeEntity = NewFeeEntity(
            id = 0,
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
            clientName = ""
        )
    ): Boolean {
        return try {
            ensureConnection(printerName)?.use { socket ->
                val outputStream = socket.outputStream
                val content = when (type) {
                    DocumentType.PAYMENT -> InvoiceGenerator.generatePaymentContent(feeEntity)
                    DocumentType.DAY_CLOSE -> InvoiceGenerator.generateDayCloseContent(data)
                }
                PrinterUtils.sendDataPrinter(content, outputStream)
                true
            } == true
        } catch (e: Exception) {
            Log.e(TAG, "Printing failed", e)
            resetConnection()
            false
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Throws(IOException::class)
    private fun ensureConnection(printerName: String): BluetoothSocket? {

        if (currentSocket?.isConnected == true) {
            return currentSocket
        }

        resetConnection()

        val adapter =
            BluetoothAdapter.getDefaultAdapter() ?: throw IOException("Bluetooth no disponible")
        if (!adapter.isEnabled) throw IOException("Bluetooth desactivado")

        val device =
            findPrinter(adapter, printerName) ?: throw IOException("Impresora no encontrada")

        try {
            val socket = device.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID))
            socket.connect()

            Thread.sleep(RECONNECTION_DELAY_MS)

            currentDevice = device
            currentSocket = socket
            return socket
        } catch (e: Exception) {
            try {
                val socket =
                    device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID))
                socket.connect()

                Thread.sleep(RECONNECTION_DELAY_MS)

                currentDevice = device
                currentSocket = socket
                return socket
            } catch (e2: Exception) {
                resetConnection()
                throw IOException("No se pudo conectar a la impresora", e2)
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun findPrinter(adapter: BluetoothAdapter, name: String): BluetoothDevice? {
        return adapter.bondedDevices.firstOrNull { device ->
            device.name?.equals(name, ignoreCase = true) == true
        }
    }

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

    private fun hasBluetoothPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}