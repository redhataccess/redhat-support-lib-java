package com.redhat.gss.redhat_support_lib.web;

import com.redhat.gss.redhat_support_lib.helpers.ConfigHelper;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


public class ConfiglessCustomHttpEngine extends ApacheHttpClient4Engine {


    public ConfiglessCustomHttpEngine() {
        super();
        HttpClientBuilder client = HttpClientBuilder.create();

        try {
            client.setSslcontext(createGullibleSslContext());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        this.httpClient = client.build();
    }

    public static TrustManager[] gullibleManagers = new TrustManager[]{new X509TrustManager() {

        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }};

    public static SSLContext createGullibleSslContext()
            throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext ctx = SSLContext.getInstance("SSL");
        ctx.init(null, gullibleManagers, new SecureRandom());
        return ctx;
    }
}

