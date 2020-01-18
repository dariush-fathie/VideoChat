package ir.jin724.videochat.webRTC

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.util.Log
import ir.jin724.videochat.VideoChatApp
import ir.jin724.videochat.VideoChatApp.Companion.gson
import ir.jin724.videochat.data.userRepository.User
import org.json.JSONObject
import org.webrtc.*
import timber.log.Timber


class WebRTCClient(
    private val context: Application,
    private val config: WebRTCConfig,
    private val localViewRenderer: SurfaceViewRenderer,
    private val remoteViewRenderer: SurfaceViewRenderer
) {

    companion object {
        private const val LOCAL_VIDEO_TRACK_ID = "local_video_track"
        private const val LOCAL_AUDIO_TRACK_ID = "local_audio_track"
        private const val LOCAL_STREAM_ID = "local_stream_id"

        private const val AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation"
        private const val AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl"
        private const val AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter"
        private const val AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression"

        private const val OFFER = "offer"
        private const val ANSWER = "answer"
        private const val CANDIDATE = "candidate"
        private const val DISPOSE = "dispose"
        private const val UNAVAILABLE = "unavailable"

        const val SDP_MID = "sdpMid"
        const val SDP_M_LINE_INDEX = "sdpMLineIndex"
        const val SDP = "sdp"


        const val SOCKET_PAYLOAD_SESSION_DESCRIPTION = "sd"
        const val SOCKET_PAYLOAD_ICE_CANDIDATE = "ice"
        const val SOCKET_PAYLOAD_BOB = "bob"

        private val TAG = this::class.java.simpleName
    }

    private val socket = (context as VideoChatApp).socket
    private val rootEglBase: EglBase = EglBase.create()
    private val observer: PeerConnection.Observer

    private val audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private lateinit var surfaceTextureHelper: SurfaceTextureHelper

    init {
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        audioManager.isSpeakerphoneOn = true

        observer = object : PeerConnectionObserver() {
            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)
                Timber.tag(TAG).e("onIceCandidate : ${p0?.toString()}")
                //signallingClient.send(p0)
                // todo send ice candidate
                //dataRepo.sendData(p0)
                try {
                    socket.emit(
                        CANDIDATE,
                        gson.toJson(p0, IceCandidate::class.java),
                        config.bob.toJson()
                    )
                } catch (e: Exception) {
                    Timber.e("onIceCandidate socket error %s", e.message)
                    e.printStackTrace()
                }

                addIceCandidate(p0)
            }

            override fun onAddStream(p0: MediaStream?) {
                super.onAddStream(p0)
                Timber.tag(TAG).e("onAddStream : ${p0?.toString()}")
                p0?.videoTracks?.get(0)?.addSink(remoteViewRenderer)

                p0?.let { stream ->
                    stream.audioTracks?.let {
                        if (it.isNotEmpty()) {
                            it.forEach { audioTrack ->
                                // todo . what to do with audio tracks?
                            }
                        }
                    }

                    stream.videoTracks?.let {
                        if (it.isNotEmpty()) {
                            it.forEach { videoTrack ->
                                videoTrack?.addSink(remoteViewRenderer)
                            }
                        }
                    }
                }

            }

            override fun onRemoveStream(p0: MediaStream?) {
                super.onRemoveStream(p0)

                p0?.let { stream ->
                    stream.audioTracks?.let {
                        if (it.isNotEmpty()) {
                            it.forEach { audioTrack ->
                                // todo . what to do with audio tracks?
                            }
                        }
                    }

                    stream.videoTracks?.let {
                        if (it.isNotEmpty()) {
                            it.forEach { videoTrack ->
                                videoTrack?.removeSink(remoteViewRenderer)
                            }
                        }
                    }
                }
            }

            override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
                super.onIceCandidatesRemoved(p0)
                p0?.let {
                    remoteIceCandidate(it)
                }
                // todo send remote ice candidate
            }

        }
    }


    private fun initSocket() {
        // this events comes from bob
        socket.on(OFFER) { args ->
            Timber.e("****************** OFFER ****************")

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

                Timber.e("sdp from bob :", sdp.toString())
                Timber.e("bob user :", bob.toString())


                // باید بررسی شود آیا کاربر می تواند در این لحظه پاسخگو باشد یا نه
                setRemoteSessionDescription(sdp)
                answer()
            }


        }.on(ANSWER) { args ->
            Timber.e("****************** ANSWER ****************")

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

                Timber.e("sdp from bob :", sdp.toString())
                Timber.e("bob user :", bob.toString())

                setRemoteSessionDescription(sdp)
            }
            //answer()
        }.on(CANDIDATE) { args ->
            if (args.isNotEmpty()) {
                Timber.e("****************** CANDIDATE ****************")

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

                Timber.e("ice candidate", ice.toString())
                Timber.e("bob user", bob.toString())

                addIceCandidate(ice)
            }
        }


    }

    private val iceServer = listOf(
        /*PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
            .createIceServer(),
*/
        PeerConnection.IceServer.builder("stun:194.5.175.240:3478")
            .setUsername("kaptaRTC")
            .setPassword("17551755")
            .createIceServer(),

        PeerConnection.IceServer.builder("turn:194.5.175.240:3478")
            .setUsername("kaptaRTC")
            .setPassword("17551755")
            .createIceServer()
    )

    private val peerConnectionFactory by lazy {
        buildPeerConnectionFactory()
    }

    private val videoCapturer: VideoCapturer by lazy {
        createVideoCapturer(context)
    }

    private val localVideoSource by lazy {
        peerConnectionFactory.createVideoSource(config.isScreenCast, config.alignTimeStamps)
    }

    private val localAudioSource by lazy {
        val audioConstraints = MediaConstraints()

        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "true")
        )
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false")
        )
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "true")
        )
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "true")
        )

        peerConnectionFactory.createAudioSource(audioConstraints)
    }

    private val peerConnection by lazy {
        buildPeerConnection()
    }


    fun start() {
        initSurfaceView(localViewRenderer)
        initSurfaceView(remoteViewRenderer)
        initPeerConnectionFactory(context)
        startLocalVideoCapture(localViewRenderer)
        initSocket()
    }


    private fun initPeerConnectionFactory(context: Application) {
        // init peerConnectionFactory options globally
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)
    }


    private fun buildPeerConnectionFactory(): PeerConnectionFactory {
        // build an instance of peerConnectionFactory
        //val adm = JavaAudioDeviceModule.builder(context).createAudioDeviceModule()

        return PeerConnectionFactory
            .builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(rootEglBase.eglBaseContext))
            .setVideoEncoderFactory(
                DefaultVideoEncoderFactory(
                    rootEglBase.eglBaseContext,
                    true,
                    true
                )
            )
            // overriding options to add more option
            .setOptions(PeerConnectionFactory.Options().apply {
                disableEncryption = true
                disableNetworkMonitor = true
            })
            .createPeerConnectionFactory()
    }


    private fun buildPeerConnection(): PeerConnection? {
        val rtcConfiguration = PeerConnection.RTCConfiguration(iceServer)
        with(rtcConfiguration) {
            // bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
            // enableDtlsSrtp = true
            //enableRtpDataChannel = config.enableRtpDataChannel
        }
        return peerConnectionFactory.createPeerConnection(
            rtcConfiguration,
            observer
        )
    }


    private fun createVideoCapturer(context: Context): CameraVideoCapturer {
        //val fileVideoCapturer = FileVideoCapturer("");
        Timber.e("getVideoCapturer")
        return Camera2Enumerator(context).run {
            deviceNames.find {
                isFrontFacing(it)
            }?.let {
                createCapturer(it, null)
            } ?: throw IllegalStateException()
        }.apply {
            Timber.e("getVideoCapturer = $this")
        }
    }

    private fun startLocalVideoCapture(localSurfaceRenderer: SurfaceViewRenderer) {
        surfaceTextureHelper =
            SurfaceTextureHelper.create(Thread.currentThread().name, rootEglBase.eglBaseContext)

        Timber.e("videoCapturer = $videoCapturer")

        videoCapturer.initialize(
            surfaceTextureHelper,
            localSurfaceRenderer.context,
            localVideoSource.capturerObserver
        )

        // todo get resolution an fps from config
        videoCapturer.startCapture(1920, 1080, 60)

        val localVideoTrack =
            peerConnectionFactory.createVideoTrack(LOCAL_VIDEO_TRACK_ID, localVideoSource)
        localVideoTrack.addSink(localSurfaceRenderer)

        val localStream = peerConnectionFactory.createLocalMediaStream(LOCAL_STREAM_ID)

        //create an AudioSource instance
        val localAudioTrack =
            peerConnectionFactory.createAudioTrack(LOCAL_AUDIO_TRACK_ID, localAudioSource)

        /*WebRtcAudioManager.setStereoOutput(true)
        WebRtcAudioManager.setStereoOutput(true)*/

        localStream.addTrack(localVideoTrack)
        localStream.addTrack(localAudioTrack)
        peerConnection?.addStream(localStream)
    }


    private fun initSurfaceView(view: SurfaceViewRenderer) =
        view.run {
            setMirror(true)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
            setEnableHardwareScaler(true)
            setZOrderMediaOverlay(true)
            init(rootEglBase.eglBaseContext, null)
        }

    private fun setLocalSessionDescription(sessionDescription: SessionDescription) {
        peerConnection?.setLocalDescription(CustomSdbObserver(), sessionDescription)
    }

    private fun setRemoteSessionDescription(sessionDescription: SessionDescription) {
        peerConnection?.setRemoteDescription(CustomSdbObserver(), sessionDescription)
    }


    private fun createMediaConstraints(): MediaConstraints {
        return MediaConstraints().apply {
            mandatory.add(
                MediaConstraints.KeyValuePair(
                    "OfferToReceiveVideo",
                    "${config.videoEnabled}"
                )
            )
            mandatory.add(
                MediaConstraints.KeyValuePair(
                    "OfferToReceiveAudio", "${config.audioEnabled}"
                )
            )
        }
    }


    fun call() {
        with(peerConnection!!) {
            createOffer(object : CustomSdbObserver() {
                override fun onCreateSuccess(p0: SessionDescription?) {
                    Timber.e("onCreateSuccess")

                    // todo send offer
                    // dataRepo.sendData(p0)
                    // درخواست اتصال ارسال می شود
                    socket.emit(
                        OFFER,
                        gson.toJson(p0, SessionDescription::class.java),
                        config.bob.toJson()
                    )

                    setLocalSessionDescription(p0!!)
                }

                override fun onCreateFailure(p0: String?) {
                    super.onCreateFailure(p0)
                    Timber.e("offer onCreateFailure $p0")
                }

                override fun onSetFailure(p0: String?) {
                    super.onSetFailure(p0)
                    Timber.e("onSetFailure $p0")
                }
            }, createMediaConstraints())
        }
    }

    fun answer() {
        with(peerConnection!!) {
            createAnswer(object : CustomSdbObserver() {

                override fun onCreateSuccess(p0: SessionDescription?) {
                    super.onCreateSuccess(p0)
                    // todo send answer
                    // dataRepo.sendData(p0)
                    Timber.e("****************** answer onCreateSuccess ****************")

                    socket.emit(
                        ANSWER,
                        gson.toJson(p0, SessionDescription::class.java),
                        config.bob.toJson()
                    )

                    setLocalSessionDescription(p0!!)
                }

                override fun onCreateFailure(p0: String?) {
                    Timber.e("****************** answer onCreateFailure ****************")
                    Timber.e("answer error $p0")
                }

            }, createMediaConstraints())
        }
    }

    fun dispose() {

        socket.off(OFFER)
        socket.off(ANSWER)
        socket.off(CANDIDATE)


        remoteViewRenderer.release()
        localViewRenderer.release()
        //remoteViewRenderer.clearImage()
        //localViewRenderer.clearImage()


        /*if (videoFileRenderer != null) {
            videoFileRenderer.release();
            videoFileRenderer = null;
        }*/

        /*if (fullscreenRenderer != null) {
            fullscreenRenderer.release();
            fullscreenRenderer = null;
        }*/

        peerConnection?.close()

        /*if (dataChannel != null) {
            dataChannel.dispose()
            dataChannel = null
        }*/

        /*if (rtcEventLog != null) { // RtcEventLog should stop before the peer connection is disposed.
            rtcEventLog.stop()
            rtcEventLog = null
        }*/


        if (peerConnection != null) {
            peerConnection?.dispose()
        }


        if (localAudioSource != null) {
            localAudioSource.dispose()
        }


        try {
            videoCapturer.stopCapture()
        } catch (e: InterruptedException) {
            throw java.lang.RuntimeException(e)
        }

        videoCapturer.dispose()

        if (localVideoSource != null) {
            localVideoSource.dispose()
        }

        //surfaceTextureHelper.dispose()


        /*if (saveRecordedAudioToFile != null) {
            Log.d(TAG, "Closing audio file for recorded input audio.")
            saveRecordedAudioToFile.stop()
            saveRecordedAudioToFile = null
        }
        */


        Log.d(TAG, "Closing peer connection factory.")
        peerConnectionFactory.dispose()
        rootEglBase.release()


        PeerConnectionFactory.stopInternalTracingCapture()
        PeerConnectionFactory.shutdownInternalTracer()

    }

    /*fun onRemoteSessionReceived(sessionDescription: SessionDescription) {
        peerConnection?.setRemoteDescription(object : CustomSdbObserver() {}, sessionDescription)
    }*/

    fun addIceCandidate(iceCandidate: IceCandidate?) {
        peerConnection?.addIceCandidate(iceCandidate)
    }

    fun remoteIceCandidate(removedIce: Array<out IceCandidate>) {
        peerConnection?.removeIceCandidates(removedIce)
    }
}


/*
* todo :
* videoSource adapt format : aspectRatio, max and min width
*
* */




