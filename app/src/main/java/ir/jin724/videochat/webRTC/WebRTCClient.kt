package ir.jin724.videochat.webRTC

import android.app.Application
import android.content.Context
import android.media.AudioManager
import ir.jin724.videochat.VideoChatApp
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import timber.log.Timber

class WebRTCClient(
    private val context: Application,
    private val localViewRenderer: SurfaceViewRenderer,
    private val remoteViewRenderer: SurfaceViewRenderer
) {

    companion object {
        private const val LOCAL_TRACK_ID = "local_track"
        private const val LOCAL_STREAM_ID = "local_track"

        private const val OFFER = "offer"
        private const val ANSWER = "answer"
        private const val CANDIDATE = "candidate"
        private const val I_AM_ONLINE = "iAmOnline"

        private const val SDP_MID = "sdpMid"
        private const val SDP_M_LINE_INDEX = "sdpMLineIndex"
        private const val SDP = "sdp"

        private val TAG = this::class.java.simpleName
    }

    private val socket = (context as VideoChatApp).socket
    private val rootEglBase: EglBase = EglBase.create()
    private val observer: PeerConnection.Observer


    init {
        val mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mAudioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        mAudioManager.isSpeakerphoneOn = true

        observer = object : PeerConnectionObserver() {
            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)
                Timber.tag(TAG).e("onIceCandidate : ${p0?.toString()}")
                //signallingClient.send(p0)
                // todo send ice candidate
                //dataRepo.sendData(p0)
                socket.emit(CANDIDATE, p0)
                addIceCandidate(p0)
            }

            override fun onAddStream(p0: MediaStream?) {
                super.onAddStream(p0)
                Timber.tag(TAG).e("onAddStream : ${p0?.toString()}")
                p0?.videoTracks?.get(0)?.addSink(remoteViewRenderer)
            }

            override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
                super.onIceCandidatesRemoved(p0)
                p0?.let { remoteIceCandidate(it) }
                // todo send remote ice candidate
            }

        }
    }



    private fun initSocket() {
        socket.on(OFFER) { args ->
            // یک درخواست از کاربری دیگر امده است
            //  باید session کاربر ذخیره شود و پاسخ او را بدهیم
            // البته ممکن است نخواهیم پاسخ دهیم که باید کنترل شدو
            try {
                val obj = args[0] as JSONObject

                // remote use session
                val sdp = SessionDescription(
                    SessionDescription.Type.OFFER,
                    obj.getString(SDP)
                )
                // باید بررسی شود آیا کاربر می تواند در این لحظه پاسخگو باشد یا نه
                setRemoteSessionDescription(sdp)
                call()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }.on(ANSWER) { args ->
            // درخواست ما پاسخ داده شده است
            // پس طرف مقابل session خود را برای ما ارسال می کند تا اتصال برقرار شود
            // البته ممکن است پاسخ ندهد که باید کنترل شود
            try {
                val obj = args[0] as JSONObject

                // remote use session
                val sdp = SessionDescription(
                    SessionDescription.Type.ANSWER,
                    obj.getString(SDP)
                )
                setRemoteSessionDescription(sdp)
                answer()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }.on(CANDIDATE) { args ->
            try {
                val obj = args[0] as JSONObject
                addIceCandidate(
                    IceCandidate(
                        obj.getString(SDP_MID),
                        obj.getInt(SDP_M_LINE_INDEX),
                        obj.getString(SDP)
                    )
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        socket.connect()
        socket.emit(I_AM_ONLINE)
    }

    private val iceServer = listOf(
        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
            .createIceServer(),
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
        peerConnectionFactory.createVideoSource(false)
    }

    private val localAudioSource by lazy {
        val audioConstraints = MediaConstraints()
        peerConnectionFactory.createAudioSource(audioConstraints)
    }

    private val peerConnection by lazy { buildPeerConnection() }


    fun start() {
        initSurfaceView(localViewRenderer)
        initSurfaceView(remoteViewRenderer)
        initPeerConnectionFactory(context)

        startLocalVideoCapture(localViewRenderer)

        /*sdbObserver = object : CustomSdbObserver() {

        }*/

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
        /*with(rtcConfiguration) {
            *//*bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
            enableDtlsSrtp = true
            enableRtpDataChannel = true*//*
        }*/
        return peerConnectionFactory.createPeerConnection(
            rtcConfiguration,
            observer
        )
    }

    private fun createVideoCapturer(context: Context): CameraVideoCapturer {
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
        val surfaceTextureHelper =
            SurfaceTextureHelper.create(Thread.currentThread().name, rootEglBase.eglBaseContext)

        Timber.e("videoCapturer = $videoCapturer")

        videoCapturer.initialize(
            surfaceTextureHelper,
            localSurfaceRenderer.context,
            localVideoSource.capturerObserver
        )

        videoCapturer.startCapture(1920, 1080, 60)

        val localVideoTrack =
            peerConnectionFactory.createVideoTrack(LOCAL_TRACK_ID, localVideoSource)
        localVideoTrack.addSink(localSurfaceRenderer)
        val localStream = peerConnectionFactory.createLocalMediaStream(LOCAL_STREAM_ID)

        //create an AudioSource instance
        val localAudioTrack = peerConnectionFactory.createAudioTrack("101", localAudioSource)

        /*WebRtcAudioManager.setStereoOutput(true)
        WebRtcAudioManager.setStereoOutput(true)*/

        localStream.addTrack(localVideoTrack)
        localStream.addTrack(localAudioTrack)
        peerConnection?.addStream(localStream)
    }

    private fun initSurfaceView(view: SurfaceViewRenderer) = view.run {
        setMirror(true)
        setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
        setEnableHardwareScaler(true)
        init(rootEglBase.eglBaseContext, null)
    }

    private fun setLocalSessionDescription(sessionDescription: SessionDescription) {
        peerConnection?.setLocalDescription(object : CustomSdbObserver() {}, sessionDescription)
    }

    private fun setRemoteSessionDescription(sessionDescription: SessionDescription) {
        peerConnection?.setRemoteDescription(object : CustomSdbObserver() {}, sessionDescription)
    }


    fun call() {
        with(peerConnection!!) {
            val constraints = MediaConstraints().apply {
                mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            }

            createOffer(object : CustomSdbObserver() {
                override fun onCreateSuccess(p0: SessionDescription?) {
                    super.onCreateSuccess(p0)
                    // todo send offer
                    // dataRepo.sendData(p0)
                    // درخواست اتصال ارسال می شود
                    socket.emit(OFFER, p0)
                    setLocalSessionDescription(p0!!)
                }
            }, constraints)
        }
    }

    fun answer() {
        with(peerConnection!!) {
            val constraints = MediaConstraints().apply {
                mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            }

            createAnswer(object : CustomSdbObserver() {
                override fun onCreateSuccess(p0: SessionDescription?) {
                    super.onCreateSuccess(p0)
                    // todo send answer
                    // dataRepo.sendData(p0)
                    socket.emit(ANSWER, p0)
                    setLocalSessionDescription(p0!!)
                }
            }, constraints)
        }
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




