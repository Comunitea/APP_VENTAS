package com.cafedered.midban.async;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Company;
import com.cafedered.midban.entities.Order;
import com.cafedered.midban.entities.OrderLine;
import com.cafedered.midban.service.repositories.OrderLineRepository;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.service.repositories.SynchronizationRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.SessionFactory;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.debortoliwines.openerp.api.OpenERPCommand;

import org.apache.xmlrpc.XmlRpcException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * Created by nacho on 9/12/15.
 */
public class OrderSynchronizationService extends Service {

    private Timer timer;
    private Timer syncOrders;
    public static boolean anotherThreadSynchronizing = false;

    private TimerTask syncTask = new TimerTask() {
        @Override
        public void run() {
            try {
                OrderRepository.getInstance().getRemoteObjects(new Order(), MidbanApplication.getLoggedUser().getLogin(),
                        MidbanApplication.getLoggedUser().getPasswd(), false);
                OrderLineRepository.getInstance().getRemoteObjects(new OrderLine(), MidbanApplication.getLoggedUser().getLogin(),
                        MidbanApplication.getLoggedUser().getPasswd(), false);
            } catch (ConfigurationException e) {
                if (LoggerUtil.isDebugEnabled()) {
                    e.printStackTrace();
                    appendLog(new Date().toString() + e.getMessage());
                }
            }
        }
    };

    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {

            if (LoggerUtil.isDebugEnabled()) {
                System.out.println("Fase previa al trabajo de sincronización de pedidos...");
                appendLog(new Date().toString() + "Fase previa al trabajo de sincronización de pedidos...");
            }
            String maxDateOld = SynchronizationRepository.getInstance().getMaxDateFor(Company.class);


            if (LoggerUtil.isDebugEnabled()) {
                System.out.println("Timer for orders doing work");
                appendLog(new Date().toString() + "Servicio en funcionamiento...");
            }
            // el maxDateOld != null es una forma de comprobar que al menos se haya sincronizado por primera vez
            // pues no tiene sentido hacer nada si no hubo ni siquiera una sincronización
            // también miro que la app esté en primer plano, sino no sincronizo
            if ((!anotherThreadSynchronizing) && (!"0001-01-01 00:00:00".equals(maxDateOld)) && (MidbanApplication.appInForeground)) {
                anotherThreadSynchronizing = true;
                if (LoggerUtil.isDebugEnabled()) {
                    System.out.println("Iniciando trabajo de sincronización de pedidos...");
                    appendLog(new Date().toString() + "Iniciando trabajo de sincronización de pedidos...");
                }
                try {
                    List<HashMap<String, Object>> pending = OrderRepository.getUnsynchronizedOrders();
                    final int size = pending.size();
                    if (LoggerUtil.isDebugEnabled()) {
                        System.out.println("Sincronizando " + size + " pedidos...");
                        appendLog(new Date().toString() + "Sincronizando " + size + " pedidos...");
                        try {
                            System.out.println(new JSONArray(pending));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Iterator<HashMap<String, Object>> itOrders = pending.iterator();
                    while (itOrders.hasNext()) {
                        HashMap<String, Object> anOrder = itOrders.next();
                        if (anOrder.containsKey("id")) {
                            //editando
                            OpenERPCommand commandConfirm = new OpenERPCommand(SessionFactory
                                    .getInstance(MidbanApplication.getLoggedUser().getLogin(),
                                            MidbanApplication.getLoggedUser().getPasswd()).getSession());
                            HashMap<String, Object> newMap = new HashMap<String, Object>();
                            newMap.putAll(anOrder);
                            newMap.remove("id");
                            try {
                                if (LoggerUtil.isDebugEnabled())
                                    System.out.println(new JSONObject(newMap));
                                appendLog("Reenviando easy_modification pendiente del pedido: " + new JSONObject(newMap));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            commandConfirm.callObjectFunction("sale.order",
                                    "easy_modification", new Object[]{(Integer) anOrder.get("id"), anOrder});
                            doRemoveOfOldLines(((Integer) anOrder.get("id")).longValue());
                        } else {
                            //creando
                            OpenERPCommand commandConfirm = new OpenERPCommand(SessionFactory
                                    .getInstance(MidbanApplication.getLoggedUser().getLogin(),
                                            MidbanApplication.getLoggedUser().getPasswd()).getSession());
                            try {
                                if (LoggerUtil.isDebugEnabled())
                                    System.out.println(new JSONObject(anOrder));
                                appendLog("Reenviando create_and_confirm pendiente del pedido: " + new JSONObject(anOrder));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                SessionFactory
                                        .getInstance(MidbanApplication.getLoggedUser().getLogin(),
                                                MidbanApplication.getLoggedUser().getPasswd()).getSession().executeCommand("sale.order", "create_and_confirm", new Object[]{anOrder});

                            // dejo lo de arriba para probar lo del context
                            // commandConfirm.callObjectFunction("sale.order", "create_and_confirm", new Object[]{anOrder});
                            // si ha ido bien la elimino
                                itOrders.remove();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    OrderRepository.getInstance().getRemoteObjects(new Order(),
                            MidbanApplication.getLoggedUser().getLogin(),
                            MidbanApplication.getLoggedUser().getPasswd(), false);

                    if (size > 0) {
                        try {
                            syncOrders.schedule(syncTask, 120000L);
                        } catch (Exception e) {
                            //already scheduled
                        }
                    }
                } catch (ConfigurationException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                    try {
                        appendLog("ERROR en el reenvío, cancelando tarea de pendientes: " + e.getMessage() + ".");
                        appendLog("Tamaño de la lista de pendientes: " + OrderRepository.getUnsynchronizedOrders().size());
                    } catch (Exception ex) {
                        //do nothing
                    }
                } catch (XmlRpcException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                    try {
                        appendLog("ERROR en el reenvío, cancelando tarea de pendientes: " + e.getMessage() + ".");
                        appendLog("Tamaño de la lista de pendientes: " + OrderRepository.getUnsynchronizedOrders().size());
                    } catch (Exception ex) {
                        //do nothing
                    }
                } catch (Exception e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                    try {
                        appendLog("ERROR en el reenvío, cancelando tarea de pendientes: " + e.getMessage() + ".");
                        appendLog("Tamaño de la lista de pendientes: " + OrderRepository.getUnsynchronizedOrders().size());
                    } catch (Exception ex) {
                        //do nothing
                    }
                }
                anotherThreadSynchronizing = false;
            } else {
                if (LoggerUtil.isDebugEnabled()) {
                    System.out.println("Timer for orders not doing work because there are another thread executing...");
                    appendLog(new Date().toString() + "No se inicia el trabajo de sincronización de pedidos porque ya hay otro hilo haciéndolo...");
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("OrderService creating");
        timer = new Timer("OrderService");
        timer.schedule(updateTask, 1000L, 30 * 1000L);
        syncOrders = new Timer("OrderSync");
//        syncOrders.schedule(syncTask, 10000L, 120 * 1000L);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("OrderService destroying");

        timer.cancel();
        timer = null;
    }

    private void doRemoveOfOldLines(Long orderId) {
        for (OrderLine oldLine : OrderLineRepository.getInstance().getLinesByOrderId(orderId)) {
            try {
                OrderLineRepository.getInstance().delete(oldLine.getId());
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
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
}
