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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.AccountJournal;
import com.cafedered.midban.entities.AccountMoveLine;
import com.cafedered.midban.entities.Invoice;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.AccountMoveLineRepository;
import com.cafedered.midban.service.repositories.InvoiceRepository;
import com.cafedered.midban.service.repositories.PartnerRepository;
import com.cafedered.midban.service.repositories.PaymentTypeRepository;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.SessionFactory;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.adapter.CustomArrayAdapter;
import com.cafedered.midban.view.fragments.InvoiceFragment;
import com.debortoliwines.openerp.api.Field;
import com.debortoliwines.openerp.api.FieldCollection;
import com.debortoliwines.openerp.api.FilterCollection;
import com.debortoliwines.openerp.api.ObjectAdapter;
import com.debortoliwines.openerp.api.OpeneERPApiException;
import com.debortoliwines.openerp.api.Row;
import com.debortoliwines.openerp.api.RowCollection;
import com.debortoliwines.openerp.api.Session;

import org.apache.xmlrpc.XmlRpcException;

public class IncomeRegistrationDialog extends Dialog {

    AccountMoveLine invoice;
    User user;

    TextView invoiceNumber;
    TextView invoiceDueDate;
    TextView invoiceAmount;
    TextView rest;
    TextView partnerName;
    Spinner paymentMethods;
    EditText amountGetIncome;
    EditText checkNumber;
    EditText checkDueDate;
    EditText account;
    Button confirmButton;
    Button cancelButton;
    LinearLayout form;
    LinearLayout confirmation;

    IncomeRegistrationDialog registrationDialog = this;
    InvoiceFragment invoiceFragment;

    public IncomeRegistrationDialog(Context context, AccountMoveLine accountMoveLineToPay,
            User user, InvoiceFragment fragment) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        invoice = accountMoveLineToPay;
        this.user = user;
        setContentView(R.layout.dialog_income_registration);
        partnerName = (TextView) findViewById(R.id.dialog_single_field_title_partner);
        invoiceNumber = (TextView) findViewById(R.id.income_registration_dialog_invoice_number);
        invoiceNumber.setText(invoice.getRef());
        invoiceDueDate = (TextView) findViewById(R.id.income_registration_dialog_invoice_due_date);
        form = (LinearLayout) findViewById(R.id.income_registration_dialog_income_form);
        confirmation = (LinearLayout) findViewById(R.id.income_registration_dialog_income_conf);
        rest = (TextView) findViewById(R.id.dialog_income_registration_amount_rest);
        try {
            if (invoice.getDateMaturity() != null)
                invoiceDueDate.setText(DateUtil.toFormattedString(
                        DateUtil.parseDate(invoice.getDateMaturity(), "yyyy-MM-dd"),
                        "dd.MM.yyyy"));
        } catch (NotFoundException e) {
            // do nothing
        } catch (ParseException e) {
            // do nothing
        }
        invoiceFragment = fragment;
        Partner partner = null;
        try {
            partner = PartnerRepository.getInstance().getById(invoice.getPartnerId().longValue());
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        partnerName.setText(partner.getName());
        invoiceAmount = (TextView) findViewById(R.id.income_registration_dialog_invoice_amount);
        invoiceAmount.setText(new BigDecimal(invoice.getAmountResidual().floatValue()).setScale(2, RoundingMode.HALF_UP).toString()
                + getContext().getResources().getString(
                        R.string.currency_symbol));
        paymentMethods = (Spinner) findViewById(R.id.payment_method);
        paymentMethods.setAdapter(new CustomArrayAdapter<AccountJournal>(
                context, getPaymentTypesForAdapter()));
        amountGetIncome = (EditText) findViewById(R.id.amount_get_income);
        amountGetIncome.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                float amount = 0;
                float amountRest = invoice.getAmountResidual().floatValue();
                try {
                    amount = Float.parseFloat(s.toString());
                } catch (NumberFormatException e) {
                    // do nothing
                }
                rest.setText(rest.getResources().getString(
                        R.string.dialog_income_registration_rest)
                        + " "
                        + (new BigDecimal(amountRest - amount).setScale(2,
                                RoundingMode.HALF_UP))
                        + rest.getResources().getString(
                                R.string.currency_symbol));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // do nothing

            }

            @Override
            public void afterTextChanged(Editable s) {
                // do nothing

            }
        });
        checkNumber = (EditText) findViewById(R.id.check_number);
        checkDueDate = (EditText) findViewById(R.id.check_due_date);
        account = (EditText) findViewById(R.id.account);
        confirmButton = (Button) findViewById(R.id.button_apply);
        confirmButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getValidationErrors().size() > 0) {

                } else {
                    sendPayment(invoice, Double.parseDouble(amountGetIncome
                            .getText().toString()),
                            (AccountJournal) paymentMethods.getSelectedItem(),
                            checkNumber.getText().toString(), checkDueDate
                                    .getText().toString(), account.getText()
                                    .toString());
                }

            }
        });
        cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationDialog.cancel();
            }
        });
    }

    public IncomeRegistrationDialog(Context context) {
        super(context);
    }

    protected void sendPayment(final AccountMoveLine invoiceToSend,
            final double amount, final AccountJournal paymentType,
            final String checkNumber, final String checkDueDate,
            final String account) {

        new AsyncTask<Void, Void, String>() {

            private ProgressDialog dialog = new ProgressDialog(getContext());

            @Override
            protected void onPreExecute() {
                this.dialog.setMessage(getContext().getResources().getString(
                        R.string.synchronizing));
                this.dialog.setCanceledOnTouchOutside(false);
                this.dialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    Session openERPSession = null;
                    User user = (User) MidbanApplication.getValueFromContext(ContextAttributes.LOGGED_USER);
                    try {
                        openERPSession = SessionFactory.getInstance(user.getLogin(), user.getPasswd()).getSession();
                    } catch (Exception e) {
                        // do nothing, openERPSession will be null
                    }
                    if (openERPSession != null) {
                        boolean saved = false;
                        if (paymentType.getType().contains("cash")) {
                            ObjectAdapter adapter;
                            try {
                                adapter = openERPSession.getObjectAdapter("account.bank.statement");
                                FilterCollection filters = new FilterCollection();
                                filters.add("journal_id", "=", paymentType.getId().intValue());
                                filters.add("user_id", "=", user.getId().intValue());
                                filters.add("state", "=", "open");
                                String[] fieldsRemote = {"id"};
                                RowCollection entities = adapter.searchAndReadObject(filters, fieldsRemote, "1900-01-01 00:00:00");
                                int idAccountBank;
                                if (entities != null && entities.size() > 0) {
                                    //Existe registro
                                    idAccountBank = entities.get(0).getID();
                                } else {
                                    Row newAccount = adapter.getNewRow(new String[]{"user_id", "journal_id", "date", "closing_date"});
                                    newAccount.put("user_id", user.getId().intValue());
                                    newAccount.put("journal_id", paymentType.getId().intValue());
                                    newAccount.put("date", DateUtil.toFormattedString(new Date(), "yyyy-MM-dd"));
                                    Calendar closingDate = Calendar.getInstance();
                                    closingDate.set(Calendar.DAY_OF_MONTH, closingDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                                    closingDate.set(Calendar.HOUR, closingDate.getActualMaximum(Calendar.HOUR));
                                    closingDate.set(Calendar.MINUTE, closingDate.getActualMaximum(Calendar.MINUTE));
                                    newAccount.put("closing_date", DateUtil.toFormattedString(new Date(), "yyyy-MM-dd HH:mm:ss"));
                                    adapter.createObject(newAccount);
                                    idAccountBank = newAccount.getID();
                                }
                                ObjectAdapter adapterLine = openERPSession.getObjectAdapter("account.bank.statement.line");
                                Row row = adapterLine.getNewRow(new String[]{"date", "name", "ref", "partner_id", "amount", "statement_id"});
                                row.put("date", DateUtil.toFormattedString(new Date(), "yyyy-MM-dd HH:mm:ss"));
                                row.put("name", invoiceToSend.getRef());
                                row.put("ref", invoiceToSend.getRef());
                                row.put("partner_id", invoiceToSend.getPartnerId());
                                row.put("amount", amount);
                                row.put("statement_id", idAccountBank);
                                adapterLine.createObject(row);
                                saved = true;
                            } catch (XmlRpcException e) {
                                e.printStackTrace();
                            } catch (OpeneERPApiException e) {
                                e.printStackTrace();
                            }
                        } else if (paymentType.getType().contains("bank")) {
                            ObjectAdapter adapter;
                            try {
                                adapter = openERPSession.getObjectAdapter("account.bank.statement");
                                FilterCollection filters = new FilterCollection();
                                filters.add("journal_id", "=", paymentType.getId().intValue());
//                                filters.add("user_id", "=", user.getId().intValue());
                                filters.add("state", "=", "draft");
                                String[] fieldsRemote = {"id"};
                                RowCollection entities = adapter.searchAndReadObject(filters, fieldsRemote, "1900-01-01 00:00:00");
                                int idAccountBank;
                                if (entities != null && entities.size() > 0) {
                                    //Existe registro
                                    idAccountBank = entities.get(0).getID();
                                    ObjectAdapter adapterLine = openERPSession.getObjectAdapter("account.bank.statement.line");
                                    Row row = adapterLine.getNewRow(new String[]{"date", "name", "ref", "partner_id", "amount", "statement_id"});
                                    row.put("date", DateUtil.toFormattedString(DateUtil.parseDate(checkDueDate, "dd.MM.yyyy"), "yyyy-MM-dd"));
                                    row.put("name", checkNumber);
                                    row.put("ref", invoiceToSend.getRef());
                                    row.put("partner_id", invoiceToSend.getPartnerId());
                                    row.put("amount", amount);
                                    row.put("statement_id", idAccountBank);
                                    adapterLine.createObject(row);
                                    saved = true;
                                }
                            } catch (XmlRpcException e) {
                                e.printStackTrace();
                            } catch (OpeneERPApiException e) {
                                e.printStackTrace();
                            }
                        }
                        if (saved) {
                            invoiceToSend.setPaymentMade(1);
                            invoiceToSend.setPaymentMadeValue(amount);
                            invoiceToSend.setPaymentMadeDate(DateUtil.toFormattedString(new Date()));
                                    AccountMoveLineRepository.getInstance().saveOrUpdate(invoiceToSend);
                            return getContext()
                                    .getResources()
                                    .getString(
                                            R.string.dialog_income_registration_payment_registered_and_sent_to_server);
                        } else {
                            return "";
                        }
//                    InvoiceRepository.getInstance().makePayment(user,
//                            invoiceToSend, amount, paymentType, checkNumber,
//                            checkDueDate, account);
                    }
                } catch (Exception e) {
                    return e.getMessage();
                }
                return "";
            }

            @Override
            protected void onPostExecute(final String message) {
                dialog.cancel();
                confirmButton.setText(getContext().getResources().getString(
                        R.string.ok));
                cancelButton.setVisibility(View.GONE);
                form.setVisibility(View.GONE);
                confirmation.setVisibility(View.VISIBLE);
                confirmButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        invoiceFragment.loadInvoices();
                        IncomeRegistrationDialog.this.dismiss();

                        // TODO update state invoice in InvoiceFragment
//                        String message = getContext()
//                                .getResources()
//                                .getString(
//                                        R.string.dialog_income_registration_payment_done,
//                                        amount, invoice.getNumber());
//                        try {
//                            sendEmail(
//                                    new String[] { invoice.getPartner()
//                                            .getEmail() },
//                                    getContext()
//                                            .getResources()
//                                            .getString(
//                                                    R.string.dialog_income_registration_payment_ticket),
//                                    message, null);
//                            MessagesForUser.showMessage(getCurrentFocus(),
//                                    R.string.mail_sent, Toast.LENGTH_LONG,
//                                    Level.INFO);
//                        } catch (ConfigurationException e) {
//                            if (LoggerUtil.isDebugEnabled())
//                                e.printStackTrace();
//                            MessagesForUser.showMessage(getCurrentFocus(),
//                                    R.string.mail_not_sent, Toast.LENGTH_LONG,
//                                    Level.SEVERE);
//                        } catch (ServiceException e) {
//                            if (LoggerUtil.isDebugEnabled())
//                                e.printStackTrace();
//                            MessagesForUser.showMessage(getCurrentFocus(),
//                                    R.string.mail_not_sent, Toast.LENGTH_LONG,
//                                    Level.SEVERE);
//                        } catch (Exception e) {
//                            if (LoggerUtil.isDebugEnabled())
//                                e.printStackTrace();
//                            MessagesForUser.showMessage(getCurrentFocus(),
//                                    R.string.mail_not_sent, Toast.LENGTH_LONG,
//                                    Level.SEVERE);
//                        }
                    }

                    public void sendEmail(String[] to, String issue,
                            String message, Uri uri) throws Exception {

                        try {
                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.setData(Uri.parse("mailto:"));
                            emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, issue);
                            emailIntent.putExtra(Intent.EXTRA_TEXT, message);
                            emailIntent.setType("plain/text");
                            getContext().startActivity(emailIntent);

                        } catch (Exception e) {
                            e.printStackTrace();
                            throw e;
                        }
                    }
                });
                if (message
                        .equals(getContext()
                                .getResources()
                                .getString(
                                        R.string.dialog_income_registration_payment_registered_and_sent_to_server)))
                    MessagesForUser.showMessage(confirmButton, message,
                            Toast.LENGTH_LONG, Level.INFO);
                else {
                    MessagesForUser.showMessage(confirmButton, getContext()
                                    .getResources()
                                    .getString(
                                            R.string.income_not_saved),
                            Toast.LENGTH_LONG, Level.SEVERE);
                    TextView mensaje = (TextView) IncomeRegistrationDialog.this.findViewById(R.id.dialog_title_income_done);
                    mensaje.setText(getContext()
                            .getResources()
                            .getString(
                                    R.string.income_not_saved));
                }
            }
        }.execute();
    }

    protected List<String> getValidationErrors() {
        List<String> result = new ArrayList<String>();
        // TODO validation must be for checks or account payments only
        if (((AccountJournal)paymentMethods.getSelectedItem()).getType().contains("bank")) {
            if (amountGetIncome.getText() == null
                    || Double.parseDouble(amountGetIncome.getText().toString()) <= 0
                    || Double.parseDouble(amountGetIncome.getText().toString()) > invoice
                            .getAmountResidual().doubleValue())
                result.add(getContext().getResources().getString(
                        R.string.dialog_income_registration_amount_not_correct));
            if (checkNumber.getText() == null
                    || checkNumber.getText().toString().equals(""))
                result.add(getContext().getResources().getString(
                        R.string.dialog_income_registration_check_mandatory));
            if (checkDueDate.getText() == null
                    || checkDueDate.getText().toString().equals(""))
                result.add(getContext()
                        .getResources()
                        .getString(
                                R.string.dialog_income_registration_check_due_date_mandatory));
//            if (account.getText() == null
//                    || account.getText().toString().equals(""))
//                result.add(getContext().getResources().getString(
//                        R.string.dialog_income_registration_account_mandatory));
        }
        return result;
    }

    private AccountJournal[] getPaymentTypesForAdapter() {
        List<AccountJournal> paymentTypes = new ArrayList<AccountJournal>();
        try {
            paymentTypes = PaymentTypeRepository.getInstance()
                    .getAll(0, 100000);
            Iterator<AccountJournal> itAccountJournals = paymentTypes.iterator();
            while (itAccountJournals.hasNext()) {
                AccountJournal type = itAccountJournals.next();
                if (!(type.getType().equals("cash") || type.getType().equals("bank"))) {
                    itAccountJournals.remove();
                }
            }
        } catch (ConfigurationException e) {
            // do nothing
        } catch (ServiceException e) {
            // do nothing
        }
        AccountJournal[] result = new AccountJournal[paymentTypes.size()];
        result = paymentTypes.toArray(result);
        return result;
    }
}