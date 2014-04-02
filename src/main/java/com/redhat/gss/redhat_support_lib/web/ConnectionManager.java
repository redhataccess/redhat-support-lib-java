package com.redhat.gss.redhat_support_lib.web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPHTTPClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.log4j.Logger;

import com.redhat.gss.redhat_support_lib.api.API;
import com.redhat.gss.redhat_support_lib.errors.FTPException;
import com.redhat.gss.redhat_support_lib.helpers.ConfigHelper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;
import com.sun.jersey.multipart.impl.MultiPartWriter;

public class ConnectionManager {

	private final static Logger LOGGER = Logger.getLogger(ConnectionManager.class.getName());
	ConfigHelper config = null;

	public ConnectionManager(ConfigHelper config) {
		this.config = config;
	}

	public Client getConnection() throws MalformedURLException {
		ClientConfig clientConfig = new DefaultApacheHttpClient4Config();
		ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager();
        clientConfig.getProperties().put( DefaultApacheHttpClient4Config.PROPERTY_CONNECTION_MANAGER,
                                          connectionManager );

		if (config.getProxyUrl() != null) {
			clientConfig.getProperties().put(
					DefaultApacheHttpClient4Config.PROPERTY_PROXY_URI,
					config.getProxyUrl() + ":" + config.getProxyPort());
			if (config.getProxyUser() != null
					&& config.getProxyPassword() != null) {
				clientConfig.getProperties().put(
						ApacheHttpClient4Config.PROPERTY_PROXY_USERNAME,
						config.getProxyUser());
				clientConfig.getProperties().put(
						ApacheHttpClient4Config.PROPERTY_PROXY_PASSWORD,
						config.getProxyPassword());
			}
		}
		clientConfig.getClasses().add(MultiPartWriter.class);

		if (config.isDevel()) {
			try {
				connectionManager.getSchemeRegistry().register(
						new Scheme("https", 443, new SSLSocketFactory(createGullibleSslContext(),
								SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)));
			} catch (KeyManagementException e) {
				LOGGER.warn(e);
			} catch (NoSuchAlgorithmException e) {
				LOGGER.warn(e);
			}

		}

		Client client = ApacheHttpClient4.create(clientConfig);
		client.setConnectTimeout(config.getTimeout());
		client.setReadTimeout(config.getTimeout());
		client.addFilter(new HTTPBasicAuthFilter(config.getUsername(), config
				.getPassword()));
		client.addFilter(new ClientFilter() {
			@Override
			public ClientResponse handle(ClientRequest request)
					throws ClientHandlerException {
				request.getHeaders().add(HttpHeaders.USER_AGENT,
						config.getUserAgent());
				return getNext().handle(request);
			}
		});
		return client;

	}

	public ConfigHelper getConfig() {
		return config;
	}

	public static TrustManager[] gullibleManagers = new TrustManager[] { new X509TrustManager() {

		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			// TODO Auto-generated method stub
			
		}

		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			// TODO Auto-generated method stub
			
		}

		public X509Certificate[] getAcceptedIssuers() {
			// TODO Auto-generated method stub
			return null;
		}
	} };

	public static HostnameVerifier gullibleVerifier = new HostnameVerifier() {

		public boolean verify(String hostname, SSLSession session) {
			// TODO Auto-generated method stub
			return true;
		}

	};

	public static SSLContext createGullibleSslContext()
			throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext ctx = SSLContext.getInstance("SSL");
		ctx.init(null, gullibleManagers, new SecureRandom());
		return ctx;
	}

	public FTPClient getFTP() throws IOException, FTPException {
		FTPClient ftp = null;
		if (config.getProxyUrl() == null) {
			ftp = new FTPClient();
		} else {
			ftp = new FTPHTTPClient(config.getProxyUrl().getHost(),
					config.getProxyPort(), config.getProxyUser(),
					config.getProxyPassword());
		}
		ftp.connect(config.getFtpHost(), config.getFtpPort());
		if (!ftp.login(config.getFtpUsername(), config.getFtpPassword())) {
			throw new FTPException("Error during FTP login");
		}
		return ftp;
	}
}
