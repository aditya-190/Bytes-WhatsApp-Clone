package com.bytes.messenger.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bytes.messenger.R
import com.bytes.messenger.adapter.CallsAdapter
import com.bytes.messenger.model.CallList
import kotlinx.android.synthetic.main.fragment_calls.view.*

class CallsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_calls, container, false)
        view.recycler.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = CallsAdapter(fetchData(view), context!!)
        }
        return view
    }

    private fun fetchData(view: View): ArrayList<CallList> {
        val callListArrayDemo: ArrayList<CallList> = ArrayList()
        if (callListArrayDemo.size == 0)
            view.no_call.visibility = View.VISIBLE
        return callListArrayDemo
    }
}