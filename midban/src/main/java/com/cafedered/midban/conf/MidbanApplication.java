/**
 * ****************************************************************************
 * MidBan is an Android App which allows to interact with OpenERP
 * Copyright (C) 2014  CafedeRed
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * *****************************************************************************
 */
package com.cafedered.midban.conf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.cafedered.cafedroidlitedao.exceptions.BadConfigurationException;
import com.cafedered.midban.R;
import com.cafedered.midban.dao.ContextDAO;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.UserRepository;
import com.cafedered.midban.utils.GMailSender;
import com.cafedered.midban.utils.LoggerUtil;

public class MidbanApplication extends Application {

    public static MidbanApplication instance;
    private static Context context;
    private Map<String, Object> values = new HashMap<String, Object>();
    public final static String PREFIX = "HORECA_";
    public static Boolean debugEnabled;

    public Map<String, Object> getValues() {
        return values;
    }

    // uncaught exception handler variable
    private Thread.UncaughtExceptionHandler defaultUEH;

    // handler listener
    private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    String stackTrace = ex.getMessage();
                    StringWriter sw = new StringWriter();
                    ex.printStackTrace(new PrintWriter(sw));
                    stackTrace = sw.toString();
                    appendLog(stackTrace);
                    appendLog("\n");
                    final GMailSender sender = new GMailSender();
//                    try {
//                        android.os.Debug.dumpHprofData("/sdcard/dump-midban.hprof");
//                        sender.addAttachment("/sdcard/dump-midban.hprof");
//                    } catch (Exception e) {
//                        StringWriter writerE = new StringWriter();
//                        e.printStackTrace(new PrintWriter(writerE));
//                        stackTrace += "\n" + writerE.toString();
//                    }
                    if (!LoggerUtil.isDebugEnabled()) {
                        sender.set_subject("Error en APP MIDBAN");
                        sender.set_to(new String[]{"email@domain.com"});
                    } else {
                        sender.set_subject("Error en APP MIDBAN - debug local CafedeRed");
                        sender.set_to(new String[]{"email@domain.com"});
                    }
                    sender.set_body("Se ha producido un error en MIDBAN.\n Traza:\n " + stackTrace);
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                sender.send();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.execute();
                    // re-throw critical exception further to the os (important)
                    defaultUEH.uncaughtException(thread, ex);
                }
            };

    public MidbanApplication() {
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        // setup handler for uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);
    }

    public void appendLog(String text) {
        text = new Date().toString() + "\n" + text;
        File logFile = new File(Environment.getExternalStorageDirectory() + File.separator + "midban.log");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        instance = this;
        try {
            ContextDAO dao = ContextDAO.getInstance();
            debugEnabled = Boolean.valueOf(context.getResources().getString(
                    R.string.configuration_debug_enabled));
            dao.setDebugEnabled(debugEnabled);
        } catch (BadConfigurationException e) {
            e.printStackTrace();
            Toast.makeText(
                    this,
                    "ERROR de configuracion al intentar inicializar la base de datos",
                    Toast.LENGTH_LONG).show();
        }
    }

    public static void sendLogsByEmail() {
        Process mLogcatProc = null;
        BufferedReader reader = null;
        try {
            mLogcatProc = Runtime.getRuntime().exec(new String[]
                    {"logcat", "-d", "AndroidRuntime:E [com.cafedered.midban]:V *:S"});
            reader = new BufferedReader(new InputStreamReader
                    (mLogcatProc.getInputStream()));
            String line;
            final StringBuilder log = new StringBuilder();
            String separator = System.getProperty("line.separator");

            while ((line = reader.readLine()) != null) {
                log.append(line);
                log.append(separator);
            }

            // TODO enviar email si es preciso.
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public Boolean isDebugEnabled() {
        return debugEnabled;
    }

    public static Context getContext() {
        return context;
    }

    public static void putValueInContext(String name, Object value) {
        instance.getValues().put(MidbanApplication.PREFIX + name, value);
    }

    public static void removeValueInContext(String name) {
        instance.getValues().remove(MidbanApplication.PREFIX + name);
    }

    public static Object getValueFromContext(String name) {
        if (name.equals(ContextAttributes.LOGGED_USER) && instance.getValues().get(MidbanApplication.PREFIX + name) == null) {
            instance.getValues().put(MidbanApplication.PREFIX + name, UserRepository.getInstance().getLastUserLogged());
        }
        return instance.getValues().get(MidbanApplication.PREFIX + name);
    }

    public static User getLoggedUser() {
        return (User) getValueFromContext(ContextAttributes.LOGGED_USER);
    }
}