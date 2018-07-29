package com.goodapp.callblocker.ui.main

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.goodapp.callblocker.R
import com.goodapp.callblocker.model.NormalCall
import com.goodapp.callblocker.model.PhoneStatus
import com.goodapp.callblocker.model.ScamCall
import com.goodapp.callblocker.model.SuspiciousCall
import kotlinx.android.synthetic.main.calls_list_item.view.*

class CallsAdapter(val context: Context, val items: List<PhoneStatus>?) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.calls_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (items != null)
            holder.bind(items[position])
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: PhoneStatus) {
        when (item) {
            is NormalCall -> {
                itemView.findViewById<TextView>(R.id.calls_title).text = item.name
                itemView.findViewById<TextView>(R.id.calls_info).text = null
                itemView.findViewById<TextView>(R.id.calls_subtitle).text = item.phoneNumber
            }
            is ScamCall -> {
                itemView.findViewById<TextView>(R.id.calls_title).text = item.name
                itemView.findViewById<TextView>(R.id.calls_info).text = item.date
                itemView.findViewById<TextView>(R.id.calls_subtitle).text = item.phoneNumber
            }
            is SuspiciousCall -> {
                itemView.findViewById<TextView>(R.id.calls_title).text = item.name
                itemView.findViewById<TextView>(R.id.calls_info).text = item.date
                itemView.findViewById<TextView>(R.id.calls_subtitle).text = item.phoneNumber
            }
        }

    }
}