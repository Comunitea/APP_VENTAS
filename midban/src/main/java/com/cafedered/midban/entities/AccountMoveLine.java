package com.cafedered.midban.entities;

/**
 * Created by nacho on 27/11/15.
 */
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

import com.cafedered.cafedroidlitedao.annotations.Entity;
import com.cafedered.cafedroidlitedao.annotations.Id;
import com.cafedered.cafedroidlitedao.annotations.Property;
import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.annotations.Remote;
import com.cafedered.midban.annotations.RemoteProperty;
import com.cafedered.midban.service.repositories.AccountJournalRepository;
import com.debortoliwines.openerp.api.FilterCollection;
import com.debortoliwines.openerp.api.OpeneERPApiException;

@Entity(tableName = "account_move_line")
@Remote(object = "account.move.line")
public class AccountMoveLine extends BaseRemoteEntity {

    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

    @Property(columnName = "account_id")
    @RemoteProperty(name = "account_id")
    private Number accountId;

    @Property(columnName = "reconcile_id")
    @RemoteProperty(name = "reconcile_id")
    private Boolean reconcilId;

    @Property(columnName = "ref")
    @RemoteProperty(name = "ref")
    private String ref;

    @Property(columnName = "date_maturity")
    @RemoteProperty(name = "date_maturity")
    private String dateMaturity;

    @Property(columnName = "partner_id")
    @RemoteProperty(name = "partner_id")
    private Number partnerId;

    @Property(columnName = "amount_residual", type = Property.SQLType.REAL)
    @RemoteProperty(name = "amount_residual")
    private Number amountResidual;

    @Property(columnName = "payment_made")
    private Number paymentMade;
    @Property(columnName = "payment_made_value", type = Property.SQLType.REAL)
    private Number paymentMadeValue;
    @Property(columnName = "payment_made_date")
    private String paymentMadeDate;

    FilterCollection filters = new FilterCollection();

    public String getPaymentMadeDate() {
        return paymentMadeDate;
    }

    public void setPaymentMadeDate(String paymentMadeDate) {
        this.paymentMadeDate = paymentMadeDate;
    }

    public Number getPaymentMadeValue() {
        return paymentMadeValue;
    }

    public void setPaymentMadeValue(Number paymentMadeValue) {
        this.paymentMadeValue = paymentMadeValue;
    }

    public Number getPaymentMade() {
        return paymentMade;
    }

    public void setPaymentMade(Number paymentMade) {
        this.paymentMade = paymentMade;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getDateMaturity() {
        return dateMaturity;
    }

    public void setDateMaturity(String dateMaturity) {
        this.dateMaturity = dateMaturity;
    }

    public Number getAccountId() {
        return accountId;
    }

    public void setAccountId(Number accountId) {
        this.accountId = accountId;
    }

    public Boolean getReconcilId() {
        return reconcilId;
    }

    public void setReconcilId(Boolean reconcilId) {
        this.reconcilId = reconcilId;
    }

    public Number getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Number partnerId) {
        this.partnerId = partnerId;
    }

    public Number getAmountResidual() {
        return amountResidual;
    }

    public void setAmountResidual(Number amountResidual) {
        this.amountResidual = amountResidual;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public FilterCollection getRemoteFilters() {
        try {
            filters.add("reconcile_id", "is null", null);
            filters.add("debit", ">" ,0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filters;
    }

    @Override
    public Integer getPendingSynchronization() {
        // not needed
        return null;
    }

    @Override
    public void setPendingSynchronization(Integer pendingSynchronization) {
        // not needed

    }

    public FilterCollection getFilters() {
        return filters;
    }

    public void setFilters(FilterCollection filters) {
        this.filters = filters;
    }

    @Override
    public String toString() {
        return getAmountResidual().toString();
    }

}

