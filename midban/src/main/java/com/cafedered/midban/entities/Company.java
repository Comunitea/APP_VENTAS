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
import com.cafedered.midban.dao.StateDAO;
import com.cafedered.midban.service.repositories.StateRepository;
import com.debortoliwines.openerp.api.FilterCollection;

@Entity(tableName = "res_company")
@Remote(object = "res.company")
public class Company extends BaseRemoteEntity {

    private static final long serialVersionUID = -53578618695587162L;

    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;
    @Property(columnName = "pendingSynchronization")
    private Integer pendingSynchronization;
    @Property(columnName = "city")
    @RemoteProperty(name = "city")
    private String city;
    @Property(columnName = "name")
    @RemoteProperty(name = "name")
    private String name;
    @Property(columnName = "fax")
    @RemoteProperty(name = "fax")
    private String fax;
    @Property(columnName = "phone")
    @RemoteProperty(name = "phone")
    private String phone;
    @Property(columnName = "street")
    @RemoteProperty(name = "street")
    private String street;
    @Property(columnName = "website")
    @RemoteProperty(name = "website")
    private String website;
    @Property(columnName = "email")
    @RemoteProperty(name = "email")
    private String email;
    @Property(columnName = "state_id")
    @RemoteProperty(name = "state_id",
            entityRef = State.class,
            repositoryRef = StateRepository.class,
            orderedProperties = { "id", "name" })
    private String stateId;
    @Property(columnName = "zip")
    @RemoteProperty(name = "zip")
    private String zip;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
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

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

}
