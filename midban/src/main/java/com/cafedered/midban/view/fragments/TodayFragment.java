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

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.TextChanged;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.SynchronizationSummary;
import com.cafedered.midban.service.repositories.SynchronizationSummaryRepository;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.SynchronizationActivity;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.listeners.ITodayDataChangedListenerFragment;

@Fragment(R.layout.fragment_today)
public class TodayFragment extends BaseSupportFragment {

    @Wire(view = R.id.fragment_today_date_et)
    private EditText dateSelected;
    @Wire(view = R.id.fragment_today_tab_orders)
    private TextView tabOrders;
    @Wire(view = R.id.fragment_today_tab_incomes)
    private TextView tabIncomes;
    // TODO add remaining tab (check the XML!!!)
    // @Wire(view = R.id.fragment_today_tab_devolutions)
    // private TextView tabDevolutions;
    @Wire(view = R.id.fragment_today_last_synchronization_date)
    private TextView lastSynchronizationDate;
    @Wire(view = R.id.fragment_today_frame)
    private FrameLayout frame;

    boolean isInitializationDone = false;

    Map<Integer, android.support.v4.app.Fragment> tabIds = new LinkedHashMap<Integer, android.support.v4.app.Fragment>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container,
                savedInstanceState);
        MidbanApplication.putValueInContext(
                ContextAttributes.TODAY_ACTIVITY_DATE_SELECTED,
                DateUtil.toFormattedString(new Date(), "yyyy-MM-dd"));
        dateSelected.setText(DateUtil.toFormattedString(new Date(),
                "dd.MM.yyyy"));
        List<SynchronizationSummary> theList;
        String dateToShow = "";
        try {
            theList = SynchronizationSummaryRepository.getInstance().getAll(0,
                    500000000);
            Collections.reverse(theList);
            String lastDate = null;
            for (SynchronizationSummary aSynchro : theList) {
                if (aSynchro.getResult().trim().equals("Ok")) {
                    lastDate = aSynchro.getDate();
                    break;
                }
            }
            if (lastDate != null)
                dateToShow = DateUtil.toFormattedString(
                        DateUtil.parseDate(lastDate, "yyyyMMddHHmmss"),
                        "dd.MM.yyyy HH:mm");
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ParseException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        lastSynchronizationDate.setText(getResources().getString(
                R.string.fragment_today_last_synchronization_date)
                + " " + dateToShow);
        setupDefaultTab();
        return result;
    }

    @Click(view = R.id.fragment_today_date_et)
    public void dateClicked() {
        DatePickerFragment.create(dateSelected).show(getFragmentManager(),
                "datePicker");
    }

    @TextChanged(view = R.id.fragment_today_date_et)
    public void dateChanged() {
        if (isInitializationDone) {
            try {
                MidbanApplication.putValueInContext(
                        ContextAttributes.TODAY_ACTIVITY_DATE_SELECTED,
                        DateUtil.toFormattedString(DateUtil
                                .parseDate(dateSelected.getText().toString(),
                                        "dd.MM.yyyy"), "yyyy-MM-dd"));
                if (getFragmentManager().findFragmentByTag("orders") != null)
                    ((ITodayDataChangedListenerFragment) getFragmentManager()
                            .findFragmentByTag("orders"))
                            .notifyDataChangedListener(DateUtil
                                    .toFormattedString(DateUtil.parseDate(
                                            dateSelected.getText().toString(),
                                            "dd.MM.yyyy"), "yyyy-MM-dd"));
                if (getFragmentManager().findFragmentByTag("incomes") != null)
                    ((ITodayDataChangedListenerFragment) getFragmentManager()
                            .findFragmentByTag("incomes"))
                            .notifyDataChangedListener(DateUtil
                                    .toFormattedString(DateUtil.parseDate(
                                            dateSelected.getText().toString(),
                                            "dd.MM.yyyy"), "yyyy-MM-dd"));
                // TODO add remaining tab
                // if (getSupportFragmentManager().findFragmentByTag("devolutions") !=
                // null)
                // ((ITodayDataChangedListenerFragment) getSupportFragmentManager()
                // .findFragmentByTag("devolutions"))
                // .notifyDataChangedListener(DateUtil
                // .toFormattedString(DateUtil.parseDate(
                // dateSelected.getText().toString(),
                // "dd.MM.yyyy"), "yyyy-MM-dd"));
            } catch (ParseException e) {
                if (LoggerUtil.isDebugEnabled())
                    e.printStackTrace();
            }
        } else {
            isInitializationDone = true;
        }
    }

    @Click(view = R.id.fragment_today_send)
    public void sendClicked() {
        startActivityForResult(
                getNextIntent(new Bundle(), getView(),
                        SynchronizationActivity.class), 0);
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
            picker.setMaxDate(new Date().getTime());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -7);
            picker.setMinDate(calendar.getTimeInMillis());
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            editText.setText(String.format("%02d", day) + "."
                    + String.format("%02d", (month + 1)) + "."
                    + String.valueOf(year));
            this.dismiss();
        }
    }

    // Tab management
    private void setupDefaultTab() {
        tabOrders.setBackgroundColor(getResources().getColor(R.color.white));
        android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) new OrderListFragment();
        getFragmentManager().beginTransaction()
                .replace(frame.getId(), fragment, "orders").commit();
        tabIncomes.setBackgroundColor(getResources().getColor(
                R.color.midban_grey));
        tabIds.put(tabOrders.getId(), fragment);
        tabIds.put(tabIncomes.getId(), new IncomeListFragment());
        // TODO add remaining tab
        // tabIds.put(tabDevolutions.getId(), new DevolutionListFragment());
    }

    @Click(views = { R.id.fragment_today_tab_orders,
            R.id.fragment_today_tab_incomes,
    /* TODO add remaining tabs R.id.fragment_today_tab_devolutions */})
    public void clickTab(View view) {
        try {
            view.setBackgroundColor(getResources().getColor(R.color.white));
            getFragmentManager().executePendingTransactions();
            getFragmentManager()
                    .beginTransaction()
                    .replace(frame.getId(), tabIds.get(view.getId()),
                            view.getTag().toString()).commit();
            for (Integer id : tabIds.keySet()) {
                if (!id.equals(view.getId())) {
                    getView().findViewById(id).setBackgroundColor(
                            getResources().getColor(R.color.midban_grey));
                }
            }
        } catch (IllegalStateException e) {
            // avoid troubles when users click like chimps over tabs! :)
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
    }
}
