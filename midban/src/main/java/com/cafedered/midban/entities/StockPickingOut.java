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

import java.util.List;

import com.cafedered.cafedroidlitedao.annotations.Entity;
import com.cafedered.cafedroidlitedao.annotations.Id;
import com.cafedered.cafedroidlitedao.annotations.Property;
import com.cafedered.midban.annotations.Remote;
import com.cafedered.midban.annotations.RemoteProperty;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.dao.PickingTypeDAO;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.service.repositories.PickingTypeRepository;
import com.cafedered.midban.service.repositories.StockMoveRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.debortoliwines.openerp.api.FilterCollection;
import com.debortoliwines.openerp.api.OpeneERPApiException;

@Entity(tableName = "stock_picking_out")
@Remote(object = "stock.picking")
public class StockPickingOut extends BaseRemoteEntity {

    private static final long serialVersionUID = -5648731922438940291L;

    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

    @Property(columnName = "partner_id")
    @RemoteProperty(name = "partner_id")
    private Number partnerId;

    @Property(columnName = "picking_type_id")
    @RemoteProperty(name = "picking_type_id",
            entityRef = PickingType.class,
            repositoryRef = PickingTypeRepository.class,
            orderedProperties = { "id", "code" })
    private Number pickingTypeId;

    // Diario de existencias
    @Property(columnName = "stock_journal_id")
    @RemoteProperty(name = "stock_journal_id")
    private Number stockJournalId;

    @Property(columnName = "date")
    @RemoteProperty(name = "date")
    private String date;

    // Tiempo programado
    @Property(columnName = "min_date")
    @RemoteProperty(name = "min_date")
    private String minDate;

    // Documento origen
    @Property(columnName = "origin")
    @RemoteProperty(name = "origin")
    private String origin;

    @Property(columnName = "sale_id")
    @RemoteProperty(name = "sale_id")
    private Number saleId;

    @Property(columnName = "note")
    @RemoteProperty(name = "note")
    private String note;

    public Number getSaleId() {
        return saleId;
    }

    public void setSaleId(Number saleId) {
        this.saleId = saleId;
    }

    public Number getPickingTypeId() {
        return pickingTypeId;
    }

    public void setPickingTypeId(Number pickingTypeId) {
        this.pickingTypeId = pickingTypeId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Number getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Number partnerId) {
        this.partnerId = partnerId;
    }

    public Number getStockJournalId() {
        return stockJournalId;
    }

    public void setStockJournalId(Number stockJournalId) {
        this.stockJournalId = stockJournalId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMinDate() {
        return minDate;
    }

    public void setMinDate(String minDate) {
        this.minDate = minDate;
    }

    public String getOrigin() {
        return origin;
    }

    public List<StockMove> getLines() {
        return StockMoveRepository.getInstance()
                .getByStockPickingOutId(getId());
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Order getRelatedOrder() {

        try {
            return OrderRepository.getInstance().getById(
                    getSaleId().longValue());
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (Exception e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        return null;
    }

    @Override
    public FilterCollection getRemoteFilters() {
        FilterCollection filters = new FilterCollection();
        try {
            filters.add("user_id", "=", ((User) MidbanApplication.getValueFromContext(ContextAttributes.LOGGED_USER)).getId());
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
