package ir.jin724.videochat.ui.main

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import ir.jin724.videochat.R
import ir.jin724.videochat.data.userRepository.User
import ir.jin724.videochat.databinding.ItemUserBinding
import ir.jin724.videochat.util.GlideApp
import ir.jin724.videochat.util.getColour
import ir.jin724.videochat.util.hide

class UserHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

    private val context: Context = binding.root.context

    fun bind(user: User) {
        GlideApp.with(context)
            .load(user.avatar)
            .into(binding.ivAvatar)

        binding.tvFirstName.text = user.firstName
        binding.tvLastName.text = user.lastName

        /*if (user.isOnline()) {
            binding.ivAvatar.borderColor = context.getColour(R.color.online)
        } else {
            binding.ivAvatar.borderColor = context.getColour(R.color.offline)
        }*/
        binding.viewStatus.hide(!user.isOnline())
        binding.tvAddress.text = ""

        itemView.setOnClickListener {

        }
    }

}