package ir.jin724.videochat.webRTC

import android.content.Context
import ir.jin724.videochat.BuildConfig
import ir.jin724.videochat.R
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class CustomTrust(private val context: Context) {
    val client: OkHttpClient

    /**
     * Returns an input stream containing one or more certificate PEM files. This implementation just
     * embeds the PEM files in Java strings; most applications will instead read this from a resource
     * file that gets bundled with the application.
     */
    private fun trustedCertificatesInputStream(): InputStream {
        return context.resources.openRawResource(R.raw.cert)
    }

    /**
     * Returns a trust manager that trusts `certificates` and none other. HTTPS services whose
     * certificates have not been signed by these certificates will fail with a `SSLHandshakeException`.
     *
     *
     * This can be used to replace the host platform's built-in trusted certificates with a custom
     * set. This is useful in development where certificate authority-trusted certificates aren't
     * available. Or in production, to avoid reliance on third-party certificate authorities.
     *
     *
     * See also [CertificatePinner], which can limit trusted certificates while still using
     * the host platform's built-in trust store.
     *
     * <h3>Warning: Customizing Trusted Certificates is Dangerous!</h3>
     *
     *
     * Relying on your own trusted certificates limits your server team's ability to update their
     * TLS certificates. By installing a specific set of trusted certificates, you take on additional
     * operational complexity and limit your ability to migrate between certificate authorities. Do
     * not use custom trusted certificates in production without the blessing of your server's TLS
     * administrator.
     */
    @Throws(GeneralSecurityException::class)
    private fun trustManagerForCertificates(`in`: InputStream): X509TrustManager {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificates = certificateFactory.generateCertificates(`in`)

        require(!certificates.isEmpty()) {
            "expected non-empty set of trusted certificates"
        }

        // Put the certificates a key store.
        val password = "password".toCharArray() // Any password will work.
        val keyStore = newEmptyKeyStore(password)

        for ((index, certificate) in certificates.withIndex()) {
            val certificateAlias = (index).toString()
            keyStore.setCertificateEntry(certificateAlias, certificate)
        }
        // Use it to build an X509 trust manager.
        val keyManagerFactory =
            KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm()
            )

        keyManagerFactory.init(keyStore, password)
        val trustManagerFactory =
            TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm()
            )

        trustManagerFactory.init(keyStore)
        val trustManagers =
            trustManagerFactory.trustManagers
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
            ("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers))
        }
        return trustManagers[0] as X509TrustManager
    }

    @Throws(GeneralSecurityException::class)
    private fun newEmptyKeyStore(password: CharArray): KeyStore {
        return try {
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            val `in`: InputStream? =
                null // By convention, 'null' creates an empty key store.
            keyStore.load(`in`, password)
            keyStore
        } catch (e: IOException) {
            throw AssertionError(e)
        }
    }

    init {

        val trustManager: X509TrustManager
        val sslSocketFactory: SSLSocketFactory
        try {
            trustManager = trustManagerForCertificates(trustedCertificatesInputStream())
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
            sslSocketFactory = sslContext.socketFactory
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        }


        val loggingInterceptor =
            HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                if (BuildConfig.DEBUG) {
                    Timber.tag("OkHttp").e(message)
                }
            }).setLevel(HttpLoggingInterceptor.Level.BODY)


        client = OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustManager)
            .addNetworkInterceptor(loggingInterceptor)
            .connectTimeout(45, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .protocols(listOf(Protocol.HTTP_1_1))
            .hostnameVerifier { hostname, session ->
                hostname.contains("jinchat.ir")
            }
            .build()
    }
}