package ir.jin724.videochat.webRTC

import android.os.Parcelable
import ir.jin724.videochat.data.userRepository.User
import kotlinx.android.parcel.Parcelize


@Parcelize
data class WebRTCConfig(
    val bob: User,
    val audioEnabled: Boolean = true,
    val videoEnabled: Boolean = true,
    val isScreenCast: Boolean = false,
    val alignTimeStamps: Boolean = false,
    val enableRtpDataChannel: Boolean = false
) :
    Parcelable