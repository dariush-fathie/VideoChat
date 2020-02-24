package ir.jin724.videochat.webRTC

import ir.jin724.videochat.data.userRepository.User
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

interface WebRTCEvent {
    fun onICECandidate(ice: IceCandidate)
    fun onICERemoved(ice: Array<IceCandidate>)
    fun onOffer(sdp: SessionDescription, bob: User)
    fun onAnswer(sdp: SessionDescription, bob: User)
    fun sendAnswer(sdp: SessionDescription, bob: User)
    fun sendOffer(sdp: SessionDescription, bob: User)
    fun sendICE(ice: IceCandidate, bob: User)
    fun sendICERemoved(ice: Array<out IceCandidate>?, bob: User)
    fun onDisconnect()
    fun onUnavailable()
}