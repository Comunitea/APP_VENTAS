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

@SuppressWarnings("serial")
@Entity(tableName = "appusers")
@Remote(object = "res.users")
public class User extends BaseRemoteEntity {
    @Id(column = "id", autoIncrement = false)
    @RemoteProperty(name = "id")
    private Long id;
    @RemoteProperty(name = "login")
    @Property(columnName = "login")
    private String login;
    @Property(columnName = "passwd")
    private String passwd;
    //@Property(columnName = "route_ids")
    private String routeIds;
    @Property(columnName = "fecha_login")
    private String fechaLogin;


    @RemoteProperty(name = "company_id")
    @Property(columnName = "company_id")
    private Number companyId;


    public static User create(String login, String password, Integer idUsuario) {
        return new User(login, password, idUsuario);
    }

    public User() {

    }

    protected User(String login, String password, Integer idUsuario) {
        super();
        this.login = login;
        this.passwd = password;
        if (idUsuario != null)
            this.id = idUsuario.longValue();
    }


    public String getRouteIds() {
        return routeIds;
    }

    public void setRouteIds(String routeIds) {
        this.routeIds = routeIds;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String password) {
        this.passwd = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Number getCompanyId() { return companyId; }

    public void setCompanyId(Number companyId) { this.companyId = companyId; }

    @Override
    public FilterCollection getRemoteFilters() {
        return null;
    }

    @Override
    public Integer getPendingSynchronization() {
        return this.pendingSynchronization;
    }

    @Override
    public void setPendingSynchronization(Integer pendingSynchronization) {
        this.pendingSynchronization = pendingSynchronization;
    }

    public String getFechaLogin() {
        return fechaLogin;
    }

    public void setFechaLogin(String fechaLogin) {
        this.fechaLogin = fechaLogin;
    }
}
