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
package com.cafedered.midban.view.adapter;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.CustomerList;
import com.cafedered.midban.entities.Invoice;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.Route;
import com.cafedered.midban.entities.decorators.RouteDecorator;
import com.cafedered.midban.service.repositories.CustomerListRepository;
import com.cafedered.midban.service.repositories.InvoiceRepository;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.service.repositories.PartnerRepository;
import com.cafedered.midban.service.repositories.RouteRepository;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.SessionFactory;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.IncomeActivity;
import com.cafedered.midban.view.activities.OrderActivity;
import com.cafedered.midban.view.fragments.RouteFragment;
import com.debortoliwines.openerp.api.OpenERPCommand;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RouteListItemAdapter extends BaseAdapter {

    private List<RouteDecorator> routes;
    private static LayoutInflater inflater = null;
    private RouteFragment fragment;

    public RouteListItemAdapter(RouteFragment fragment, List<RouteDecorator> routes) {
        this.routes = routes;
        this.fragment = fragment;
        if (fragment != null && fragment.getActivity() != null)
            inflater = (LayoutInflater) fragment.getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return routes.size();
    }

    public Object getItem(int position) {
        return routes.get(position);
    }

    public long getItemId(int position) {
        return routes.get(position).getId();
    }

    public static class ViewHolder {
        public TextView plannedHour;
        public TextView openingHours;
        public TextView partnerName;
        public TextView contactName;
        public TextView phoneNumber;
        public TextView address;
        public TextView orderLink;
        public ImageView orderLinkIv;
        public TextView incomeLink;
        public ImageView incomeLinkIv;
        public TextView mediumLink;
        public ImageView mediumLinkIv;
        public TextView closeLink;
        public ImageView closeLinkIv;
        public LinearLayout closeGroup;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
//        if (convertView == null) {
            vi = inflater.inflate(R.layout.route_list_item, null);
            holder = new ViewHolder();
            holder.plannedHour = (TextView) vi.findViewById(R.id.route_list_item_planned_hour);
            holder.openingHours = (TextView) vi.findViewById(R.id.route_list_item_opening_hours);
            holder.partnerName = (TextView) vi.findViewById(R.id.route_list_item_social_name);
            holder.contactName = (TextView) vi.findViewById(R.id.route_list_item_contact_name);
            holder.phoneNumber = (TextView) vi.findViewById(R.id.route_list_item_phone);
            holder.address = (TextView) vi.findViewById(R.id.route_list_item_address);
            holder.orderLink = (TextView) vi.findViewById(R.id.route_list_item_make_order_tv);
            holder.orderLinkIv = (ImageView) vi.findViewById(R.id.route_list_item_make_order_iv);
            holder.incomeLink = (TextView) vi.findViewById(R.id.route_list_item_make_income_tv);
            holder.incomeLinkIv = (ImageView) vi.findViewById(R.id.route_list_item_make_income_iv);
            holder.mediumLink = (TextView) vi.findViewById(R.id.route_list_item_medium_tv);
            holder.mediumLinkIv = (ImageView) vi.findViewById(R.id.route_list_item_make_medium_iv);
            holder.closeLink = (TextView) vi.findViewById(R.id.route_list_item_close_visit_tv);
            holder.closeLinkIv = (ImageView) vi.findViewById(R.id.route_list_item_close_visit_iv);
            holder.closeGroup = (LinearLayout) vi.findViewById(R.id.route_list_item_close_group);
            vi.setTag(holder);
//        } else
//            holder = (ViewHolder) vi.getTag();
        final RouteDecorator route = routes.get(position);
        holder.plannedHour.setText(route.getHour());
        holder.openingHours.setText(route.getHorarioApertura());
        holder.partnerName.setText(route.getName());
        holder.contactName.setText(route.getPartnerContact());
        holder.phoneNumber.setText(route.getPhone());
        holder.phoneNumber.setPaintFlags(holder.phoneNumber.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.phoneNumber.setOnClickListener(makeCall(route));
        holder.address.setText(route.getAddress() == null ? "" : route.getAddress() + " " + route.getTown() == null ? "" : route.getTown());
        holder.orderLinkIv.setOnClickListener(getOrderClickListener(route));
        holder.orderLink.setText("...");
        holder.orderLink.setVisibility(View.INVISIBLE);
        getOrdersFor(route.getId(), holder.orderLink);
        holder.incomeLink.setText("...");
        holder.incomeLink.setVisibility(View.INVISIBLE);
        getAmountDebtFor(route.getId(), holder.incomeLink);
//        holder.incomeLink.setText(route.getAmountToIncome().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + vi.getResources().getString(R.string.currency_symbol));
        holder.incomeLinkIv.setOnClickListener(getIncomeClickListener(route));
        holder.mediumLink.setText(route.getMediumText());
        getCloseVisitText(holder.closeGroup, route);
        holder.closeLink.setOnClickListener(getCloseVisitClickListener(route));
        holder.closeLinkIv.setOnClickListener(getCloseVisitClickListener(route));
        return vi;
    }

    private void getCloseVisitText(View closeGroup, RouteDecorator route) {
        try {
            CustomerList customer = CustomerListRepository.getInstance().getById(route.getCustomerListId().longValue());
            if (customer != null && customer.getResult() != null) {
                switch (customer.getResult()) {
                    case "sale_done": {
                        closeGroup.setBackgroundColor(closeGroup.getResources().getColor(R.color.ok_background_light));
                        break;
                    }
                    case "pending": {
                        closeGroup.setBackgroundColor(closeGroup.getResources().getColor(R.color.error_background_light));
                        break;
                    }
                    default: {
                        closeGroup.setBackgroundColor(closeGroup.getResources().getColor(R.color.warn_background_light));
                    }
                }
            } else {
                closeGroup.setBackgroundColor(closeGroup.getResources().getColor(R.color.error_background_light));
            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

    }

    private void getOrdersFor(final Long id, final TextView orderLink) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                return OrderRepository.getInstance().getNumberOfOrdersForPartner(id);
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                orderLink.setText("" + result);
                orderLink.setVisibility(View.VISIBLE);
                AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                fadeIn.setDuration(1500);
                fadeIn.setFillAfter(true);
                orderLink.startAnimation(fadeIn);
            }
        }.execute();
    }

    private void getAmountDebtFor(final Long id, final TextView amountLink) {
        new AsyncTask<Void, Void, BigDecimal>() {
            @Override
            protected BigDecimal doInBackground(Void... params) {
                try {
                    return new BigDecimal(PartnerRepository.getInstance().getById(id).getCredit().floatValue());
                } catch (ConfigurationException e) {
                    e.printStackTrace();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(BigDecimal result) {
                super.onPostExecute(result);
                if (result != null)
                    amountLink.setText("" + result.setScale(2, RoundingMode.HALF_UP).toString());
                else
                    amountLink.setText("-");
                AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                fadeIn.setDuration(1500);
                fadeIn.setFillAfter(true);
                amountLink.setVisibility(View.VISIBLE);
                amountLink.startAnimation(fadeIn);
            }
        }.execute();
    }

    private View.OnClickListener makeCall(final RouteDecorator route) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + route.getPhone()));
                v.getContext().startActivity(intent);
            }
        };
    }

    private View.OnClickListener getOrderClickListener(final RouteDecorator route) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Partner partner = PartnerRepository.getInstance().getById(route.getId());
                    MidbanApplication.putValueInContext(ContextAttributes.PARTNER_TO_ORDER, partner);
                    Intent intent = new Intent(fragment.getActivity(), OrderActivity.class);
                    intent.putExtras(new Bundle());
                    fragment.startActivity(intent);
                } catch (ConfigurationException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                } catch (ServiceException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                }
            }
        };
    }

    private View.OnClickListener getIncomeClickListener(final RouteDecorator route) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Partner partner = PartnerRepository.getInstance().getById(route.getId());
                    MidbanApplication.putValueInContext(ContextAttributes.PARTNER_TO_INCOME, partner);
                    Intent intent = new Intent(fragment.getActivity(), IncomeActivity.class);
                    intent.putExtras(new Bundle());
                    fragment.startActivity(intent);
                } catch (ConfigurationException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                } catch (ServiceException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                }
            }
        };
    }

    private View.OnClickListener getCloseVisitClickListener(final RouteDecorator route) {
        return new View.OnClickListener() {
            private int selectedItem = 0;
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(v.getContext());
                CharSequence items[] = new CharSequence[] {"Venta", "Pendiente", "Visitado sin pedido", "Cerrado hoy", "Cerrado por vacaciones", "Cerrado por reforma",
                    "No visitado", "Entregado OK", "Entregado con incidencia"};
                adb.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface d, int n) {
                        selectedItem = n;
                    }

                });
                adb.setNegativeButton("Cancelar", null);
                adb.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (selectedItem) {
                            case 0: cerrarRuta("sale_done", route.getCustomerListId());
                                break;
                            case 1: cerrarRuta("pending", route.getCustomerListId());
                                break;
                            case 2: cerrarRuta("visited_no_order", route.getCustomerListId());
                                break;
                            case 3: cerrarRuta("closed_day", route.getCustomerListId());
                                break;
                            case 4: cerrarRuta("closed_holidays", route.getCustomerListId());
                                break;
                            case 5: cerrarRuta("closed_reform", route.getCustomerListId());
                                break;
                            case 6: cerrarRuta("no_visited", route.getCustomerListId());
                                break;
                            case 7: cerrarRuta("delivered_ok", route.getCustomerListId());
                                break;
                            case 8: cerrarRuta("delivered_issue", route.getCustomerListId());
                                break;
                        }
                    }
                });
                adb.setTitle("Cierre de visita");
                adb.show();
            }
        };
    }

    private void cerrarRuta(final String result, final Integer customerListId) {
        try {
            final CustomerList customerList = CustomerListRepository.getInstance().getById(customerListId.longValue());
            customerList.setResult(result);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        OpenERPCommand command = new OpenERPCommand(SessionFactory
                                .getInstance(MidbanApplication.getLoggedUser().getLogin(),
                                        MidbanApplication.getLoggedUser().getPasswd()).getSession());
                        HashMap<String, Object> visita = new HashMap<String, Object>();
                        visita.put("result", result);
                        command.writeObject("customer.list", customerListId, visita);
                        CustomerListRepository.getInstance().saveOrUpdate(customerList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    notifyDataSetChanged();
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

}