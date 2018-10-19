package com.cafedered.midban.entities;

import com.cafedered.cafedroidlitedao.annotations.Entity;
import com.cafedered.cafedroidlitedao.annotations.Id;
import com.cafedered.cafedroidlitedao.annotations.Property;
import com.debortoliwines.openerp.api.FilterCollection;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "product_partner_price")
public class ProductPartnerPrice extends BaseRemoteEntity {

    private static final long serialVersionUID = 139997522872094875L;

    @Id(autoIncrement = true, column = "id")
    private Long id;

    @Property(columnName = "product_id", type = Property.SQLType.INTEGER)
    private Number productId;

    @Property(columnName = "partner_id", type = Property.SQLType.INTEGER)
    private Number partnerId;

    @Property(columnName = "price", type = Property.SQLType.REAL)
    private Number price;

    @Property(columnName = "dateCalculated")
    private String dateCalculated;

    public ProductPartnerPrice() {
        super();
        dateCalculated = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Number getProductId() {
        return productId;
    }

    public void setProductId(Number productId) {
        this.productId = productId;
    }

    public Number getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Number partnerId) {
        this.partnerId = partnerId;
    }

    public Number getPrice() {
        return price;
    }

    public void setPrice(Number price) {
        this.price = price;
    }

    public String getDateCalculated() {
        return dateCalculated;
    }

    public void setDateCalculated(String dateCalculated) {
        this.dateCalculated = dateCalculated;
    }

    @Override
    public Integer getPendingSynchronization() {
        return null;
    }

    @Override
    public void setPendingSynchronization(Integer pendingSynchronization) {

    }

    @Override
    public FilterCollection getRemoteFilters() {
        return new FilterCollection();
    }
}
