package com.example.trabpratico.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.R
import com.example.trabpratico.network.ObsResponse
import com.example.trabpratico.network.UserDetailsResponse

class UserObsAdapter : RecyclerView.Adapter<UserObsAdapter.UserObsViewHolder>() {

    private val userObservationsMap = mutableMapOf<UserDetailsResponse, List<ObsResponse>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserObsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_observations, parent, false)
        return UserObsViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserObsViewHolder, position: Int) {
        val user = userObservationsMap.keys.elementAt(position)
        holder.bind(user, userObservationsMap[user] ?: emptyList())
    }

    override fun getItemCount(): Int = userObservationsMap.size

    fun submitUserObservations(userObservations: Map<UserDetailsResponse, List<ObsResponse>>) {
        userObservationsMap.clear()
        userObservationsMap.putAll(userObservations)
        notifyDataSetChanged()
    }

    class UserObsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textUserName: TextView = itemView.findViewById(R.id.textUserName)
        private val recyclerViewUserObs: RecyclerView = itemView.findViewById(R.id.recyclerViewUserObs)
        private val obsAdapter = ObsAdapter()

        init {
            recyclerViewUserObs.layoutManager = LinearLayoutManager(itemView.context)
            recyclerViewUserObs.adapter = obsAdapter
        }

        fun bind(user: UserDetailsResponse, observations: List<ObsResponse>) {
            textUserName.text = user.name
            obsAdapter.submitList(observations)
        }
    }
}