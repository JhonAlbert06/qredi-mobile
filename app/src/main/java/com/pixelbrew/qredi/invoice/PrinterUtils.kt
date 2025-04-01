package com.pixelbrew.qredi.invoice

import java.io.OutputStream

object PrinterUtils {
    fun sendDataPrinter(data: String, outputStream: OutputStream) {
        val bytes = data.toByteArray()
        outputStream.write(bytes)
        outputStream.flush()
    }
}