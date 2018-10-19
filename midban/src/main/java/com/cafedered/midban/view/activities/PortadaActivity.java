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
package com.cafedered.midban.view.activities;

import java.util.logging.Level;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Activity;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.async.OrderSynchronizationService;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.RouteDetail;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.service.repositories.RouteDetailRepository;
import com.cafedered.midban.service.repositories.UserRepository;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.base.BaseSupportActivity;
import com.cafedered.midban.view.dialogs.SelectPartnerDialog;

@Activity(layout = R.layout.activity_portada,
        title = R.string.activity_portada_title,
        rootView = R.id.activity_portada_container)
public class PortadaActivity extends BaseSupportActivity implements OnClickListener {

    @Wire(view = R.id.activity_portada_clientes_tv)
    TextView clientesTv;
    @Wire(view = R.id.activity_portada_pedidos_tv)
    TextView pedidosTv;
    @Wire(view = R.id.activity_portada_catalogo_tv)
    TextView catalogoTv;
    @Wire(view = R.id.activity_portada_devoluciones_tv)
    TextView devolucionesTv;
    @Wire(view = R.id.activity_portada_cobros_tv)
    TextView cobrosTv;
    @Wire(view = R.id.activity_portada_agenda_tv)
    TextView agendaTv;
    @Wire(view = R.id.activity_portada_reservas_tv)
    TextView reservasTv;
    @Wire(view = R.id.activity_portada_hoy_tv)
    TextView hoyTv;
    @Wire(view = R.id.activity_portada_sincronizacion_tv)
    TextView sincronizacionTv;
    @Wire(view = R.id.activity_portada_rutas_tv)
    TextView rutaTv;
    @Wire(view = R.id.activity_portada_medios_tv)
    TextView mediosTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = new Intent(this, OrderSynchronizationService.class);
        this.startService(i);
        clientesTv.setOnClickListener(this);
        pedidosTv.setOnClickListener(this);
        catalogoTv.setOnClickListener(this);
        devolucionesTv.setOnClickListener(this);
        cobrosTv.setOnClickListener(this);
        agendaTv.setOnClickListener(this);
        reservasTv.setOnClickListener(this);
        hoyTv.setOnClickListener(this);
        sincronizacionTv.setOnClickListener(this);
//        rutaTv.setOnClickListener(this);
        mediosTv.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.portada_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.portada_action_exit:
                User user = UserRepository.getInstance().getLastUserLogged();
                if (user != null) {
                    user.setFechaLogin(null);
                    try {
                        UserRepository.getInstance().saveOrUpdate(user);
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                }
                startActivityForResult(
                        getNextIntent(new Bundle(), this,
                                LoginActivity.class), 0);
                break;
            case R.id.portada_action_configuration:
                startActivityForResult(
                        getNextIntent(new Bundle(), this,
                                ConfigurationActivity.class), 0);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.quit)
                .setMessage(R.string.really_quit)
                .setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                // Stop the activity
                                PortadaActivity.this.finishAffinity();
                            }

                        }).setNegativeButton(R.string.cancel, null).show();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag.toString().length() > 0) {
            String option = tag.toString();
            if (option.equals("clientes")) {
                startActivity(getNextIntent(new Bundle(), this,
                        PartnerListActivity.class));
            } else if (option.equals("pedidos")) {
                if (OrderRepository.getInstance().isOrderInitialized() &&
                        MidbanApplication.getValueFromContext(ContextAttributes.PARTNER_TO_ORDER) != null &&
                        !(MidbanApplication.getValueFromContext(ContextAttributes.READ_ONLY_ORDER_MODE) != null
                                && (Boolean)MidbanApplication.getValueFromContext(ContextAttributes.READ_ONLY_ORDER_MODE)))
                    startActivityForResult(
                            getNextIntent(new Bundle(), this,
                                    OrderActivity.class), 0);
                else {
                    if ((MidbanApplication
                            .getValueFromContext(ContextAttributes.READ_ONLY_ORDER_MODE) != null
                            && (Boolean)MidbanApplication.getValueFromContext(
                            ContextAttributes.READ_ONLY_ORDER_MODE)))
                        OrderRepository.clearCurrentOrder();
                    new SelectPartnerDialog(this)
                            .openDialogForSelectingPartner(
                                    getResources()
                                            .getString(
                                                    R.string.activity_portada_new_order_dialog),
                                    ContextAttributes.PARTNER_TO_ORDER,
                                    OrderActivity.class);
                }
            } else if (option.equals("catalogo")) {
                startActivity(getNextIntent(new Bundle(), this,
                        ProductCatalogActivity.class));
            } else if (option.equals("devoluciones")) {
                MessagesForUser.showMessage(this, R.string.todo_message,
                        Toast.LENGTH_LONG, Level.WARNING);
                // startActivity(getNextIntent(new Bundle(), this,
                // PartnersActivity.class));
            } else if (option.equals("cobros")) {
                new SelectPartnerDialog(this).openDialogForSelectingPartner(
                        getResources().getString(
                                R.string.activity_portada_incomes_dialog),
                        ContextAttributes.PARTNER_TO_INCOME,
                        IncomeActivity.class);
            } else if (option.equals("agenda")) {
                startActivity(getNextIntent(new Bundle(), this,
                        CalendarActivity.class));
            } else if (option.equals("reservar")) {
                new SelectPartnerDialog(this)
                        .openDialogForSelectingPartner(
                                getResources()
                                        .getString(
                                                R.string.activity_portada_new_reservation_dialog),
                                ContextAttributes.PARTNER_TO_RESERVATION,
                                ReservationActivity.class);
            } else if (option.equals("hoy")) {
                startActivity(getNextIntent(new Bundle(), this,
                        TodayActivity.class));
            } else if (option.equals("sincronizar")) {
                startActivity(getNextIntent(new Bundle(), this,
                        SynchronizationActivity.class));
            } else if (option.equals("rutas")) {
                startActivity(getNextIntent(new Bundle(), this, RouteActivity.class));
            } else if (option.equals("medios")) {
                MessagesForUser.showMessage(this, R.string.todo_message, Toast.LENGTH_LONG, Level.WARNING);
            }

        }
    }
}
