package ir.jin724.videochat.webRTC

import android.app.Application
import android.content.Context
import android.util.Log
import ir.jin724.videochat.data.userRepository.User
import org.webrtc.*
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class WebRTCClient(
    private val context: Application,
    private val webRTCEvent: WebRTCEvent,
    private val config: WebRTCConfig,
    private val localViewRenderer: SurfaceViewRenderer,
    private val remoteViewRenderer: SurfaceViewRenderer,
    private val bob: User
) {

    companion object {
        private val executor: ExecutorService = Executors.newSingleThreadExecutor()

        private const val LOCAL_VIDEO_TRACK_ID = "local_video_track"
        private const val LOCAL_AUDIO_TRACK_ID = "local_audio_track"
        private const val LOCAL_STREAM_ID = "local_stream_id"

        private const val AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation"
        private const val AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl"
        private const val AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter"
        private const val AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression"

        const val SDP_MID = "sdpMid"
        const val SDP_M_LINE_INDEX = "sdpMLineIndex"
        const val SDP = "sdp"

        private val TAG = this::class.java.simpleName
    }

    private val rootEglBase: EglBase = EglBase.create()
    private val observer: PeerConnection.Observer

    private lateinit var surfaceTextureHelper: SurfaceTextureHelper

    init {

        observer = object : PeerConnectionObserver() {
            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)
                Timber.tag(TAG).e("onIceCandidate : ${p0?.toString()}")

                if (isMirror()) {
                    remotePeerContext.addIceCandidate(p0)
                } else {
                    try {
                        webRTCEvent.sendICE(p0!!, config.bob)
                    } catch (e: Exception) {
                        Timber.e("onIceCandidate socket error %s", e.message)
                        e.printStackTrace()
                    }

                    addIceCandidate(p0)
                }
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
                webRTCEvent.sendICERemoved(p0, config.bob)
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
        //createVideoCapturer1(context)
    }

    private val localVideoSource by lazy {
        peerConnectionFactory.createVideoSource(config.isScreenCast, config.alignTimeStamps)
    }

    private val localAudioSource by lazy {
        val audioConstraints = MediaConstraints()

        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "false")
        )
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false")
        )
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "false")
        )
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "false")
        )

        peerConnectionFactory.createAudioSource(audioConstraints)
    }

    private val peerConnection by lazy {
        buildPeerConnection()
    }

    private val remotePeerContext by lazy {
        buildRemotePeerConnection()
    }

    // todo remove this later
    fun start() {
        initSurfaceView(localViewRenderer)
        initSurfaceView(remoteViewRenderer)
        initPeerConnectionFactory(context)
        startLocalVideoCapture(localViewRenderer)

        // todo what happen to this
        // initSocket()


        // put this out
        val audioManager = AppRTCAudioManager.create(context)
        audioManager.start(object : AppRTCAudioManager.AudioManagerEvents {
            override fun onAudioDeviceChanged(
                selectedAudioDevice: AppRTCAudioManager.AudioDevice?,
                availableAudioDevices: Set<AppRTCAudioManager.AudioDevice>?
            ) {

            }
        })
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

    private fun buildRemotePeerConnection(): PeerConnection {
        val config = PeerConnection.RTCConfiguration(arrayListOf())
        return peerConnectionFactory.createPeerConnection(
            config,
            object : PeerConnectionObserver() {
                override fun onIceCandidate(p0: IceCandidate?) {
                    super.onIceCandidate(p0)
                    Timber.tag(TAG).e("onIceCandidate : ${p0?.toString()}")
                    //signallingClient.send(p0)
                    // todo send ice candidate
                    //dataRepo.sendData(p0)
                    /*try {
                            socket.emit(
                                CANDIDATE,
                                gson.toJson(p0, IceCandidate::class.java),
                                config.bob.toJson()
                            )
                        } catch (e: Exception) {
                            Timber.e("onIceCandidate socket error %s", e.message)
                            e.printStackTrace()
                        }*/

                    addIceCandidate(p0)
                }

                override fun onAddStream(p0: MediaStream?) {
                    super.onAddStream(p0)
                    Timber.tag(TAG).e("onAddStream : ${p0?.toString()}")
                    //p0?.videoTracks?.get(0)?.addSink(remoteViewRenderer)

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
                        //remoteIceCandidate(it)
                        remotePeerContext.removeIceCandidates(p0)
                    }
                    // todo send remote ice candidate
                }
            })!!
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

    private fun createVideoCapturer1(context: Context): CameraVideoCapturer {
        //val fileVideoCapturer = FileVideoCapturer("");
        Timber.e("getVideoCapturer")
        return Camera1Enumerator().run {
            deviceNames.find {
                isBackFacing(it)
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

        //localVideoTrack.setEnabled(config.videoEnabled)
        //localAudioTrack.setEnabled(config.audioEnabled)

        localStream.addTrack(localVideoTrack)
        localStream.addTrack(localAudioTrack)

        peerConnection?.addStream(localStream)
    }

    private fun initSurfaceView(view: SurfaceViewRenderer) =
        view.run {
            setMirror(true)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
            setEnableHardwareScaler(true)
            //setZOrderMediaOverlay(true)
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

    private fun changeVideoFormat(width: Int, height: Int, fps: Int) {
        localVideoSource.adaptOutputFormat(width, height, fps)
    }

    private fun getRemoteVideoTrack(): MediaStreamTrack? {
        peerConnection?.transceivers?.forEach {
            val mediaTrackStream = it.receiver.track()
            if (mediaTrackStream is VideoTrack) {
                return mediaTrackStream
            }
        }
        return null
    }

    private fun isMirror(): Boolean {
        return bob.userId == 32
    }

    fun offer() {
        with(peerConnection!!) {
            createOffer(object : CustomSdbObserver() {
                override fun onCreateSuccess(p0: SessionDescription?) {
                    Timber.e("onCreateSuccess")

                    // todo send offer
                    // dataRepo.sendData(p0)
                    // درخواست اتصال ارسال می شود

                    setLocalSessionDescription(p0!!)

                    if (!isMirror()) {
                        webRTCEvent.sendOffer(p0, config.bob)
                    } else {
                        remotePeerContext.setRemoteDescription(CustomSdbObserver(), p0)
                        remotePeerContext.createAnswer(object : CustomSdbObserver() {
                            override
                            fun onCreateSuccess(p0: SessionDescription?) {
                                setRemoteDescription(
                                    CustomSdbObserver(),
                                    p0
                                )
                                remotePeerContext.setLocalDescription(
                                    CustomSdbObserver(),
                                    p0
                                )
                            }
                        }, createMediaConstraints())
                    }
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

    fun onOffer(
        sdp: SessionDescription,
        bob: User
    ) {
        setRemoteSessionDescription(sdp)
        answer()
    }


    private fun answer() {
        with(peerConnection!!) {
            createAnswer(object : CustomSdbObserver() {

                override fun onCreateSuccess(p0: SessionDescription?) {
                    super.onCreateSuccess(p0)

                    p0?.let {
                        // زمانی که answer فراخوانی شود یعنی کاربر تمایل به پاسخ داشته است پس اگر تمهداتی وجود دارد باید از قبل از فراخوانی این  متند انجام شود
                        webRTCEvent.sendAnswer(it, config.bob)
                        setLocalSessionDescription(it)
                    }
                }

                override fun onCreateFailure(p0: String?) {
                    // todo
                    Timber.e("****************** answer onCreateFailure ****************")
                    Timber.e("answer error $p0")
                }

            }, createMediaConstraints())
        }
    }

    fun onAnswer(
        sdp: SessionDescription,
        bob: User
    ) {
        setRemoteSessionDescription(sdp)
    }


    fun dispose() {
        executor.execute {
            disposeInternal()
        }
    }

    private fun disposeInternal() {

        /*socket.off(OFFER)
        socket.off(ANSWER)
        socket.off(CANDIDATE)*/

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
        remotePeerContext.close()

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
            remotePeerContext.dispose()
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

    fun onICERemoved(ice: Array<IceCandidate>) {
        peerConnection?.removeIceCandidates(ice)
    }

    fun remoteIceCandidate(removedIce: Array<out IceCandidate>) {
        peerConnection?.removeIceCandidates(removedIce)
    }


    private fun switchCameraInternal() {
        // todo check this later
        val isError = false
        if (videoCapturer is CameraVideoCapturer) {
            if (!config.videoEnabled || isError) {
                Timber.e(
                    TAG,
                    "Failed to switch camera. Video: " + config.videoEnabled + ". Error : " + isError
                )
                return  // No video is sent or only one camera is available or error happened.
            }
            Timber.e(TAG, "Switch camera")
            val cameraVideoCapturer = videoCapturer as CameraVideoCapturer
            cameraVideoCapturer.switchCamera(null)
        } else {
            Log.d(TAG, "Will not switch camera, video caputurer is not a camera")
        }
    }

    fun switchCamera() {
        executor.execute { switchCameraInternal() }
    }
}


/*
* todo :
* videoSource adapt format : aspectRatio, max and min width
*
* */




