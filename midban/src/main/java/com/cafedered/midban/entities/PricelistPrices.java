package com.cafedered.midban.entities;

import com.cafedered.cafedroidlitedao.annotations.Entity;
import com.cafedered.cafedroidlitedao.annotations.Id;
import com.cafedered.cafedroidlitedao.annotations.Property;
import com.cafedered.midban.annotations.Remote;
import com.cafedered.midban.annotations.RemoteProperty;
import com.debortoliwines.openerp.api.FilterCollection;

@Entity(tableName = "table_pricelist_prices")
@Remote(object = "table.pricelist.prices")

public class PricelistPrices extends BaseRemoteEntity {
    private static final long serialVersionUID = 2325116999511804028L;

    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

   // @Property(columnName = "display_name")
   // @RemoteProperty(name = "display_name")
    private String displayName;

    @Property(columnName = "product_id")
    @RemoteProperty(name = "product_id")
    private Number productId;

    @Property(columnName = "price")
    @RemoteProperty(name = "price")
    private Number price;

    @Property(columnName = "company_id")
    @RemoteProperty(name = "company_id")
    private Number companyId;

    @Property(columnName = "pricelist_id")
    @RemoteProperty(name = "pricelist_id")
    private Number pricelistId;


    @Override
    public Integer getPendingSynchronization() {
        return null;
    }

    @Override
    public void setPendingSynchronization(Integer pendingSynchronization) {

    }

    @Override
    public FilterCollection getRemoteFilters() {
        return null;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Number getProductId() {
        return productId;
    }

    public void setProductId(Number productId) {
        this.productId = productId;
    }

    public Number getPrice() {
        return price;
    }

    public void setPrice(Number price) {
        this.price = price;
    }

    public Number getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Number companyId) {
        this.companyId = companyId;
    }

    public Number getPricelistId() {
        return pricelistId;
    }

    public void setPricelistId(Number pricelistId) {
        this.pricelistId = pricelistId;
    }
}
