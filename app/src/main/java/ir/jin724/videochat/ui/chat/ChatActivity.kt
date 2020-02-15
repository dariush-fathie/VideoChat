package ir.jin724.videochat.ui.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.icu.text.DateFormat
import android.icu.util.Calendar
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import ir.jin724.videochat.R
import ir.jin724.videochat.VideoChatApp
import ir.jin724.videochat.data.chatRepository.ChatItem
import ir.jin724.videochat.data.userRepository.User
import ir.jin724.videochat.databinding.ActivityChatBinding
import ir.jin724.videochat.ui.call.WebRTCActivity
import ir.jin724.videochat.util.*
import ir.jin724.videochat.webRTC.WebRTCConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity(), View.OnClickListener {

    private val binding: ActivityChatBinding by lazy {
        DataBindingUtil.setContentView<ActivityChatBinding>(this, R.layout.activity_chat)
    }

    companion object {
        const val PAYLOAD = "chat_payload"
    }

    private lateinit var chatAdapter: ChatAdapter
    private val me: User by lazy {
        (application as VideoChatApp).prefsManager.getUser()
    }

    private lateinit var bob: User

    private val viewModel: ChatViewModel by lazy {
        ViewModelProviders.of(this).get(ChatViewModel::class.java)
    }

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
        getMessagesHistory()
        setUpObservers()
    }

    private lateinit var job: Job
    private fun setUpObservers() {
        viewModel.sendChatResult.observe(this) {
            chatAdapter.delivered(it)
            binding.rvChats.post {
                binding.rvChats.scrollToPosition(chatAdapter.itemCount - 1)
            }
        }

        viewModel.liveNewMessage.observe(this) {
            chatAdapter.addItem(it)
            playSound(true)
            binding.rvChats.post {
                binding.rvChats.scrollToPosition(chatAdapter.itemCount - 1)
            }
        }



        viewModel.isWritingEvent.observe(this) {
            binding.toolbar.subtitle = "is writing..."

            lifecycleScope.launch {
                try {
                    job.cancel()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                job = launch {
                    delay(800)
                    launch(lifecycleScope.coroutineContext) {
                        binding.toolbar.subtitle = bob.firstName + " " + bob.lastName
                    }
                }
            }

        }
    }

    private fun getMessagesHistory() {
        viewModel.getChatHistory(me.userId, bob.userId).observe(this) {
            chatAdapter.addItems(it)

            binding.rvChats.post {
                binding.rvChats.scrollToPosition(chatAdapter.itemCount - 1)
            }
        }
    }

    private fun setClickListenersUp() {
        binding.ivSend.setOnClickListener(this)
        binding.ivAttach.setOnClickListener(this)
    }

    private fun setUpMessageBox() {
        /*binding.etMessage.setOnEditorActionListener { v, actionId, event ->
            if (actionId in arrayOf(EditorInfo.IME_ACTION_SEND, EditorInfo.IME_ACTION_DONE)) {
                val message = v.text.toString()
                if (performMessage(message)) {
                    KeyboardUtil.closeKeyBoard(v.windowToken, this)
                }
            }
            return@setOnEditorActionListener false
        }*/

        binding.etMessage.doOnTextChanged { text, _, _, _ ->
            viewModel.emitIsWriting(me, bob)
        }
    }

    private fun sendMessage(message: String) {
        Toast.makeText(this, "sendMessage", Toast.LENGTH_SHORT).show()
        val chatItem = ChatItem(
            ChatUtil.generateTempChatItemId(me),
            message.encodeBase64(),
            me.userId,
            bob.userId,
            -1,
            false,
            DateConverter.getInstance().currentDate3()
        )

        chatAdapter.addItem(chatItem)
        playSound(false)
        binding.rvChats.post {
            binding.rvChats.scrollToPosition(chatAdapter.itemCount - 1)
        }
        // todo send message to server

        viewModel.sendMessage(chatItem)
    }

    private fun uploadChatBackground() {
        GlideApp.with(this)
            .load(R.drawable.chat_background)
            .centerCrop()
            .into(binding.ivBackground)
    }

    private fun updateToolbar() {
        intent?.run {
            bob = getParcelableExtra(PAYLOAD) ?: throw Exception("otherUser must not be null")
            bob
        }?.let {
            binding.toolbar.subtitle = it.firstName + " " + it.lastName
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_video_call -> videoCall()
            R.id.menu_voice_call -> voiceCall()
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
                LinearLayoutManager(this@ChatActivity, LinearLayoutManager.VERTICAL, false)
                    .apply {
                        stackFromEnd = true
                        isSmoothScrollbarEnabled = true
                    }
            /*addItemDecoration(
                ChatDecoration(this@ChatActivity , 8)
            )*/
            chatAdapter = ChatAdapter(me)
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

    private fun playSound(inMessage: Boolean) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val thePlayer = MediaPlayer.create(
            this,
            if (inMessage) R.raw.sound_in else R.raw.sound_out
        )

        try {
            thePlayer.setVolume(
                (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) / 7.0).toFloat(),
                (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) / 7.0).toFloat()
            )
        } catch (e: Exception) {
            e.printStackTrace()

        }

        thePlayer.start()
    }


    private val videoCallRequestCode = 1515

    private fun voiceCall() {
        startActivityForResult(Intent(this, WebRTCActivity::class.java).apply {
            putExtra(Constants.WEB_RTC_CONFIG, WebRTCConfig(bob, videoEnabled = false))
            putExtra(Constants.BOB , bob)
        }, videoCallRequestCode)
    }

    private fun videoCall() {
        startActivityForResult(Intent(this, WebRTCActivity::class.java).apply {
            putExtra(Constants.WEB_RTC_CONFIG, WebRTCConfig(bob))
            putExtra(Constants.BOB , bob)
        }, videoCallRequestCode)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == videoCallRequestCode) {
            Toast.makeText(this, "Good luck", Toast.LENGTH_LONG).show()
        }
    }


}
