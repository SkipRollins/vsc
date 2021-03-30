package com.github.skiprollins.vsc.util

import androidx.annotation.experimental.UseExperimental
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class QrAnalyzer(
    private val onSuccess: (String) -> Unit
): ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    var pendingTask: Task<*>? = null

    @UseExperimental(markerClass = ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (pendingTask != null && pendingTask?.isComplete != true) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            if (pendingTask != null && pendingTask?.isComplete != true) {
                imageProxy.close()
                return
            }

            pendingTask = scanner.process(inputImage)
                .addOnSuccessListener { list ->
                    val code = list.filter { it.valueType == Barcode.TYPE_TEXT }
                        .map { it.rawValue }
                        .firstOrNull()

                    if (code != null) {
                        onSuccess(code)
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }

        } else {
            imageProxy.close()
        }
    }

}