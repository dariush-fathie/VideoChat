package ir.jin724.videochat.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.JsonObject
import ir.jin724.videochat.SignallingClientListener
import ir.jin724.videochat.VideoChatApp
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {


    private var token = ""
    private var messageId = -1
    val gson = Gson()


    companion object {
        var listener: SignallingClientListener? = null

        fun setListenerInstance(listener: SignallingClientListener) {
            this.listener = listener
        }
    }


    private val tag = this::class.java.simpleName

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data["data"]
        Timber.tag(tag).e("message received : $data")
        val jsonObject = gson.fromJson(data, JsonObject::class.java)

        if (jsonObject.has("serverUrl")) {
            Timber.tag(tag).e("onIceCandidateReceived")
            listener?.onIceCandidateReceived(
                gson.fromJson(
                    jsonObject,
                    IceCandidate::class.java
                )
            )
        } else if (jsonObject.has("type") && jsonObject.get("type").asString == "OFFER") {
            Timber.tag(tag).e("onOfferReceived")
            listener?.onOfferReceived(
                gson.fromJson(
                    jsonObject,
                    SessionDescription::class.java
                )
            )
        } else if (jsonObject.has("type") && jsonObject.get("type").asString == "ANSWER") {
            Timber.tag(tag).e("onAnswerReceived")
            listener?.onAnswerReceived(
                gson.fromJson(
                    jsonObject,
                    SessionDescription::class.java
                )
            )
        }

    }


    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        VideoChatApp.token = newToken
    }


}