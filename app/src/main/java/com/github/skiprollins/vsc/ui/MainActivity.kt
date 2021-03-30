package com.github.skiprollins.vsc.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.github.skiprollins.vsc.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_PRODUCT_ID = 1
    }

    val cartFragment = CartFragment()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, cartFragment)
            .commit()

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            requestIdFromScanner()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
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