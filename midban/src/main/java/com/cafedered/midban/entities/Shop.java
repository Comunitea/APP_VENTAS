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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.cafedered.cafedroidlitedao.annotations.Entity;
import com.cafedered.cafedroidlitedao.annotations.Id;
import com.cafedered.cafedroidlitedao.annotations.Property;
import com.cafedered.midban.annotations.Remote;
import com.cafedered.midban.annotations.RemoteProperty;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.service.repositories.OrderLineRepository;
import com.cafedered.midban.service.repositories.PartnerRepository;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.debortoliwines.openerp.api.FilterCollection;
import com.debortoliwines.openerp.api.OpeneERPApiException;

@Entity(tableName = "sale_shop")
@Remote(object = "sale.shop")
public class Shop extends BaseRemoteEntity implements Comparable<Order> {

    private static final long serialVersionUID = 2282169925763946165L;
    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

    @Property(columnName = "name")
    @RemoteProperty(name = "name")
    private String name;

    @Property(columnName = "indirect_invoicing")
    @RemoteProperty(name = "indirect_invoicing")
    private Boolean indirectInvoicing;

    @Property(columnName = "pricelist_id")
    @RemoteProperty(name = "pricelist_id")
    private Number pricelistId;

    @Property(columnName = "sequence")
    @RemoteProperty(name = "sequence")
    private Number sequence;


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

    @Override
    public int compareTo(Order another) {
        return 0;
    }


    public Boolean getIndirectInvoicing() {
        return indirectInvoicing;
    }

    public void setIndirectInvoicing(Boolean indirectInvoicing) {
        this.indirectInvoicing = indirectInvoicing;
    }

    public Number getPricelistId() {
        return pricelistId;
    }

    public void setPricelistId(Number pricelistId) {
        this.pricelistId = pricelistId;
    }

    public Number getSequence() {
        return sequence;
    }

    public void setSequence(Number sequence) {
        this.sequence = sequence;
    }
}
