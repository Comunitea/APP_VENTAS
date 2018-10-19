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

@Entity(tableName = "stock_move")
@Remote(object = "stock.move")
public class StockMove extends BaseRemoteEntity {

    private static final long serialVersionUID = 735678215228141057L;

    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

    @Property(columnName = "product_id")
    @RemoteProperty(name = "product_id")
    private Number productId;

    @Property(columnName = "product_qty")
    @RemoteProperty(name = "product_qty")
    private Number productQty;

    @Property(columnName = "product_uos_qty")
    @RemoteProperty(name = "product_uos_qty")
    private Number productUosQty;

    @Property(columnName = "name")
    @RemoteProperty(name = "name")
    private String name;

    @Property(columnName = "company_id")
    @RemoteProperty(name = "company_id")
    private Number companyId;

    @Property(columnName = "location_dest_id")
    @RemoteProperty(name = "location_dest_id")
    private Number locationDestId;

    @Property(columnName = "partner_id")
    @RemoteProperty(name = "partner_id")
    private Number partnerId;

    @Property(columnName = "date_expected")
    @RemoteProperty(name = "date_expected")
    private String dateExpected;

    @Property(columnName = "purchase_line_id")
    @RemoteProperty(name = "purchase_line_id")
    private Number purchaseLineId;

    @Property(columnName = "picking_id")
    @RemoteProperty(name = "picking_id")
    private Number pickingId;

    @Property(columnName = "type")
    @RemoteProperty(name = "type")
    private String type;

    @Property(columnName = "location_id")
    @RemoteProperty(name = "location_id")
    private Number locationId;

    @Property(columnName = "create_date")
    @RemoteProperty(name = "create_date")
    private String createDate;

    @Property(columnName = "tracking_id")
    @RemoteProperty(name = "tracking_id")
    private Number trackingId;

    @Property(columnName = "prodlot_id")
    @RemoteProperty(name = "prodlot_id")
    private Number prodlotId;

    @Property(columnName = "sale_line_id")
    @RemoteProperty(name = "sale_line_id")
    private Number saleLineId;

    public Number getSaleLineId() {
        return saleLineId;
    }

    public void setSaleLineId(Number saleLineId) {
        this.saleLineId = saleLineId;
    }

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

    public Number getProductQty() {
        return productQty;
    }

    public void setProductQty(Number productQty) {
        this.productQty = productQty;
    }

    public Number getProductUosQty() {
        return productUosQty;
    }

    public void setProductUosQty(Number productUosQty) {
        this.productUosQty = productUosQty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Number getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Number companyId) {
        this.companyId = companyId;
    }

    public Number getLocationDestId() {
        return locationDestId;
    }

    public void setLocationDestId(Number locationDestId) {
        this.locationDestId = locationDestId;
    }

    public Number getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Number partnerId) {
        this.partnerId = partnerId;
    }

    public String getDateExpected() {
        return dateExpected;
    }

    public void setDateExpected(String dateExpected) {
        this.dateExpected = dateExpected;
    }

    public Number getPurchaseLineId() {
        return purchaseLineId;
    }

    public void setPurchaseLineId(Number purchaseLineId) {
        this.purchaseLineId = purchaseLineId;
    }

    public Number getPickingId() {
        return pickingId;
    }

    public void setPickingId(Number pickingId) {
        this.pickingId = pickingId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Number getLocationId() {
        return locationId;
    }

    public void setLocationId(Number locationId) {
        this.locationId = locationId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Number getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(Number trackingId) {
        this.trackingId = trackingId;
    }

    public Number getProdlotId() {
        return prodlotId;
    }

    public void setProdlotId(Number prodlotId) {
        this.prodlotId = prodlotId;
    }

    @Override
    public FilterCollection getRemoteFilters() {
        //FIXME picking.type.id --> stock_picking_type.code = outgoing; stock move de salida.
        return null;
    }

    @Override
    public Integer getPendingSynchronization() {
        return null;
    }

    @Override
    public void setPendingSynchronization(Integer pendingSynchronization) {

    }

}
