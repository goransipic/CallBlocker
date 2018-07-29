package com.goodapp.callblocker.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goodapp.callblocker.R
import kotlinx.android.synthetic.main.main_fragment.*
import android.support.v7.widget.DividerItemDecoration


class MainFragment : Fragment() {

    companion object {
        val TYPE_BLOCKED_CALLS = 0
        val TYPE_SUSPICIOUS_CALLS = 1
        val TYPE_OF_CALL = "TYPE_OF_CALL"
        fun newInstance(typeOfCall: Int): Fragment {
            val fragment = MainFragment()
            val bundle = Bundle()
            bundle.putInt(TYPE_OF_CALL, typeOfCall)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rc_calls.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            addItemDecoration(dividerItemDecoration)
        }

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        if (arguments?.getInt(TYPE_OF_CALL) == TYPE_BLOCKED_CALLS) {
            viewModel.getBlockedCalls().observe(viewLifecycleOwner, Observer {
                rc_calls.adapter = CallsAdapter(requireContext(), it)
            })
        } else {
            viewModel.getSuspisiousCalls().observe(viewLifecycleOwner, Observer {
                rc_calls.adapter = CallsAdapter(requireContext(), it)
            })
        }


    }

}
