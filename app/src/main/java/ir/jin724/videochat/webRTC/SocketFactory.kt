package ir.jin724.videochat.webRTC

import android.content.Context
import io.socket.client.IO
import io.socket.client.Socket
import ir.jin724.videochat.util.Constants
import timber.log.Timber

object SocketFactory {

    private const val TAG = "SocketFactory"

    fun getSecureSocket(trust: CustomTrust): Socket? {
        val uri = Constants.BASE_URL
        try { // Load CAs from an InputStream.

            // default settings for all sockets
            IO.setDefaultOkHttpWebSocketFactory(trust.client)
            IO.setDefaultOkHttpCallFactory(trust.client)

            // set as an option
            val opts = IO.Options()
            opts.callFactory = trust.client
            opts.webSocketFactory = trust.client
            opts.secure = true
            val socket = IO.socket(uri, opts)

            socket.on(Socket.EVENT_CONNECT_ERROR) {
                Timber.tag(TAG).e(Socket.EVENT_CONNECT_ERROR)
                for (o in it) {
                    Timber.i(o.toString())
                }
            }.on(Socket.EVENT_CONNECT_TIMEOUT) {
                Timber.tag(TAG).i(Socket.EVENT_CONNECT_TIMEOUT)
            }.on(Socket.EVENT_CONNECT) {
                Timber.tag(TAG).i(Socket.EVENT_CONNECT)
            }.on(Socket.EVENT_CONNECTING) {
                Timber.tag(TAG).e(Socket.EVENT_CONNECTING)
            }.on(Socket.EVENT_DISCONNECT) {
                Timber.tag(TAG).e(Socket.EVENT_DISCONNECT)
            }.on(Socket.EVENT_RECONNECT) {
                Timber.tag(TAG).e(Socket.EVENT_RECONNECT)
            }.on(Socket.EVENT_RECONNECTING) {
                Timber.tag(TAG).e(Socket.EVENT_RECONNECTING)
            }.on(Socket.EVENT_RECONNECT_ERROR) {
                Timber.tag(TAG).e(Socket.EVENT_RECONNECT_ERROR)
            }.on(Socket.EVENT_MESSAGE) {
                Timber.tag(TAG).e(Socket.EVENT_MESSAGE)
            }.on(Socket.EVENT_ERROR) {
                Timber.tag(TAG).e(Socket.EVENT_ERROR)
            }

            socket.connect()
            return socket
        } catch (e: Exception) {
            e.printStackTrace()
            // todo check here later
        }

        return null
    }

}