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
package com.cafedered.midban.view.dialogs;

import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.service.repositories.InvoiceRepository;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.adapter.CustomArrayAdapter;
import com.cafedered.midban.view.adapter.InvoiceListItemAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;

public class FilterInvoicesDialog extends Dialog {

    private String dateFrom;
    private String dateTo;
    private Float amountLessThan;
    private Float amountMoreThan;
    private String state;
    private Partner partner;

    private static FilterInvoicesDialog instance = null;

    public static FilterInvoicesDialog getInstance(Context context,
            final BaseSupportFragment fragment, ListView list, boolean first,
            Partner partner) {
        if (first)
            instance = null;
        if (instance == null)
            instance = new FilterInvoicesDialog(context, fragment, list);
        instance.partner = partner;
        return instance;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public FilterInvoicesDialog(Context context, final BaseSupportFragment fragment,
            final ListView list) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_invoice_filters);

        final EditText dateFromEt = (EditText) findViewById(R.id.dialog_invoice_from);
        dateFromEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment.create(dateFromEt).show(
                        fragment.getFragmentManager(), "datePicker");
            }
        });
        final EditText dateToEt = (EditText) findViewById(R.id.dialog_invoice_to);
        dateToEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment.create(dateToEt).show(
                        fragment.getFragmentManager(), "datePicker");
            }
        });
        final EditText amountLessThanEt = (EditText) findViewById(R.id.dialog_invoice_amount_less_than);
        final EditText amountMoreThanEt = (EditText) findViewById(R.id.dialog_invoice_amount_more_than);
        final Spinner states = (Spinner) findViewById(R.id.dialog_invoice_filters_spinner_state);
        Set setStates = new LinkedHashSet<String>();
        setStates.add(getContext().getResources().getString(
                R.string.filter_invoices_dialog_states));
        try {
            setStates.addAll(InvoiceRepository.getInstance()
                    .getAllDistinctSomeProperty("state"));
        } catch (ServiceException e1) {
            if (LoggerUtil.isDebugEnabled())
                e1.printStackTrace();
        }
        states.setAdapter(new CustomArrayAdapter<String>(context, setStates));

        Button apply = (Button) findViewById(R.id.dialog_invoices_filters_button_apply);
        apply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!states.getSelectedItem().equals(
                        getContext().getResources().getString(
                                R.string.filter_invoices_dialog_states)))
                    state = states.getSelectedItem().toString();
                else
                    state = null;
                try {
                    dateFrom = DateUtil.toFormattedString(DateUtil.parseDate(
                            dateFromEt.getText().toString(), "dd.MM.yyyy"),
                            "yyyy-MM-dd");
                } catch (Exception e) {
                    dateFrom = null;
                }
                try {
                    dateTo = DateUtil.toFormattedString(DateUtil.parseDate(
                            dateToEt.getText().toString(), "dd.MM.yyyy"),
                            "yyyy-MM-dd");
                } catch (Exception e) {
                    dateTo = null;
                }
                try {
                    amountLessThan = Float.parseFloat(amountLessThanEt
                            .getText().toString());
                } catch (Exception e) {
                    amountLessThan = null;
                }
                try {
                    amountMoreThan = Float.parseFloat(amountMoreThanEt
                            .getText().toString());
                } catch (Exception e) {
                    amountMoreThan = null;
                }
                list.setAdapter(new InvoiceListItemAdapter(fragment,
                        InvoiceRepository.getInstance().getInvoicesWithFilters(
                                state, dateFrom, dateTo, amountLessThan,
                                amountMoreThan, partner.getId())));
                instance.cancel();
            }
        });

        TextView clearFilters = (TextView) findViewById(R.id.dialog_invoices_filters_clear);
        clearFilters.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                states.setSelection(0);
                dateTo = "";
                dateFrom = "";
                amountLessThan = null;
                amountMoreThan = null;
                dateToEt.setText(v.getResources().getString(
                        R.string.dialog_invoice_date_to));
                dateFromEt.setText(v.getResources().getString(
                        R.string.dialog_invoice_date_from));
                amountMoreThanEt.setText(v.getResources().getString(
                        R.string.dialog_invoice_amount_more_than));
                amountLessThanEt.setText(v.getResources().getString(
                        R.string.dialog_invoice_amount_less_than));
            }
        });
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
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            editText.setText(String.format("%02d", day) + "."
                    + String.format("%02d", (month + 1)) + "."
                    + String.valueOf(year));
            this.dismiss();
        }
    }

}
