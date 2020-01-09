package ir.jin724.videochat.ui.main

import android.view.ViewGroup
import androidx.collection.ArraySet
import androidx.recyclerview.widget.RecyclerView
import ir.jin724.videochat.data.userRepository.User
import ir.jin724.videochat.databinding.ItemUserBinding
import ir.jin724.videochat.util.inflater


class MainAdapter : RecyclerView.Adapter<UserHolder>() {

    private val dataSet = ArraySet<User>()

    fun addUsers(list: List<User>) {
        dataSet.addAll(list)
        notifyDataSetChanged()
    }

    fun addUser(user: User) {
        dataSet.removeAll {
            it.userId == user.userId
        }
        dataSet.add(user)
        notifyDataSetChanged()
    }

    fun onUserDisconnected(clientId: String) {
        dataSet.forEach {
            if (it.clientId == clientId) {
                it.state = "offline"
            }
        }

        notifyDataSetChanged()
    }

    private fun getItem(position: Int) = dataSet.valueAt(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val binding = ItemUserBinding.inflate(parent.inflater())
        return UserHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }
}


