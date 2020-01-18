package ir.jin724.videochat.ui.chat.holder

import android.text.format.DateUtils
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ir.jin724.videochat.R
import ir.jin724.videochat.VideoChatApp.Companion.dateConverter
import ir.jin724.videochat.data.chatRepository.ChatItem
import ir.jin724.videochat.databinding.ItemBobMessageBinding
import ir.jin724.videochat.util.setImageResIf


class BobMessageHolder(private val binding: ItemBobMessageBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(chatItem: ChatItem) {
        binding.tvMessage.text = chatItem.text
        binding.tvTime.text = dateConverter.convert2(chatItem.time)


        binding.ivDelivery.setImageResIf(
            chatItem.delivered,
            R.drawable.ic_check_24dp,
            R.drawable.ic_check_double_24dp
        )

        binding.ivDelivery.visibility = View.GONE
    }

}