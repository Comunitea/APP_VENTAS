package com.cafedered.midban.entities;

import com.cafedered.cafedroidlitedao.annotations.Entity;
import com.cafedered.cafedroidlitedao.annotations.Id;
import com.cafedered.cafedroidlitedao.annotations.Property;
import com.cafedered.midban.annotations.Remote;
import com.cafedered.midban.annotations.RemoteProperty;
import com.debortoliwines.openerp.api.FilterCollection;

/**
 * Created by nacho on 4/11/15.
 */
@Entity(tableName = "customer_list")
@Remote(object = "customer.list")
public class CustomerList extends BaseRemoteEntity {
    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

    @Property(columnName = "result")
    @RemoteProperty(name = "result")
    private String result;

    @Property(columnName = "customer_id")
    @RemoteProperty(name = "customer_id")
    private Number customerId;

    @Property(columnName = "detail_id")
    @RemoteProperty(name = "detail_id")
    private Number detailId;

    @Property(columnName = "pending_synchroization")
    private Integer pendingSynchronization;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Number getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Number customerId) {
        this.customerId = customerId;
    }

    public Number getDetailId() {
        return detailId;
    }

    public void setDetailId(Number detailId) {
        this.detailId = detailId;
    }

    @Override
    public Integer getPendingSynchronization() {
        return pendingSynchronization;
    }

    @Override
    public void setPendingSynchronization(Integer pendingSynchronization) {
        this.pendingSynchronization = pendingSynchronization;
    }

    @Override
    public FilterCollection getRemoteFilters() {
        return new FilterCollection();
    }
}
