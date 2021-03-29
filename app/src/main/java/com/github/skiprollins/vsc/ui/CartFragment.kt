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
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class CartFragment : Fragment() {

    @Inject lateinit var viewModel: CartContract
    private val subs = CompositeDisposable()

    private lateinit var cartList: RecyclerView
    private val cartAdapter by lazy {
        CartAdapter(requireContext())
    }

    private lateinit var dialogCustomView: View
    private val dialogEditText: EditText by lazy { dialogCustomView.findViewById(R.id.dialog_edittext) }
    private val addItemDialog: AlertDialog by lazy {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_item)
            .setView(dialogCustomView)
            .setPositiveButton(R.string.add_item) { di, _ ->
                addItem(dialogEditText.text.toString())
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

        cartList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        cartList.adapter = cartAdapter

        dialogCustomView = inflater.inflate(R.layout.view_dialog_edittext, null)

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            dialogEditText.setText("")
            addItemDialog.apply {
                show()
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        subscribeToObservables()

        viewModel.fetchInventory()
    }

    override fun onPause() {
        super.onPause()
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