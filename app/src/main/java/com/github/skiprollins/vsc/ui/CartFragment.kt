package com.github.skiprollins.vsc.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
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
        }
    }

    private fun unsubscribeFromObservables() {
        subs.clear()
    }
}