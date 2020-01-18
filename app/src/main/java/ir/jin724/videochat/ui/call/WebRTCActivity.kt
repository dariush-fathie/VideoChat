package ir.jin724.videochat.ui.call

import android.Manifest
import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kotlinpermissions.KotlinPermissions
import ir.jin724.videochat.R
import ir.jin724.videochat.databinding.ActivityWebrtcBinding
import ir.jin724.videochat.util.Constants
import ir.jin724.videochat.webRTC.WebRTCClient
import ir.jin724.videochat.webRTC.WebRTCConfig
import timber.log.Timber


class WebRTCActivity : AppCompatActivity() {

    private lateinit var webRtcClient: WebRTCClient
    private lateinit var config: WebRTCConfig


    private val binding: ActivityWebrtcBinding by lazy {
        DataBindingUtil.setContentView<ActivityWebrtcBinding>(this, R.layout.activity_webrtc)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        config = intent.getParcelableExtra(Constants.WEB_RTC_CONFIG)
            ?: throw Exception("webRTCConfig must not be null")


        KotlinPermissions.with(this)
            .permissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            .onAccepted {
                Timber.e("Granted")
                initWebRTCClient()
            }
            .ask()

    }


    private fun initWebRTCClient() {
        webRtcClient = WebRTCClient(
            this.application,
            config,
            binding.localView,
            binding.remoteView
        )

        binding.btnCall.setOnClickListener {
            Timber.e("call click")
            webRtcClient.call()
        }

        binding.btnDispose.setOnClickListener {
            Timber.e("dispose click")
            webRtcClient.dispose()
            setResult(Activity.RESULT_OK)
            finish()
        }

        webRtcClient.start()
    }


}
