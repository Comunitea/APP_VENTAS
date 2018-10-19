package com.cafedered.midban.entities;

import com.cafedered.cafedroidlitedao.annotations.Entity;
import com.cafedered.cafedroidlitedao.annotations.Id;
import com.cafedered.cafedroidlitedao.annotations.Property;
import com.cafedered.midban.annotations.Remote;
import com.cafedered.midban.annotations.RemoteProperty;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.debortoliwines.openerp.api.FilterCollection;
import com.debortoliwines.openerp.api.OpeneERPApiException;

/**
 * Created by nacho on 28/05/15.
 */
@Entity(tableName = "route")
@Remote(object = "route")
public class Route extends BaseRemoteEntity {

    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

    @Property(columnName = "name")
    @RemoteProperty(name = "name")
    private String name;

    @Property(columnName = "code")
    @RemoteProperty(name = "code")
    private String code;

    @Property(columnName = "comercial_id")
    @RemoteProperty(name = "comercial_id")
    private Number comercialId;

    @Property(columnName = "day_id")
    private Number dayId;

    @Property(columnName = "partner_ids")
    private String partners;

    public Number getDayId() {
        return dayId;
    }

    public void setDayId(Number dayId) {
        this.dayId = dayId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Number getComercialId() {
        return comercialId;
    }

    public void setComercialId(Number comercialId) {
        this.comercialId = comercialId;
    }

    public String getPartners() {
        return partners;
    }

    public void setPartners(String partners) {
        this.partners = partners;
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
        FilterCollection filters = new FilterCollection();
        try {
            filters.add("type", "=", "comercial");
            filters.add("comercial_id", "=", ((User)MidbanApplication.getValueFromContext(ContextAttributes.LOGGED_USER)).getId());
        } catch (OpeneERPApiException e) {
            e.printStackTrace();
        }
        return filters;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
