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

@Entity(tableName = "product_uom")
@Remote(object = "product.uom")
public class ProductUom extends BaseRemoteEntity {

    private static final long serialVersionUID = -3820418892351702742L;

    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

    @Property(columnName = "name")
    @RemoteProperty(name = "name")
    private String name;

    @Property(columnName = "category_id")
    @RemoteProperty(name = "category_id")
    private Long categoryId;

    @Property(columnName = "factor")
    @RemoteProperty(name = "factor")
    private Number factor;

    @Property(columnName = "factor_inv")
    @RemoteProperty(name = "factor_inv")
    private Number factor_inv;

    @Property(columnName = "uom_type")
    @RemoteProperty(name = "uom_type")
    private String uom_type;
    


    @Override
    public Integer getPendingSynchronization() {
        // not needed
        return null;
    }

    @Override
    public void setPendingSynchronization(Integer pendingSynchronization) {
        // not needed
    }

    @Override
    public FilterCollection getRemoteFilters() {
        // not needed
        return null;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Number getFactor() {
        return factor;
    }

    public void setFactor(Number factor) {
        this.factor = factor;
    }

    public Number getFactor_inv() {
        return factor_inv;
    }

    public void setFactor_inv(Number factor_inv) {
        this.factor_inv = factor_inv;
    }

    public String getUom_type() {
        return uom_type;
    }

    public void setUom_type(String uom_type) {
        this.uom_type = uom_type;
    }
}
