package com.github.skiprollins.vsc.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.github.skiprollins.vsc.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_PRODUCT_ID = 1
    }

    private lateinit var subtotal: TextView

    val cartFragment = CartFragment().also {
        it.subtotalListener = {
            subtotal.text = getString(R.string.subtotal, it)
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = getString(R.string.jungsoos_market)

        subtotal = findViewById(R.id.subtotal)
        subtotal.text = getString(R.string.subtotal, 0.0)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, cartFragment)
            .commit()

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            requestIdFromScanner()
        }
    }


    private fun requestIdFromScanner() {
        startActivityForResult(Intent(this, ScannerActivity::class.java), REQUEST_PRODUCT_ID)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PRODUCT_ID && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                cartFragment.addItem(data?.getStringExtra(ScannerActivity.EXTRA_PRODUCT_ID) ?: "")
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}