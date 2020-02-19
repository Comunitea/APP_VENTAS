/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.xmlrpc.client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.XmlRpcStreamRequestConfig;
import org.apache.xmlrpc.util.HttpUtil;
import org.xml.sax.SAXException;

import android.annotation.SuppressLint;
import android.util.Log;

import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.utils.SessionFactory;


/** Default implementation of an HTTP transport, based on the
 * {@link java.net.HttpURLConnection} class.
 */
public class XmlRpcSunHttpTransport extends XmlRpcHttpTransport {
	private static final String userAgent = USER_AGENT + " (Sun HTTP Transport)";
	private URLConnection conn;

	/** Creates a new instance.
	 * @param pClient The client controlling this instance.
	 */
	public XmlRpcSunHttpTransport(XmlRpcClient pClient) {
		super(pClient, userAgent);
	}

    protected URLConnection newURLConnection(URL pURL) throws IOException {
        return pURL.openConnection();
    }

    /**
     * For use by subclasses.
     */
    protected URLConnection getURLConnection() {
        return conn;
    }

    @SuppressLint("SdCardPath")
    public HttpURLConnection setUpHttpsConnection(URL url) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream caInput = null;
            if (url.toString().contains("apolo"))
                caInput = new BufferedInputStream(MidbanApplication
                        .getContext().getAssets()
                        .open("apolo.midban.com.server.crt"));
            else if (url.toString().contains("dev8"))
                caInput = new BufferedInputStream(MidbanApplication
                        .getContext().getAssets()
                        .open("dev8.midban.com.server.crt"));
            else if (url.toString().contains("tdx"))
                caInput = new BufferedInputStream(MidbanApplication
                        .getContext().getAssets()
                        .open("tdx_midban_com.crt"));
            else if (url.toString().contains("albertopolo"))
                caInput = new BufferedInputStream(MidbanApplication
                        .getContext().getAssets()
                        .open("gestion_albertopolo_com.crt"));
            if (caInput != null) {
                Certificate ca = cf.generateCertificate(caInput);
                // System.out.println("ca=" + ((X509Certificate)
                // ca).getSubjectDN());

                // Create a KeyStore containing our trusted CAs
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);

                // Create a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory
                        .getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                // Create an SSLContext that uses our TrustManager
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);
                // Tell the URLConnection to use a SocketFactory from our SSLContext
                // TODO make it work with both protocols (self-signed certificates
                // stuff)
                if (url.toString().startsWith("http:"))
                    url = new URL(url.toString().replace("http", "https"));
                HttpsURLConnection urlConnection = (HttpsURLConnection) url
                        .openConnection();
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                return urlConnection;
            } else {

                if (url.toString().startsWith("http:"))
                    url = new URL(url.toString().replace("http", "https"));
                HttpURLConnection urlConnection = (HttpURLConnection) url
                        .openConnection();
                return urlConnection;
            }
        } catch (Exception ex) {
            Log.e(SessionFactory.class.getName(),
                    "Failed to establish SSL connection to server: "
                            + ex.toString());
            return null;
        }
    }

    public Object sendRequest(XmlRpcRequest pRequest) throws XmlRpcException {
		XmlRpcHttpClientConfig config = (XmlRpcHttpClientConfig) pRequest.getConfig();
		try {
            URLConnection c = conn = null;
            if (config.getServerURL().getProtocol().equals("https")){
                c = conn = setUpHttpsConnection(config
                        .getServerURL());
            }
            else{
                c = conn = newURLConnection(config.getServerURL());
            }
			c.setUseCaches(false);
			c.setDoInput(true);
			c.setDoOutput(true);
        } catch (Exception e) {
			throw new XmlRpcException("Failed to create URLConnection: " + e.getMessage(), e);
		}
		return super.sendRequest(pRequest);
	}

	protected void setRequestHeader(String pHeader, String pValue) {
	    getURLConnection().setRequestProperty(pHeader, pValue);
	}

	protected void close() throws XmlRpcClientException {
	    final URLConnection c = getURLConnection();
		if (c instanceof HttpURLConnection) {
			((HttpURLConnection) c).disconnect();
		}
	}

	protected boolean isResponseGzipCompressed(XmlRpcStreamRequestConfig pConfig) {
		return HttpUtil.isUsingGzipEncoding(getURLConnection().getHeaderField("Content-Encoding"));
	}

	protected InputStream getInputStream() throws XmlRpcException {
		try {
		    URLConnection connection = getURLConnection();
		    if ( connection instanceof HttpURLConnection ) {
		        HttpURLConnection httpConnection = (HttpURLConnection) connection;
		        int responseCode = httpConnection.getResponseCode();
		        if (responseCode < 200  ||  responseCode > 299) {
		            throw new XmlRpcHttpTransportException(responseCode, httpConnection.getResponseMessage());
		        }
		    }
			return connection.getInputStream();
		} catch (IOException e) {
			throw new XmlRpcException("Failed to create input stream: " + e.getMessage(), e);
		}
	}

	protected void writeRequest(ReqWriter pWriter) throws IOException, XmlRpcException, SAXException {
        pWriter.write(getURLConnection().getOutputStream());
	}
}