package ir.jin724.videochat.ui.chat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.jin724.videochat.data.chatRepository.ChatItem
import ir.jin724.videochat.data.chatRepository.ChatResponse
import ir.jin724.videochat.data.userRepository.User
import ir.jin724.videochat.databinding.ItemBobMessageBinding
import ir.jin724.videochat.databinding.ItemMyMessaageBinding
import ir.jin724.videochat.ui.chat.holder.BobMessageHolder
import ir.jin724.videochat.ui.chat.holder.MyMessageHolder
import ir.jin724.videochat.util.inflater
import timber.log.Timber

class ChatAdapter(private val user: User) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataSet = ArrayList<ChatItem>()

    companion object {
        const val MY_CHAT_TYPE = 0
        const val RECEIVED_CHAT_TYPE = 1
    }

    private fun getItem(position: Int): ChatItem {
        return dataSet[position]
    }

    fun addItems(items: List<ChatItem>) {
        dataSet.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(item: ChatItem) {
        dataSet.add(item)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MY_CHAT_TYPE) {
            MyMessageHolder(ItemMyMessaageBinding.inflate(parent.inflater()))
        } else {
            BobMessageHolder(ItemBobMessageBinding.inflate(parent.inflater()))
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Timber.e("isFromMine ${user.userId} ${getItem(position).from} %b", isFromMine(position))

        if (isFromMine(position)) {
            holder as MyMessageHolder
            holder.bind(getItem(position))
        } else {
            holder as BobMessageHolder
            holder.bind(getItem(position))
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isFromMine(position)) {
            MY_CHAT_TYPE
        } else {
            RECEIVED_CHAT_TYPE
        }
    }

    private fun isFromMine(position: Int) = getItem(position).from == user.userId

    fun delivered(response: ChatResponse) {
        val result = dataSet.filter {
            it.ChatItemId == response.tempChatItemId
        }
        if (result.isNotEmpty()) {
            result.first().delivered = response.delivered
            result.first().ChatItemId = response.chatItemId
        }
        // todo notify only changed position
        notifyDataSetChanged()
    }

}