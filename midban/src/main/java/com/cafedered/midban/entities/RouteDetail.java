package com.cafedered.midban.entities;

import com.cafedered.cafedroidlitedao.annotations.Entity;
import com.cafedered.cafedroidlitedao.annotations.Id;
import com.cafedered.cafedroidlitedao.annotations.Property;
import com.cafedered.midban.annotations.Remote;
import com.cafedered.midban.annotations.RemoteProperty;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.utils.DateUtil;
import com.debortoliwines.openerp.api.FilterCollection;
import com.debortoliwines.openerp.api.OpeneERPApiException;

import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "route_detail")
@Remote(object = "route.detail")
public class RouteDetail extends BaseRemoteEntity{

    private static final long serialVersionUID = 4135643563737121447L;

    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

    @Property(columnName = "date")
    @RemoteProperty(name = "date")
    private String date;

    @Property(columnName = "route_id")
    @RemoteProperty(name = "route_id")
    private Number routeId;

    @Property(columnName = "customers")
    private String customers;

    public String getCustomers() {
        return customers;
    }

    public void setCustomers(String customers) {
        this.customers = customers;
    }

    public Number getRouteId() {
        return routeId;
    }

    public void setRouteId(Number routeId) {
        this.routeId = routeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public FilterCollection getRemoteFilters() {
        FilterCollection filters = new FilterCollection();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -9);
        try {
            filters.add("date", ">=", DateUtil.toFormattedString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss"));
            filters.add("comercial_id", "=", ((User) MidbanApplication.getValueFromContext(ContextAttributes.LOGGED_USER)).getId());
        } catch (OpeneERPApiException e) {
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
}
