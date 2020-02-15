package ir.jin724.videochat.ui.chat.holder

import androidx.recyclerview.widget.RecyclerView
import ir.jin724.videochat.R
import ir.jin724.videochat.VideoChatApp
import ir.jin724.videochat.data.chatRepository.ChatItem
import ir.jin724.videochat.databinding.ItemMyMessaageBinding
import ir.jin724.videochat.util.decodeBase64
import ir.jin724.videochat.util.setImageResIf
import timber.log.Timber


class MyMessageHolder(private val binding: ItemMyMessaageBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(chatItem: ChatItem) {
        binding.tvMessage.text = chatItem.text.decodeBase64()
        Timber.e("time is %s", chatItem.time)
        binding.tvTime.text = VideoChatApp.dateConverter.convert2(chatItem.time)

        binding.ivDelivery.setImageResIf(
            chatItem.delivered,
            R.drawable.ic_check_double_24dp,
            R.drawable.ic_check_24dp
        )
    }

}