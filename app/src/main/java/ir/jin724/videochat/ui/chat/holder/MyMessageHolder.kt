package ir.jin724.videochat.ui.chat.holder

import androidx.recyclerview.widget.RecyclerView
import ir.jin724.videochat.R
import ir.jin724.videochat.data.chatRepository.ChatItem
import ir.jin724.videochat.databinding.ItemMyMessaageBinding
import ir.jin724.videochat.util.setImageResIf


class MyMessageHolder(private val binding: ItemMyMessaageBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(chatItem: ChatItem) {
        binding.tvMessage.text = "$adapterPosition${chatItem.text}"
        binding.tvTime.text = chatItem.time

        binding.ivDelivery.setImageResIf(
            chatItem.delivered,
            R.drawable.ic_check_double_24dp,
            R.drawable.ic_check_24dp
        )
    }

}