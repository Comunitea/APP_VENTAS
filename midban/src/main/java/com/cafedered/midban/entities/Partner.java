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

import java.util.Date;

import com.cafedered.cafedroidlitedao.annotations.Entity;
import com.cafedered.cafedroidlitedao.annotations.Id;
import com.cafedered.cafedroidlitedao.annotations.Property;
import com.cafedered.midban.annotations.Remote;
import com.cafedered.midban.annotations.RemoteProperty;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.dao.StateDAO;
import com.cafedered.midban.service.repositories.StateRepository;
import com.cafedered.midban.utils.exceptions.ObjectArrayToStringFilter;
import com.debortoliwines.openerp.api.FilterCollection;
import com.debortoliwines.openerp.api.OpeneERPApiException;

@Entity(tableName = "res_partner")
@Remote(object = "res.partner")
public class Partner extends BaseRemoteEntity {

    private static final long serialVersionUID = -2830315448959401488L;

    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

    @Property(columnName = "pendingSynchronization")
    private Integer pendingSynchronization;

    @Property(columnName = "type")
    @RemoteProperty(name = "type")
    private String type;

    @Property(columnName = "contact_address")
    @RemoteProperty(name = "contact_address")
    private String contactAddress;

    @Property(columnName = "phone")
    @RemoteProperty(name = "phone")
    private String phone;

    @Property(columnName = "street")
    @RemoteProperty(name = "street")
    private String street;

    @Property(columnName = "function")
    @RemoteProperty(name = "function")
    private String function;

    @Property(columnName = "city")
    @RemoteProperty(name = "city")
    private String city;

    @Property(columnName = "credit_limit", type = Property.SQLType.REAL)
    @RemoteProperty(name = "credit_limit")
    private Number creditLimit;

    @Property(columnName = "total_debt", type = Property.SQLType.REAL)
    private Number totalDebt;

    @Property(columnName = "credit", type = Property.SQLType.REAL)
    @RemoteProperty(name = "credit")
    private Number credit;

    @Property(columnName = "fax")
    @RemoteProperty(name = "fax")
    private String fax;

    @Property(columnName = "category_id")
    @RemoteProperty(name = "category_id")
    private Number categoryId;

    @Property(columnName = "email")
    @RemoteProperty(name = "email")
    private String email;

    @Property(columnName = "comment")
    @RemoteProperty(name = "comment")
    private String comment;

    @Property(columnName = "mobile")
    @RemoteProperty(name = "mobile")
    private String mobile;

    @Property(columnName = "last_reconciliation_date")
//    @RemoteProperty(name = "last_reconciliation_date")
    private Date lastReconciliationDate;

    @Property(columnName = "name")
    @RemoteProperty(name = "display_name")
    private String name;

    @Property(columnName = "birthdate")
    @RemoteProperty(name = "birthdate")
    private Date birthdate;

    @Property(columnName = "debit", type = Property.SQLType.REAL)
    @RemoteProperty(name = "debit")
    private Number debit;

    @Property(columnName = "commercial_partner_id")
    @RemoteProperty(name = "commercial_partner_id")
    private Number commercialPartnerId;

    @Property(columnName = "zip")
    @RemoteProperty(name = "zip")
    private String zip;

    @Property(columnName = "website")
    @RemoteProperty(name = "website")
    private String website;

    @Property(columnName = "ref")
    @RemoteProperty(name = "ref")
    private String ref;

    @Property(columnName = "date")
    @RemoteProperty(name = "date")
    private Date date;

    @Property(columnName = "active")
    @RemoteProperty(name = "active")
    private Boolean active;

    @Property(columnName = "is_company")
    @RemoteProperty(name = "is_company")
    private Boolean isCompany;

    @Property(columnName = "use_parent_address")
    @RemoteProperty(name = "use_parent_address")
    private Boolean useParentAddress;

    @Property(columnName = "parent_id")
    @RemoteProperty(name = "parent_id")
    private Number parentId;

    @Property(columnName = "pricelist_id")
    @RemoteProperty(name = "property_product_pricelist")
    private Number pricelistId;

    @Property(columnName = "user_id")
    @RemoteProperty(name = "user_id")
    private Number userId;

    @Property(columnName = "customer_payment_mode")
    @RemoteProperty(name = "customer_payment_mode")
    private Number customerPaymentMode;

    @Property(columnName = "property_payment_term")
    @RemoteProperty(name = "property_payment_term")
    private Number propertyPaymentTerm;

    public Number getUserId() {
        return userId;
    }

    public void setUserId(Number userId) {
        this.userId = userId;
    }

    public Boolean getUseParentAddress() {
        return useParentAddress;
    }

    public void setUseParentAddress(Boolean useParentAddress) {
        this.useParentAddress = useParentAddress;
    }

    public Number getParentId() {
        return parentId;
    }

    public void setParentId(Number parentId) {
        this.parentId = parentId;
    }

    public Boolean getIsCompany() {
        return isCompany;
    }

    public void setIsCompany(Boolean isCompany) {
        this.isCompany = isCompany;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public Number getPricelistId() {
        return pricelistId;
    }

    public void setPricelistId(Number pricelistId) {
        this.pricelistId = pricelistId;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Number getTotalDebt() {
        return totalDebt;
    }

    public void setTotalDebt(Number totalDebt) {
        this.totalDebt = totalDebt;
    }

    public Number getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Number creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Number getCredit() {
        return credit;
    }

    public void setCredit(Number credit) {
        this.credit = credit;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public Number getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Number categoryId) {
        this.categoryId = categoryId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getLastReconciliationDate() {
        return lastReconciliationDate;
    }

    public void setLastReconciliationDate(Date lastReconciliationDate) {
        this.lastReconciliationDate = lastReconciliationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Number getDebit() {
        return debit;
    }

    public void setDebit(Number debit) {
        this.debit = debit;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public FilterCollection getRemoteFilters() {
        FilterCollection filters = new FilterCollection();
        try {
            filters.add("customer", "=", true);
            filters.add("is_company", "=", true);
            // DAVID - LO RETIRO filters.add("user_id", "=", ((User)MidbanApplication.getValueFromContext(ContextAttributes.LOGGED_USER)).getId());
        } catch (OpeneERPApiException e) {
            e.printStackTrace();
        }
        return filters;
    }

    public String getCompleteAddress() {
        return (getStreet() + "\n" + getCity() + " " + getZip()).replace(
                "null", "");
    }

    @Override
    public Integer getPendingSynchronization() {
        return pendingSynchronization;
    }

    @Override
    public void setPendingSynchronization(Integer pendingSynchronization) {
        this.pendingSynchronization = pendingSynchronization;
    }

    public Number getCommercialPartnerId() {
        return commercialPartnerId;
    }

    public void setCommercialPartnerId(Number commercialPartnerId) {
        this.commercialPartnerId = commercialPartnerId;
    }

    public Number getCustomerPaymentMode() {
        return customerPaymentMode;
    }

    public void setCustomerPaymentMode(Number customerPaymentMode) {
        this.customerPaymentMode = customerPaymentMode;
    }

    public Number getPropertyPaymentTerm() {
        return propertyPaymentTerm;
    }

    public void setPropertyPaymentTerm(Number propertyPaymentTerm) {
        this.propertyPaymentTerm = propertyPaymentTerm;
    }
}
