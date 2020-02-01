package ir.jin724.videochat.ui.call

import android.Manifest
import android.animation.Animator
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.kotlinpermissions.KotlinPermissions
import ir.jin724.videochat.R
import ir.jin724.videochat.data.userRepository.User
import ir.jin724.videochat.databinding.ActivityWebrtcBinding
import ir.jin724.videochat.util.Constants
import ir.jin724.videochat.util.CustomAnimationListener
import ir.jin724.videochat.webRTC.WebRTCClient
import ir.jin724.videochat.webRTC.WebRTCConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


class WebRTCActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var webRtcClient: WebRTCClient
    private lateinit var config: WebRTCConfig


    private val binding: ActivityWebrtcBinding by lazy {
        DataBindingUtil.setContentView<ActivityWebrtcBinding>(this, R.layout.activity_webrtc)
    }

    private val bob: User by lazy {
        intent.getParcelableExtra(Constants.BOB) as User
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        config = intent.getParcelableExtra(Constants.WEB_RTC_CONFIG)
            ?: throw Exception("webRTCConfig must not be null")


        KotlinPermissions.with(this)
            .permissions(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .onAccepted {
                Timber.e("Granted")
                initWebRTCClient()
            }
            .ask()


        if (Build.VERSION.SDK_INT >= 21) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )
        }

        binding.btnSwitchCamera.setOnClickListener(this)
        binding.btnVideo.setOnClickListener(this)
        binding.btnVoice.setOnClickListener(this)
        binding.root.setOnClickListener(this)

        lifecycleScope.launch {
            hideWithDelay()
        }
    }


    private fun initWebRTCClient() {
        webRtcClient = WebRTCClient(
            this.application,
            config,
            binding.localView,
            binding.remoteView,
            bob
        )

        binding.localView.setZOrderMediaOverlay(true)
        binding.localView.setZOrderOnTop(true)
        //binding.remoteView.setZOrderMediaOverlay(true)
        //binding.remoteView.setZOrderOnTop(true)

        binding.btnDispose.setOnClickListener {
            Timber.e("dispose click")
            webRtcClient.dispose()
            setResult(Activity.RESULT_OK)
            finish()
        }

        webRtcClient.start()
        webRtcClient.call()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_voice -> {

            }
            R.id.btn_video -> {

            }
            R.id.btn_switch_camera -> {
                webRtcClient.switchCamera()
            }
            R.id.root -> {
                controlButtonsVisibility()
            }
        }
    }

    private lateinit var hideJob: Job
    private var visibility = true


    private fun controlButtonsVisibility() {
        lifecycleScope.launch {
            if (this@WebRTCActivity::hideJob.isInitialized) {
                hideJob.cancel()
            }
            Timber.e("root clicked")
            if (visibility) {
                hideButtons()
            } else {
                showButtons()
                hideWithDelay()
            }
        }
    }

    private suspend fun hideWithDelay() {
        coroutineScope {
            delay(5000)
            hideJob = lifecycleScope.launch {
                hideButtons()
            }
        }
    }


    private fun hideButtons() {
        binding.btnDispose.animate().setDuration(300).alpha(0f)
            .translationY(50f)
            .setListener(object : CustomAnimationListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    binding.btnDispose.visibility = View.GONE
                }
            })
        binding.llBottom.animate().setDuration(300).alpha(0f)
            .translationY(50f)
            .setListener(object : CustomAnimationListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    binding.llBottom.visibility = View.GONE
                }
            })
        visibility = false
    }

    private fun showButtons() {
        // todo animate visibility
        binding.btnDispose.animate().setDuration(150).alpha(1f)
            .translationY(0f)
            .setListener(object : CustomAnimationListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    binding.btnDispose.visibility = View.VISIBLE
                }
            })
        binding.llBottom.animate().setDuration(150).alpha(1f)
            .translationY(0f)
            .setListener(object : CustomAnimationListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    binding.llBottom.visibility = View.VISIBLE
                }
            })
        visibility = true
    }


}
