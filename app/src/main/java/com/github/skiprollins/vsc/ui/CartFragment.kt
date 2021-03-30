package com.github.skiprollins.vsc.ui

import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginLeft
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.github.skiprollins.vsc.BaseApp
import com.github.skiprollins.vsc.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class CartFragment : Fragment() {

    @Inject lateinit var viewModel: CartContract
    private val subs = CompositeDisposable()

    var subtotalListener: (Double) -> Unit = {}

    private lateinit var cartList: RecyclerView
    private val cartAdapter by lazy {
        CartAdapter(requireContext()).also {
            it.subtotalListener = subtotalListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as? BaseApp)?.appComponent?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        cartList = view.findViewById(R.id.recyclerview_cart)

        cartList.adapter = cartAdapter

        return view
    }

    override fun onStart() {
        super.onStart()
        subscribeToObservables()
    }

    override fun onStop() {
        super.onStop()
        unsubscribeFromObservables()
    }

    private fun subscribeToObservables() {
        subs.apply {
            add(viewModel.inventoryObservable.subscribe {
                cartAdapter.setItems(it)
            })

            add(viewModel.itemObservable.subscribe {
                val insertIndex = cartAdapter.addItem(it)
                cartList.scrollToPosition(insertIndex)
            })

            add(viewModel.errorObservable.subscribe { (error, payload) ->
                when (error) {
                    CartContract.Error.ITEM_NOT_FOUND -> handleItemNotFound(payload)
                }
            })
        }
    }

    private fun unsubscribeFromObservables() {
        subs.clear()
    }

    private fun handleItemNotFound(payload: Any?) {
        if (payload is String && payload.toIntOrNull() != null) {
            MaterialAlertDialogBuilder(this@CartFragment.requireContext())
                .setTitle(R.string.error_item_not_found_title)
                .setMessage(getString(R.string.error_item_not_found_message, payload))
                .setPositiveButton(R.string.ok) { di, _ -> di.dismiss() }
                .create()
                .show()
        }
    }

    fun addItem(id: String) {
        viewModel.getItem(id)
    }
}