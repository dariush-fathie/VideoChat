package ir.jin724.videochat.ui.chat

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import ir.jin724.videochat.R
import ir.jin724.videochat.data.userRepository.User
import ir.jin724.videochat.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private val binding: ActivityChatBinding by lazy {
        DataBindingUtil.setContentView<ActivityChatBinding>(this, R.layout.activity_chat)
    }

    companion object {
        const val PAYLOAD = "chat_payload"
    }

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        updateToolbar()
        setUpList()
    }

    private fun updateToolbar() {
        intent?.run {
            val otherUser = getParcelableExtra<User>(PAYLOAD)
            otherUser
        }?.let {
            binding.toolbar.subtitle = it.firstName + " " + it.lastName
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun setUpList() {
        with(binding.rvChats) {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }
    }
}
