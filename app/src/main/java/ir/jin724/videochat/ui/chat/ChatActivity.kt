package ir.jin724.videochat.ui.chat

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import ir.jin724.videochat.R
import ir.jin724.videochat.VideoChatApp
import ir.jin724.videochat.data.chatRepository.ChatItem
import ir.jin724.videochat.data.userRepository.User
import ir.jin724.videochat.databinding.ActivityChatBinding
import ir.jin724.videochat.util.FixedOffsetDividerDecoration2
import ir.jin724.videochat.util.GlideApp
import ir.jin724.videochat.util.KeyboardUtil

class ChatActivity : AppCompatActivity(), View.OnClickListener {

    private val binding: ActivityChatBinding by lazy {
        DataBindingUtil.setContentView<ActivityChatBinding>(this, R.layout.activity_chat)
    }

    companion object {
        const val PAYLOAD = "chat_payload"
    }

    private lateinit var chatAdapter: ChatAdapter
    private val user: User by lazy {
        (application as VideoChatApp).prefsManager.getUser()
    }

    private lateinit var otherUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        updateToolbar()
        setUpList()
        uploadChatBackground()
        setUpMessageBox()
        setClickListenersUp()
    }

    private fun setClickListenersUp() {
        binding.ivSend.setOnClickListener(this)
        binding.ivAttach.setOnClickListener(this)
    }

    private fun setUpMessageBox() {
        binding.etMessage.setOnEditorActionListener { v, actionId, event ->
            if (actionId in arrayOf(EditorInfo.IME_ACTION_SEND, EditorInfo.IME_ACTION_DONE)) {
                val message = v.text.toString()
                if (performMessage(message)) {
                    KeyboardUtil.closeKeyBoard(v.windowToken, this)
                }
            }
            return@setOnEditorActionListener false
        }
    }

    private fun sendMessage(message: String) {
        Toast.makeText(this, "sendMessage", Toast.LENGTH_SHORT).show()
        chatAdapter.addItem(ChatItem(-1, message, user.userId, otherUser.userId, false, ""))
        // todo send message to server
    }

    private fun uploadChatBackground() {
        GlideApp.with(this)
            .load(R.drawable.chat_background)
            .centerCrop()
            .into(binding.ivBackground)
    }

    private fun updateToolbar() {
        intent?.run {
            otherUser = getParcelableExtra(PAYLOAD) ?: throw Exception("otherUser must not be null")
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setUpList() {
        with(binding.rvChats) {
            layoutManager =
                LinearLayoutManager(this@ChatActivity, LinearLayoutManager.VERTICAL, true)
            addItemDecoration(
                FixedOffsetDividerDecoration2(
                    this@ChatActivity,
                    48,
                    8,
                    8,
                    8,
                    false,
                    1
                )
            )
            chatAdapter = ChatAdapter(user)
            adapter = chatAdapter
        }
    }

    private fun performMessage(message: String): Boolean {
        // todo perform message validity and other stuff here
        if (message.isNotEmpty()) {
            binding.etMessage.text?.clear()
            sendMessage(message)
            return true
        }

        return false
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_send -> {
                val message = binding.etMessage.text?.toString()
                message?.let {
                    performMessage(message)
                }
            }
            R.id.iv_attach -> {
                Toast.makeText(this, "attach", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
