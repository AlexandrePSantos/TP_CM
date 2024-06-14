package com.example.trabpratico.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.R
import com.example.trabpratico.network.ObsResponse


class ObsAdapter : RecyclerView.Adapter<ObsAdapter.ObsViewHolder>() {

    private var observations: List<ObsResponse> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_observation, parent, false)
        return ObsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObsViewHolder, position: Int) {
        holder.bind(observations[position])}

    override fun getItemCount(): Int = observations.size

    fun submitList(obsList: List<ObsResponse>) {
        observations = obsList
        notifyDataSetChanged()
    }

    class ObsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val obsNameTextView: TextView = itemView.findViewById(R.id.obsNameTextView)
        private val obsContentTextView: TextView = itemView.findViewById(R.id.obsContentTextView)

        fun bind(obs: ObsResponse) {
            obsNameTextView.text = obs.nameobs
            obsContentTextView.text = obs.content
        }
    }
}