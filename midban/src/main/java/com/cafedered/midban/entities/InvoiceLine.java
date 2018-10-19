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

import com.cafedered.cafedroidlitedao.annotations.Entity;
import com.cafedered.cafedroidlitedao.annotations.Id;
import com.cafedered.cafedroidlitedao.annotations.Property;
import com.cafedered.midban.annotations.Remote;
import com.cafedered.midban.annotations.RemoteProperty;
import com.debortoliwines.openerp.api.FilterCollection;

@Entity(tableName = "account_invoice_line")
@Remote(object = "account.invoice.line")
public class InvoiceLine extends BaseRemoteEntity {

    private static final long serialVersionUID = 5952616909170077797L;

    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

    @Property(columnName = "product_id")
    @RemoteProperty(name = "product_id")
    private Number productId;

    @Property(columnName = "name")
    @RemoteProperty(name = "name")
    private String name;

    @Property(columnName = "quantity")
    @RemoteProperty(name = "quantity")
    private Number quantity;

    @Property(columnName = "price_unit")
    @RemoteProperty(name = "price_unit")
    private Number priceUnit;

    @Property(columnName = "discount")
    @RemoteProperty(name = "discount")
    private Number discount;

    @Property(columnName = "invoice_id")
    @RemoteProperty(name = "invoice_id")
    private Number invoiceId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Number getProductId() {
        return productId;
    }

    public void setProductId(Number productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Number getQuantity() {
        return quantity;
    }

    public void setQuantity(Number quantity) {
        this.quantity = quantity;
    }

    public Number getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(Number priceUnit) {
        this.priceUnit = priceUnit;
    }

    public Number getDiscount() {
        return discount;
    }

    public void setDiscount(Number discount) {
        this.discount = discount;
    }

    public Number getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Number invoiceId) {
        this.invoiceId = invoiceId;
    }

    FilterCollection filters = new FilterCollection();

    @Override
    public FilterCollection getRemoteFilters() {
        return filters;
    }

    @Override
    public Integer getPendingSynchronization() {
        return null;
    }

    @Override
    public void setPendingSynchronization(Integer pendingSynchronization) {

    }

}
