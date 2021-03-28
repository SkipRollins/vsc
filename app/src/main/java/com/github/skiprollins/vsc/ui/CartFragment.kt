package com.github.skiprollins.vsc.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.github.skiprollins.vsc.BaseApp
import com.github.skiprollins.vsc.R
import com.github.skiprollins.vsc.network.Product
import io.reactivex.rxjava3.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class CartFragment : Fragment() {

    @Inject lateinit var viewModel: CartContract
    private val subs = CompositeDisposable()

    private lateinit var cartList: RecyclerView
    private val cartAdapter by lazy {
        CartAdapter(requireContext())
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
            AlertDialog.Builder(this@CartFragment.requireContext())
                .setTitle(R.string.error_item_not_found_title)
                .setMessage(getString(R.string.error_item_not_found_message, payload))
                .setPositiveButton(R.string.ok) { di, _ -> di.dismiss() }
                .create()
                .show()
        }
    }

    private var counter: Int = 0
    fun addItem() {
        val id = "000${(counter++ % 4) + 1}"
        viewModel.getItem(id)
    }
}