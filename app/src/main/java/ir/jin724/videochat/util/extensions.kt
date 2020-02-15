package ir.jin724.videochat.util

import android.content.Context
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import ir.jin724.videochat.webRTC.WebRTCClient
import org.json.JSONObject
import org.webrtc.IceCandidate

fun ViewGroup.inflater(): LayoutInflater {
    return LayoutInflater.from(context)
}


fun View.hide(boolean: Boolean) {
    visibility = if (boolean) {
        View.GONE
    } else {
        View.VISIBLE
    }
}


fun Context.getColour(@ColorRes color: Int): Int {
    return ContextCompat.getColor(this, color)
}

fun View.getColour(@ColorRes color: Int): Int {
    return ContextCompat.getColor(context, color)
}


fun AppCompatImageView.setImageResIf(condition: Boolean, @DrawableRes ifTrue: Int, @DrawableRes ifFalse: Int) {
    if (condition) {
        setImageResource(ifTrue)
    } else {
        setImageResource(ifFalse)
    }
}

fun Int.toPx(context: Context): Float {
    return Converter.pxFromDp(context, this + 0f)
}


fun Float.toPx(context: Context): Float {
    return Converter.pxFromDp(context, this)
}

fun JSONObject.toIce(): IceCandidate {
    return IceCandidate(
        getString(WebRTCClient.SDP_MID),
        getInt(WebRTCClient.SDP_M_LINE_INDEX),
        getString(WebRTCClient.SDP)
    )
}


fun String.decodeBase64(): String {
    return String(Base64.decode(this, Base64.DEFAULT))
}


fun String.encodeBase64(): String {
    return String(Base64.encode(this.toByteArray(), Base64.DEFAULT))
}