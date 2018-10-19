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
package com.cafedered.midban.view.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.TextChanged;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.CustomerList;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.Route;
import com.cafedered.midban.entities.RouteDetail;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.entities.WeekDay;
import com.cafedered.midban.entities.decorators.RouteDecorator;
import com.cafedered.midban.service.repositories.CustomerListRepository;
import com.cafedered.midban.service.repositories.PartnerRepository;
import com.cafedered.midban.service.repositories.RouteDetailRepository;
import com.cafedered.midban.service.repositories.RouteRepository;
import com.cafedered.midban.service.repositories.UserRepository;
import com.cafedered.midban.service.repositories.WeekDayRepository;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.adapter.RouteListItemAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

@Fragment(R.layout.fragment_route)
public class RouteFragment extends BaseSupportFragment {

    @Wire(view = R.id.fragment_route_list)
    ListView routesListView;
    @Wire(view = R.id.fragment_route_date_et)
    private EditText dateSelected;
    List<RouteDecorator> routes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        setHasOptionsMenu(true);
        dateSelected.setText(DateUtil.toFormattedString(new Date(),
                "dd.MM.yyyy"));
        try {
            new AsyncTask<Void, Void, List<RouteDecorator>>() {

                private ProgressDialog dialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    dialog = ProgressDialog.show(RouteFragment.this.getActivity(), "Cargando...",
                            "Cargando rutas", true);
                }

                @Override
                protected List<RouteDecorator> doInBackground(Void... params) {
                    try {
                        return obtainRoutes((User)MidbanApplication.getValueFromContext(ContextAttributes.LOGGED_USER));
                    } catch (ServiceException e) {
                        e.printStackTrace();
                        return Collections.EMPTY_LIST;
                    } catch (ConfigurationException e) {
                        e.printStackTrace();
                        return Collections.EMPTY_LIST;
                    }
                }

                @Override
                protected void onPostExecute(List<RouteDecorator> routeDecorators) {
                    super.onPostExecute(routeDecorators);
                    this.dialog.dismiss();
                    routes = routeDecorators;
                    routesListView.setAdapter(new RouteListItemAdapter(RouteFragment.this, routes));
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
            MessagesForUser.showMessage(getActivity(), R.string.fragment_route_error_obtaining_routes, Toast.LENGTH_LONG, Level.SEVERE);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            new AsyncTask<Void, Void, List<RouteDecorator>>() {

                private ProgressDialog dialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    dialog = ProgressDialog.show(RouteFragment.this.getActivity(), "Cargando...",
                            "Cargando rutas", true);
                }

                @Override
                protected List<RouteDecorator> doInBackground(Void... params) {
                    try {
                        return obtainRoutes((User)MidbanApplication.getValueFromContext(ContextAttributes.LOGGED_USER));
                    } catch (ServiceException e) {
                        e.printStackTrace();
                        return Collections.EMPTY_LIST;
                    } catch (ConfigurationException e) {
                        e.printStackTrace();
                        return Collections.EMPTY_LIST;
                    }
                }

                @Override
                protected void onPostExecute(List<RouteDecorator> routeDecorators) {
                    super.onPostExecute(routeDecorators);
                    this.dialog.dismiss();
                    routes = routeDecorators;
                    routesListView.setAdapter(new RouteListItemAdapter(RouteFragment.this, routes));
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
            MessagesForUser.showMessage(getActivity(), R.string.fragment_route_error_obtaining_routes, Toast.LENGTH_LONG, Level.SEVERE);
        }
    }

    private List<RouteDecorator> obtainRoutes(User loggedUser) throws ServiceException, ConfigurationException {
        List<RouteDecorator> result = new ArrayList<RouteDecorator>();
        int hour = 8;
        int minute = 0;
        User userExample = new User();
        userExample.setLogin(loggedUser.getLogin());
        User user = UserRepository.getInstance().getByExample(userExample, Restriction.AND, true, 0, 1).get(0);
        if (user.getRouteIds() != null) {
            List<String> routeIdsOfUser = Arrays.asList((String[]) user.getRouteIds().split(";"));
            for (String routeId : routeIdsOfUser) {
                if (routeId != null && routeId.length() > 0) {
                    Route route = RouteRepository.getInstance().getById(Long.valueOf(routeId));
                    Calendar now = Calendar.getInstance();
                    try {
                        now.setTime(DateUtil.parseDate(dateSelected.getText().toString(), "dd.MM.yyyy"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        now.setTime(new Date());
                    }
                    int today;
                    if (now.get(Calendar.DAY_OF_WEEK) != 1)
                        today = now.get(Calendar.DAY_OF_WEEK) - 1;
                    else
                        today = 7;
                    if (route != null) {
                        WeekDay weekday = WeekDayRepository.getInstance().getById(route.getDayId().longValue());
                        if (weekday != null && weekday.getSequence() != null &&
                                weekday.getSequence().intValue() == today) {
                            try {
                                RouteDetail detail = RouteDetailRepository.getInstance().getByRouteId(route.getId().longValue(), now.getTime());
                                if (detail != null && detail.getCustomers() != null) {
                                    String[] customers = detail.getCustomers().split(";");
                                    for (String aCustomerId : customers) {
                                        if (aCustomerId != null && aCustomerId.length() > 0) {
                                            CustomerList cl = CustomerListRepository.getInstance().getById(Long.valueOf(aCustomerId));
                                            Partner partner = null;
                                            if (cl != null) {
                                                partner = PartnerRepository.getInstance().getById(cl.getCustomerId().longValue());
                                            }
                                            if (partner != null) {
                                                String hourFormatted = String.format("%02d", hour) + ":" + String.format("%02d", minute);
                                                if (hour == 23 && minute == 50) {
                                                    hour = 0;
                                                } else if (minute == 50) hour++;
                                                if (minute < 50)
                                                    minute += 10;
                                                else minute = 0;
                                                //TODO medios!!
                                                result.add(RouteDecorator.createRouteDecorator(partner.getId(), hourFormatted, partner.getName(), "", partner.getPhone(), partner.getCompleteAddress(), partner.getZip(), partner.getCity(), partner.getEmail(), "", BigDecimal.ZERO, 0, "0/0", Integer.valueOf(aCustomerId)));
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    @Click(view = R.id.fragment_route_date_et)
    public void dateClicked() {
        DatePickerFragment.create(dateSelected).show(getFragmentManager(),
                "datePicker");
    }

    @TextChanged(view = R.id.fragment_route_date_et)
    public void dateChanged() {
        onResume();
    }

    public static class DatePickerFragment extends android.support.v4.app.DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        public static EditText editText;
        DatePicker dpResult;

        public DatePickerFragment() {
            super();
        }

        public static DatePickerFragment create(EditText edition) {
            DatePickerFragment result = new DatePickerFragment();
            editText = edition;
            return result;
        }

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this,
                    year, month, day);
            DatePicker picker = dialog.getDatePicker();
            picker.setCalendarViewShown(false); // bug in Android when setting
            // max date does not allow to
            // show calendar view

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 6);
            Calendar calendarMin = Calendar.getInstance();
            calendarMin.add(Calendar.DATE, -3);
            picker.setMaxDate(calendar.getTimeInMillis());
            picker.setMinDate(calendarMin.getTimeInMillis());
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            editText.setText(String.format("%02d", day) + "."
                    + String.format("%02d", (month + 1)) + "."
                    + String.valueOf(year));
            this.dismiss();
        }
    }
}
