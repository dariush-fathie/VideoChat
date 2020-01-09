package ir.jin724.videochat.ui.chat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.jin724.videochat.data.chatRepository.ChatItem
import ir.jin724.videochat.data.userRepository.User
import ir.jin724.videochat.databinding.ItemChatReceivedBinding
import ir.jin724.videochat.databinding.ItemMyChatBinding
import ir.jin724.videochat.ui.chat.holder.MyMessageHolder
import ir.jin724.videochat.ui.chat.holder.ReceivedMessageHolder
import ir.jin724.videochat.util.inflater

class ChatAdapter(private val user: User) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataSet = ArrayList<ChatItem>()

    companion object {
        private const val MY_CHAT_TYPE = 0
        private const val RECEIVED_CHAT_TYPE = 1
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
            MyMessageHolder(ItemMyChatBinding.inflate(parent.inflater()))
        } else {
            ReceivedMessageHolder(ItemChatReceivedBinding.inflate(parent.inflater()))
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isFromMine(position)) {
            holder as MyMessageHolder
            holder.bind(getItem(position))
        } else {
            holder as ReceivedMessageHolder
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

}