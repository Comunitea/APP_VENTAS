/*******************************************************************************
 * MidBan is an Android App which allows to interact with OpenERP
 *     Copyright (C) 2014  CafedeRed
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.cafedered.midban.utils;


import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import android.annotation.SuppressLint;
import android.util.Log;

import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Configuration;
import com.cafedered.midban.service.repositories.ConfigurationRepository;
import com.debortoliwines.openerp.api.OpenERPXmlRpcProxy;
import com.debortoliwines.openerp.api.Session;

public class SessionFactory {
    private String loggedUsername;
    private String loggedPassword;
    private Session session;
    private static SessionFactory instance;
    private static boolean connecting = false;

    private SessionFactory(String loggedUsername, String loggedPassword)
            throws Exception {

        Configuration configuration = ConfigurationRepository.getInstance()
                .getConfiguration();

        session = new Session(configuration.getProtocolRCP(),
                configuration.getUrlOpenErp(), configuration
                .getPortOpenErp().intValue(), configuration.getDbOpenErp(),
                loggedUsername, loggedPassword);

        this.setLoggedPassword(loggedPassword);
        this.setLoggedUsername(loggedUsername);
        try {
            session.startSession();
        } catch (Exception e) {
            throw e;
        }
    }

    public Integer getUserId(String username, String password) throws Exception {
        Configuration configuration = ConfigurationRepository.getInstance()
                .getConfiguration();
        OpenERPXmlRpcProxy.RPCProtocol protocol = configuration.getProtocolRCP();
//        if (configuration.getProtocolRCP().equals("http"))
//            protocol = OpenERPXmlRpcProxy.RPCProtocol.RPC_HTTP;
//        else
//            protocol = OpenERPXmlRpcProxy.RPCProtocol.RPC_HTTPS;

        OpenERPXmlRpcProxy commonClient1 = new OpenERPXmlRpcProxy(protocol, configuration.getUrlOpenErp(),
                configuration.getPortOpenErp().intValue(), OpenERPXmlRpcProxy.RPCServices.RPC_COMMON);
        startConnecting();
        Object id = null;

        try {
            id = commonClient1.execute("login", new Object[]{configuration.getDbOpenErp(), username, password});
        } catch (ClassCastException var8) {
            throw new Exception("Database " + configuration.getDbOpenErp() + " does not exist");
        } finally {
            connecting = false;
        }

        if(id instanceof Integer) {
            return (Integer)id;
        } else {
            throw new Exception("Incorrect username and/or password.  Login Failed.");
        }
    }

    private static synchronized void startConnecting() {
        while(connecting) {
            try {
                Thread.sleep(100L);
            } catch (Exception var1) {
                ;
            }
        }

        connecting = true;
    }

    @SuppressLint("SdCardPath")
    public HttpsURLConnection setUpHttpsConnection(String urlString) {
        try {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            // My CRT file that I put in the assets folder
            // I got this file by following these steps:
            // * Go to https://littlesvr.ca using Firefox
            // * Click the padlock/More/Security/View Certificate/Details/Export
            // * Saved the file as littlesvr.crt (type X.509 Certificate (PEM))
            // The MainActivity.context is declared as:
            // public static Context context;
            // And initialized in MainActivity.onCreate() as:
            // MainActivity.context = getApplicationContext();
            InputStream caInput = new BufferedInputStream(MidbanApplication
                    .getContext().getAssets().open("gestion_albertopolo_com.crt"));
            Certificate ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());

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
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url
                    .openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());

            return urlConnection;
        } catch (Exception ex) {
            Log.e(SessionFactory.class.getName(),
                    "Failed to establish SSL connection to server: "
                            + ex.toString());
            return null;
        }
    }

    public static void invalidateFactory() {
        instance = null;
    }

    public void invalidateSession() {
        session = null;
    }

    public static SessionFactory getInstance(String loggedUsername,
            String loggedPassword) throws Exception {
        if (instance == null)
            instance = new SessionFactory(loggedUsername, loggedPassword);
        return instance;
    }

    public String getLoggedUsername() {
        return loggedUsername;
    }

    public void setLoggedUsername(String loggedUsername) {
        this.loggedUsername = loggedUsername;
    }

    public String getLoggedPassword() {
        return loggedPassword;
    }

    public void setLoggedPassword(String loggedPassword) {
        this.loggedPassword = loggedPassword;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

}
