package com.bytes.messenger.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bytes.messenger.adapter.CallsAdapter
import com.bytes.messenger.databinding.FragmentCallsBinding
import com.bytes.messenger.model.CallList

class CallsFragment : Fragment() {

    private lateinit var binding: FragmentCallsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCallsBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.recycler.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = CallsAdapter(fetchData(), context!!)
        }
        return view
    }

    private fun fetchData(): ArrayList<CallList> {
        val callListArrayDemo: ArrayList<CallList> = ArrayList()
        if (callListArrayDemo.size == 0)
            binding.noCall.visibility = View.VISIBLE
        return callListArrayDemo
    }
}