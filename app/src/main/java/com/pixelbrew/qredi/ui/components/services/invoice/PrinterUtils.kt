package com.pixelbrew.qredi.ui.components.services.invoice

import java.io.OutputStream

object PrinterUtils {
    fun sendDataPrinter(data: String, outputStream: OutputStream) {
        val bytes = data.toByteArray()
        outputStream.write(bytes)
        outputStream.flush()
    }
}