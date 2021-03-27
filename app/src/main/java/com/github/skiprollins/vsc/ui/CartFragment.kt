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
import com.github.skiprollins.vsc.R
import com.github.skiprollins.vsc.network.Product
import timber.log.Timber

class CartFragment : Fragment() {

    private lateinit var cartList: RecyclerView
    private val cartAdapter by lazy {
        CartAdapter(requireContext())
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        cartList = view.findViewById(R.id.recyclerview_cart)

        cartList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        cartList.adapter = cartAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()

        val list = listOf(
            Product(
                "0001",
                "https://zxing.org/w/chart?cht=qr&chs=350x350&chld=L&choe=UTF-8&chl=0001",
                "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/120/apple/237/banana_1f34c.png",
                "Banana",
                "$1.00"
            ), Product(
                "0002",
                "https://zxing.org/w/chart?cht=qr&chs=350x350&chld=L&choe=UTF-8&chl=0002",
                "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/120/apple/237/red-apple_1f34e.png",
                "Apple",
                "$4.00"
            ), Product(
                "0003",
                "https://zxing.org/w/chart?cht=qr&chs=350x350&chld=L&choe=UTF-8&chl=0003",
                "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/120/apple/237/sparkles_2728.png",
                "Other Stuff",
                "$10.00"
            )
        )

        cartAdapter.setItems(list)
    }
}