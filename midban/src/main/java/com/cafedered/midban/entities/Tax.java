package com.cafedered.midban.entities;

import com.cafedered.cafedroidlitedao.annotations.Entity;
import com.cafedered.cafedroidlitedao.annotations.Id;
import com.cafedered.cafedroidlitedao.annotations.Property;
import com.cafedered.midban.annotations.Remote;
import com.cafedered.midban.annotations.RemoteProperty;
import com.debortoliwines.openerp.api.FilterCollection;

@Entity(tableName = "tax")
@Remote(object = "account.tax")
public class Tax extends BaseRemoteEntity{

    private static final long serialVersionUID = 4135643563737121447L;

    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

    @Property(columnName = "name")
    @RemoteProperty(name = "name")
    private String name;

    @Property(columnName = "description")
    @RemoteProperty(name = "description")
    private String description;

    @Property(columnName = "type")
    @RemoteProperty(name = "type")
    private String type;

    @Property(columnName = "amount")
    @RemoteProperty(name = "amount")
    private Number amount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Number getAmount() {
        return amount;
    }

    public void setAmount(Number amount) {
        this.amount = amount;
    }

    @Override
    public FilterCollection getRemoteFilters() {
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
