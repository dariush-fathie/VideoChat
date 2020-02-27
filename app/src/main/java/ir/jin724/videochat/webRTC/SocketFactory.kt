package ir.jin724.videochat.webRTC

import android.content.Context
import io.socket.client.IO
import io.socket.client.Socket
import ir.jin724.videochat.R
import ir.jin724.videochat.util.Constants
import okhttp3.OkHttpClient
import timber.log.Timber
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object SocketFactory {
    fun getSecureSocket(context: Context,trust: CustomTrust): Socket? {
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

            socket.on(
                Socket.EVENT_CONNECT_ERROR
            ) { args: Array<Any> ->
                for (o in args) {
                    Timber.i(o.toString())
                }
            }.on(
                Socket.EVENT_CONNECT_TIMEOUT
            ) { args: Array<Any?>? -> Timber.i(Socket.EVENT_CONNECT_TIMEOUT) }.on(
                Socket.EVENT_CONNECT
            ) { args: Array<Any?>? -> Timber.i(Socket.EVENT_CONNECT) }.on(
                "secure_data"
            ) { args: Array<Any> -> Timber.i(args[0].toString()) }
            socket.connect()

            return socket
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /*fun createSSLContext(trustManagerFactory: TrustManagerFactory): SSLContext {
        // Create an SSLContext that uses the TrustManager.
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagerFactory.trustManagers, null)
        Timber.i("sslContext created")
        return sslContext
    }

    fun createTrustManager(context: Context): TrustManagerFactory {
        val certificateFactory =
            CertificateFactory.getInstance("X.509")
        val certificate =
            certificateFactory.generateCertificate(
                context.resources.openRawResource(R.raw.cert)
            ) // from file server.crt
        // Create a KeyStore containing the trusted CAs.

        val keyStore =
            KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", certificate)
        // Create a TrustManager that trusts the CAs in KeyStore.
        val trustManagerFactory =
            TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm()
            )
        trustManagerFactory.init(keyStore)


        return trustManagerFactory

    }*/

}