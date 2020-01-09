package ir.jin724.videochat.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import ir.jin724.videochat.R
import ir.jin724.videochat.VideoChatApp
import ir.jin724.videochat.data.userRepository.User
import ir.jin724.videochat.databinding.ActivityMainBinding
import ir.jin724.videochat.ui.signup.SignUpActivity
import ir.jin724.videochat.util.PrefsManager

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    private val prefsManager: PrefsManager by lazy {
        (application as VideoChatApp).prefsManager
    }

    private val user: User by lazy {
        prefsManager.getUser()
    }

    private lateinit var mainAdapter: MainAdapter

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (user.userId == -1) {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        setUpList()
        getAllOnlineUsers()
    }

    private fun getAllOnlineUsers() {
        val user = prefsManager.getUser()
        viewModel.getAllUsers().observe(this) {
            mainAdapter.addUsers(it.filter { u ->
                u.userId != user.userId
            })
            // ofter users fetch we start observing them
            viewModel.observeUsers(user)
        }

        viewModel.connectionEvent.observe(this) {
            mainAdapter.addUser(it)
        }

        viewModel.disconnectEvent.observe(this) {
            mainAdapter.onUserDisconnected(it)
        }

    }

    private fun setUpList() {
        with(binding.rvMain) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            mainAdapter = MainAdapter()
            adapter = mainAdapter
        }
    }
}
