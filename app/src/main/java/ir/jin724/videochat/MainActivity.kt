package ir.jin724.videochat

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kotlinpermissions.KotlinPermissions
import ir.jin724.videochat.webRTC.WebRTCClient
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var webRtcClient: WebRTCClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            application,
            local_view,
            remote_view
        )

        call_button.setOnClickListener {
            webRtcClient.call()
        }

        answer_button.setOnClickListener {
            webRtcClient.answer()
        }

        webRtcClient.start()
    }


}
