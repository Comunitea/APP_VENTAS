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
package com.cafedered.midban.view.activities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.logging.Level;

import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Activity;
import com.cafedered.midban.annotations.VoidFragment;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Invoice;
import com.cafedered.midban.entities.InvoiceLine;
import com.cafedered.midban.service.repositories.InvoiceRepository;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.base.BaseSupportActivity;

@Activity(displayAppIcon = true,
        initFragment = VoidFragment.class,
        layout = R.layout.activity_invoice_detail,
        title = R.string.activity_invoice_detail_title,
        rootView = R.id.activity_invoice_detail_root_view)
public class InvoiceDetailActivity extends BaseSupportActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Invoice invoice = (Invoice) MidbanApplication
                .getValueFromContext(ContextAttributes.INVOICE_TO_DETAIL);
        try {
            invoice = InvoiceRepository.getInstance().getById(invoice.getId());
        } catch (ConfigurationException e) {
            MessagesForUser.showMessage(this,
                    R.string.activity_invoice_detail_invoice_cannot_be_showed,
                    Toast.LENGTH_LONG, Level.SEVERE);
        } catch (ServiceException e) {
            MessagesForUser.showMessage(this,
                    R.string.activity_invoice_detail_invoice_cannot_be_showed,
                    Toast.LENGTH_LONG, Level.SEVERE);
        }
        TextView invoiceNumber = (TextView) findViewById(R.id.activity_invoice_detail_number);
        if (invoice.getNumber() != null)
            invoiceNumber.setText(getResources().getString(
                    R.string.activity_invoice_detail_invoice)
                    + " " + invoice.getNumber());
        else
            invoiceNumber.setText(getResources().getString(
                    R.string.activity_invoice_detail_invoice));
        TextView partnerName = (TextView) findViewById(R.id.activity_invoice_detail_partner_name);
        partnerName.setText(getResources().getString(
                R.string.activity_invoice_detail_partner)
                + " " + invoice.getPartner().getName());
        TextView dateInvoice = (TextView) findViewById(R.id.activity_invoice_detail_date);
        try {
            if (invoice.getDateInvoice() != null)
                dateInvoice.setText(getResources().getString(
                        R.string.activity_invoice_detail_date)
                        + " "
                        + DateUtil.toFormattedString(DateUtil.parseDate(
                                invoice.getDateInvoice(), "yyyy-MM-dd"),
                                "dd.MM.yyyy"));
        } catch (NotFoundException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ParseException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        TextView partnerAddress = (TextView) findViewById(R.id.activity_invoice_detail_partner_address);
        partnerAddress.setText(invoice.getPartner().getCompleteAddress());
        TableLayout invoiceLinesTable = (TableLayout) findViewById(R.id.activity_invoice_detail_lines_table);
        TextView lineQuantityHeader = (TextView) findViewById(R.id.activity_invoice_detail_line_quantity);
        lineQuantityHeader.setText(getResources().getString(
                R.string.activity_invoice_detail_quantity));
        TextView lineProductName = (TextView) findViewById(R.id.activity_invoice_detail_line_product_name);
        lineProductName.setText(getResources().getString(
                R.string.activity_invoice_detail_product));
        TextView linePriceUnit = (TextView) findViewById(R.id.activity_invoice_detail_line_price_unit);
        linePriceUnit.setText(getResources().getString(
                R.string.activity_invoice_detail_unit_price));
        for (InvoiceLine line : invoice.getLines()) {
            TableRow row = new TableRow(invoiceLinesTable.getContext());
            row.setLayoutParams(new TableLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            TextView lineQuantity = new TextView(row.getContext());
            lineQuantity.setText("" + line.getQuantity());
            lineQuantity.setLayoutParams(new TableRow.LayoutParams(0,
                    LayoutParams.WRAP_CONTENT, 1f));
            lineQuantity.setGravity(Gravity.CENTER);
            lineQuantity.setTextSize(getResources().getDimension(R.dimen.h2));
            row.addView(lineQuantity);
            TextView lineName = new TextView(row.getContext());
            lineName.setText(line.getName());
            lineName.setLayoutParams(new TableRow.LayoutParams(0,
                    LayoutParams.WRAP_CONTENT, 4f));
            lineName.setGravity(Gravity.LEFT);
            lineName.setTextSize(getResources().getDimension(R.dimen.h2));
            row.addView(lineName);
            TextView linePrice = new TextView(row.getContext());
            linePrice.setText(""
                    + new BigDecimal(line.getPriceUnit().doubleValue())
                            .setScale(2, RoundingMode.HALF_UP)
                    + getResources().getString(R.string.currency_symbol));
            linePrice.setLayoutParams(new TableRow.LayoutParams(0,
                    LayoutParams.WRAP_CONTENT, 1f));
            linePrice.setGravity(Gravity.CENTER);
            linePrice.setTextSize(getResources().getDimension(R.dimen.h2));
            row.addView(linePrice);
            invoiceLinesTable.addView(row);
        }
        TextView amountUntaxed = (TextView) findViewById(R.id.activity_invoice_detail_amount_untaxed);
        amountUntaxed.setText(getResources().getString(
                R.string.activity_invoice_detail_unit_amount_without_taxes)
                + " "
                + invoice.getAmountUntaxed()
                + getResources().getString(R.string.currency_symbol));
        TextView amountTax = (TextView) findViewById(R.id.activity_invoice_detail_amount_tax);
        amountTax.setText(getResources().getString(
                R.string.activity_invoice_detail_unit_amount_taxes)
                + " "
                + invoice.getAmountTax()
                + getResources().getString(R.string.currency_symbol));
        TextView amountTotal = (TextView) findViewById(R.id.activity_invoice_detail_amount_total);
        amountTotal.setText(getResources().getString(
                R.string.activity_invoice_detail_unit_amount_total)
                + " "
                + new BigDecimal(invoice.getAmountTotal().floatValue())
                        .setScale(2, RoundingMode.HALF_UP)
                + getResources().getString(R.string.currency_symbol));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.invoice_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        case R.id.home_item:
            startActivityForResult(
                    getNextIntent(new Bundle(), this, PortadaActivity.class), 0);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
