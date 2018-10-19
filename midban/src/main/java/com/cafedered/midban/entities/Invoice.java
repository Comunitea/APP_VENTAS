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
package com.cafedered.midban.entities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.cafedered.cafedroidlitedao.annotations.Entity;
import com.cafedered.cafedroidlitedao.annotations.Id;
import com.cafedered.cafedroidlitedao.annotations.Property;
import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.annotations.Remote;
import com.cafedered.midban.annotations.RemoteProperty;
import com.cafedered.midban.service.repositories.InvoiceLineRepository;
import com.cafedered.midban.service.repositories.PartnerRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.debortoliwines.openerp.api.FilterCollection;

@Entity(tableName = "account_invoice")
@Remote(object = "account.invoice")
public class Invoice extends BaseRemoteEntity {

    private static final long serialVersionUID = 2414748236849450115L;

    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

    @Property(columnName = "date_invoice")
    @RemoteProperty(name = "date_invoice")
    private String dateInvoice;

    @Property(columnName = "date_due")
    @RemoteProperty(name = "date_due")
    private String dateDue;

    @Property(columnName = "number")
    @RemoteProperty(name = "number")
    private String number;

    @Property(columnName = "partner_id")
    @RemoteProperty(name = "partner_id")
    private Number partnerId;

    @Property(columnName = "period_id")
    @RemoteProperty(name = "period_id")
    private Number periodId;

    @Property(columnName = "account_id")
    @RemoteProperty(name = "account_id")
    private Number accountId;

    @Property(columnName = "state")
    @RemoteProperty(name = "state")
    private String state;

    @Property(columnName = "amount_untaxed", type = Property.SQLType.REAL)
    @RemoteProperty(name = "amount_untaxed")
    private Number amountUntaxed;

    @Property(columnName = "amount_tax", type = Property.SQLType.REAL)
    @RemoteProperty(name = "amount_tax")
    private Number amountTax;

    @Property(columnName = "amount_total", type = Property.SQLType.REAL)
    @RemoteProperty(name = "amount_total")
    private Number amountTotal;

    @Property(columnName = "residual", type = Property.SQLType.REAL)
    @RemoteProperty(name = "residual")
    private Number residual;

    @Property(columnName = "comment")
    @RemoteProperty(name = "comment")
    private String comment;

    @Property(columnName = "currency_id")
    @RemoteProperty(name = "currency_id")
    private Long currencyId;

    private Partner partner;
    private List<InvoiceLine> lines;

    public Number getResidual() {
        return residual;
    }

    public void setResidual(Number residual) {
        this.residual = residual;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDateInvoice() {
        return dateInvoice;
    }

    public void setDateInvoice(String dateInvoice) {
        this.dateInvoice = dateInvoice;
    }

    public Number getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Number periodId) {
        this.periodId = periodId;
    }

    public Number getAccountId() {
        return accountId;
    }

    public void setAccountId(Number accountId) {
        this.accountId = accountId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Number getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Number partnerId) {
        this.partnerId = partnerId;
    }

    public Partner getPartner() {
        try {
            if (this.partner == null) // retrieve just first time
                partner = PartnerRepository.getInstance().getById(
                        partnerId.longValue());
            return partner;
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        return new Partner();
    }

    public List<InvoiceLine> getLines() {
        if (this.lines == null) {
            InvoiceLine example = new InvoiceLine();
            example.setInvoiceId(id);
            try {
                lines = InvoiceLineRepository.getInstance().getByExample(
                        example, Restriction.AND, true, 0, 100000);
            } catch (ServiceException e) {
                if (LoggerUtil.isDebugEnabled())
                    e.printStackTrace();
            }
        }
        return lines;
    }

    public Number getAmountUntaxed() {
        return amountUntaxed;
    }

    public void setAmountUntaxed(Number amountUntaxed) {
        this.amountUntaxed = amountUntaxed;
    }

    public Number getAmountTax() {
        return amountTax;
    }

    public void setAmountTax(Number amountTax) {
        this.amountTax = amountTax;
    }

    public Number getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(Number amountTotal) {
        this.amountTotal = amountTotal;
    }

    @Override
    public FilterCollection getRemoteFilters() {
        FilterCollection filters = new FilterCollection();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -90);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            filters.add("date_invoice", ">", formatter.format(calendar.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filters;
    }

    @Override
    public Integer getPendingSynchronization() {
        return null;
    }

    @Override
    public void setPendingSynchronization(Integer pendingSynchronization) {

    }

    public String getDateDue() {
        return dateDue;
    }

    public void setDateDue(String dateDue) {
        this.dateDue = dateDue;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

}
