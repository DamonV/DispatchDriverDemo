package com.driver.reliantdispatch.data.secondary

import android.content.Context
import android.util.Log
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.R
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory


fun OkHttpClient.Builder.trustReliantCert(context: Context): OkHttpClient.Builder {

    val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
    val caInput = context.resources.openRawResource(R.raw.reliant)
    val ca: X509Certificate = caInput.use {
        cf.generateCertificate(it) as X509Certificate
    }
    Log.d(LOG_TAG, "ca=" + ca.subjectDN)

    val keyStoreType = KeyStore.getDefaultType()
    val keyStore = KeyStore.getInstance(keyStoreType).apply {
        load(null, null)
        setCertificateEntry("ca", ca)
    }
    val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
    val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
        init(keyStore)
    }

    val sc: SSLContext = SSLContext.getInstance("TLS").apply {
        init(null, tmf.trustManagers, null)
    }
    this.sslSocketFactory(sc.socketFactory)

    return this
}
