package ir.jin724.videochat.webRTC

import com.google.gson.Gson
import io.socket.client.Socket
import ir.jin724.videochat.VideoChatApp
import ir.jin724.videochat.data.userRepository.User
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import timber.log.Timber

//@PerApplication
class WebRTCSocketHandler(private val socket: Socket, event: WebRTCEvent, private val gson: Gson) {

    private val OFFER = "offer"
    private val ANSWER = "answer"
    private val CANDIDATE = "candidate"
    private val CANDIDATE_REMOVED = "candidate_removed"
    private val DISPOSE = "dispose"
    private val UNAVAILABLE = "unavailable"
    private val tag = this::class.java.simpleName
    private val SOCKET_PAYLOAD_SESSION_DESCRIPTION = "sd"
    private val SOCKET_PAYLOAD_ICE_CANDIDATE = "ice"
    private val SOCKET_PAYLOAD_BOB = "bob"


    init {
        socket.on(OFFER) { args ->
            Timber.tag(tag).e(OFFER)

            // یک درخواست از کاربری دیگر امده است
            //  باید session کاربر ذخیره شود و پاسخ او را بدهیم
            // البته ممکن است نخواهیم پاسخ دهیم که باید کنترل شدو

            // <--- args structure -->
            // { "sd" : {sessionDescription} , "bob" : {Bob} }
            if (args.isNotEmpty()) {
                val wrapperObject = args[0] as JSONObject

                if (!wrapperObject.has(SOCKET_PAYLOAD_SESSION_DESCRIPTION) || !wrapperObject.has(
                        SOCKET_PAYLOAD_BOB
                    )
                )
                    throw Exception("sd or bob is not available. please check offer emit at nodeJs server")

                val sdp = gson.fromJson<SessionDescription>(
                    wrapperObject.get(SOCKET_PAYLOAD_SESSION_DESCRIPTION).toString(),
                    SessionDescription::class.java
                )

                val bob = gson.fromJson<User>(
                    wrapperObject.get(SOCKET_PAYLOAD_BOB).toString(),
                    User::class.java
                )

                Timber.e("sdp from bob : %s", sdp.toString())
                Timber.e("bob user : %s", bob.toString())

                event.onOffer(sdp, bob)
            }
        }

        socket.on(ANSWER) { args ->
            Timber.tag(tag).e(ANSWER)

            // درخواست ما پاسخ داده شده است
            // پس طرف مقابل session خود را برای ما ارسال می کند تا اتصال برقرار شود
            // البته ممکن است پاسخ ندهد که باید کنترل شود

            if (args.isNotEmpty()) {
                val wrapperObject = args[0] as JSONObject

                if (!wrapperObject.has(SOCKET_PAYLOAD_SESSION_DESCRIPTION) || !wrapperObject.has(
                        SOCKET_PAYLOAD_BOB
                    )
                )
                    throw Exception("sd or bob is not available. please check answer emit at nodeJs server")

                val sdp = gson.fromJson<SessionDescription>(
                    wrapperObject.get(SOCKET_PAYLOAD_SESSION_DESCRIPTION).toString(),
                    SessionDescription::class.java
                )

                val bob = gson.fromJson<User>(
                    wrapperObject.get(SOCKET_PAYLOAD_BOB).toString(),
                    User::class.java
                )

                Timber.e("sdp from bob : %s", sdp.toString())
                Timber.e("bob user :%s", bob.toString())
                event.onAnswer(sdp, bob)
            }
        }

        socket.on(CANDIDATE) { args ->
            if (args.isNotEmpty()) {
                Timber.tag(tag).e(CANDIDATE)

                val wrapperObject = args[0] as JSONObject

                if (!wrapperObject.has(SOCKET_PAYLOAD_ICE_CANDIDATE) || !wrapperObject.has(
                        SOCKET_PAYLOAD_BOB
                    )
                )
                    throw Exception("sd or bob is not available. please check candidate emit at nodeJs server")

                val ice = gson.fromJson<IceCandidate>(
                    wrapperObject.get(SOCKET_PAYLOAD_ICE_CANDIDATE).toString(),
                    IceCandidate::class.java
                )

                val bob = gson.fromJson<User>(
                    wrapperObject.get(SOCKET_PAYLOAD_BOB).toString(),
                    User::class.java
                )

                Timber.e("ice candidate %s", ice.toString())
                Timber.e("bob user %s", bob.toString())

                event.onICECandidate(ice)
            }
        }

        socket.on(DISPOSE) {
            Timber.tag(tag).e(DISPOSE)

            event.onDisconnect()
        }

        socket.on(UNAVAILABLE) {
            Timber.tag(tag).e(UNAVAILABLE)

            event.onUnavailable()
        }

        socket.on(CANDIDATE_REMOVED) { args ->
            Timber.tag(tag).e(CANDIDATE_REMOVED)

            // todo complete this
            event.onICERemoved(arrayOf())
        }
    }


    fun sendIce(ice: IceCandidate, bob: User) {
        socket.emit(
            CANDIDATE,
            VideoChatApp.gson.toJson(ice, IceCandidate::class.java),
            bob.toJson()
        )
    }

    fun sendICERemoved(ice: Array<out IceCandidate>, bob: User) {
        // todo complete this later
        // todo check type token later
        socket.emit(CANDIDATE_REMOVED, gson.toJson(ice, Array<out IceCandidate>::class.java), bob)
    }

    fun sendOffer(sdp: SessionDescription, bob: User) {
        socket.emit(
            OFFER,
            gson.toJson(sdp, SessionDescription::class.java),
            bob.toJson()
        )
    }

    fun sendAnswer(sdp: SessionDescription, bob: User) {
        socket.emit(
            ANSWER,
            gson.toJson(sdp, SessionDescription::class.java),
            bob.toJson()
        )
    }

    fun dispose() {
        socket.off(OFFER)
        socket.off(ANSWER)
        socket.off(CANDIDATE)
        socket.off(DISPOSE)
        socket.off(UNAVAILABLE)
        // todo what to do else ?
    }

}