package com.example.trabpratico.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.databinding.ItemUserBinding
import com.example.trabpratico.network.UserDetailsResponse

class UserAdapter(private val clickListener: UserClickListener) : ListAdapter<UserDetailsResponse, UserAdapter.UserViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<UserDetailsResponse>() {
        override fun areItemsTheSame(oldItem: UserDetailsResponse, newItem: UserDetailsResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserDetailsResponse, newItem: UserDetailsResponse): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user, clickListener)
    }

    class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserDetailsResponse, clickListener: UserClickListener) {
            binding.user = user
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    class UserClickListener(val clickListener: (user: UserDetailsResponse) -> Unit) {
        fun onClick(user: UserDetailsResponse) = clickListener(user)
    }
}