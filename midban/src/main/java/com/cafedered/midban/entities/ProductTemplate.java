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
import com.cafedered.midban.dao.ProductCategoryDAO;
import com.cafedered.midban.service.repositories.ProductCategoryRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.debortoliwines.openerp.api.FilterCollection;

@Entity(tableName = "product_template")
@Remote(object = "product.template")
public class ProductTemplate extends BaseRemoteEntity {

    private static final long serialVersionUID = 5550250718714727725L;

    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

    @Property(columnName = "name")
    @RemoteProperty(name = "name")
    private String name;

    @Property(columnName = "description")
    @RemoteProperty(name = "description")
    private String description;

    @Property(columnName = "categ_id")
    @RemoteProperty(name = "categ_id",
            entityRef = ProductCategory.class,
            repositoryRef = ProductCategoryRepository.class,
            orderedProperties = { "id", "name" })
    private Number categId;

    @Property(columnName = "taxes_id")
    @RemoteProperty(name = "taxes_id")
    private Number taxesId;

    public Number getTaxesId() {
        return taxesId;
    }

    public void setTaxesId(Number taxesId) {
        this.taxesId = taxesId;
    }

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

    public Number getCategId() {
        return categId;
    }

    public void setCategId(Number categId) {
        this.categId = categId;
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

    public ProductCategory getProductCategory() {
        try {
            if (this.categId != null)
                return ProductCategoryRepository.getInstance().getById(
                    this.categId.longValue());
            else return null;
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        return null;
    }
}
