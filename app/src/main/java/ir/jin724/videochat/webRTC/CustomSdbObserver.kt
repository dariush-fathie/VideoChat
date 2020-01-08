package ir.jin724.videochat.webRTC

import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

open class CustomSdbObserver : SdpObserver {
    override fun onSetFailure(p0: String?) {

    }

    override fun onSetSuccess() {

    }

    override fun onCreateSuccess(p0: SessionDescription?) {

    }

    override fun onCreateFailure(p0: String?) {

    }
}