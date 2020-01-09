package ir.jin724.videochat.ui.chat.holder

import androidx.recyclerview.widget.RecyclerView
import ir.jin724.videochat.R
import ir.jin724.videochat.data.chatRepository.ChatItem
import ir.jin724.videochat.databinding.ItemMyChatBinding
import ir.jin724.videochat.util.setImageResIf
import timber.log.Timber


class MyMessageHolder(private val binding: ItemMyChatBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(chatItem: ChatItem) {
        binding.tvMessage.text = chatItem.text
        binding.tvTime.text = chatItem.time

        binding.ivDelivery.setImageResIf(
            chatItem.delivered,
            R.drawable.ic_check_double_24dp,
            R.drawable.ic_check_24dp
        )
    }

}