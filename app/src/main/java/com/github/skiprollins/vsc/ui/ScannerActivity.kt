package com.github.skiprollins.vsc.ui

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.skiprollins.vsc.R
import com.github.skiprollins.vsc.util.QrAnalyzer
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.lang.Exception

class ScannerActivity: AppCompatActivity() {

    companion object {
        @JvmStatic
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.VIBRATE
        )

        private const val VIBRATE_DURATION = 250L

        private const val PERMISSION_REQUEST_CODE = 10

        const val EXTRA_PRODUCT_ID = "ScannerActivity.EXTRA_PRODUCT_ID"
    }

    private lateinit var dialogCustomView: View
    private val dialogEditText: EditText by lazy { dialogCustomView.findViewById(R.id.dialog_edittext) }
    private val addItemDialog: AlertDialog by lazy {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.add_item)
            .setView(dialogCustomView)
            .setPositiveButton(R.string.add_item) { di, _ ->
                returnCode(dialogEditText.text.toString(), false)
                di.dismiss()

            }.setNegativeButton(R.string.cancel) { di, _ ->
                di.dismiss()

            }.create()

        dialogEditText.setOnKeyListener { _, keyCode, event ->
            (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER).also {
                if (it) {
                    dialog.getButton(Dialog.BUTTON_POSITIVE).performClick()
                }
            }
        }

        return@lazy dialog
    }

    private lateinit var viewFinder: PreviewView

    private var processing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        supportActionBar?.title = getString(R.string.scan_qr_code)

        viewFinder = findViewById(R.id.viewFinder)

        dialogCustomView = layoutInflater.inflate(R.layout.view_dialog_edittext, null)

        findViewById<MaterialButton>(R.id.button_type_id).setOnClickListener {
            requestIdFromUser()
        }

        if (requestPermissions()) {
            startCamera()
        }
    }

    override fun onResume() {
        super.onResume()
        processing = false
    }

    private fun returnCode(code: String, vibrate: Boolean) {
        if (!processing) {
            processing = true

            if (vibrate) {
                vibrate()
            }

            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(EXTRA_PRODUCT_ID, code)
            })

            finish()
        }
    }

    private fun vibrate() {
        (getSystemService(VIBRATOR_SERVICE) as? Vibrator)?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrate(VibrationEffect.createOneShot(VIBRATE_DURATION, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrate(VIBRATE_DURATION)
            }
        }
    }

    private fun requestIdFromUser() {
        dialogEditText.setText("")
        addItemDialog.apply {
            show()
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .apply {
                        setSurfaceProvider(viewFinder.surfaceProvider)
                    }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .build()
                    .apply {
                        setAnalyzer(
                            ContextCompat.getMainExecutor(this@ScannerActivity),
                            QrAnalyzer { returnCode(it, true) }
                        )
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
                } catch (ex: Exception) {
                    Timber.e(ex)
                }

            }, ContextCompat.getMainExecutor(this)
        )
    }





    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (allPermissionsGranted()) {
                startCamera()

            } else {
                Snackbar.make(findViewById(android.R.id.content), "Permissions not granted", Snackbar.LENGTH_LONG).show()
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun requestPermissions(): Boolean {
        val granted = allPermissionsGranted()
        if (!granted) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE)
        }

        return granted
    }


}