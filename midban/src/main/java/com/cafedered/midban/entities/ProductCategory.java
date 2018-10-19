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
import com.debortoliwines.openerp.api.FilterCollection;

@Entity(tableName = "product_category")
@Remote(object = "product.category")
public class ProductCategory extends BaseRemoteEntity {

    private static final long serialVersionUID = 4475430404464075147L;

    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

    @Property(columnName = "name")
    @RemoteProperty(name = "name")
    private String name;

    @Property(columnName = "complete_name")
    @RemoteProperty(name = "complete_name")
    private String completeName;

    @Property(columnName = "parent_id")
    @RemoteProperty(name = "parent_id",
            entityRef = ProductCategory.class,
            repositoryRef = ProductCategoryRepository.class,
            orderedProperties = { "id", "name" })
    private Number parentId;

    @Property(columnName = "parent_left")
    @RemoteProperty(name = "parent_left")
    private Number parentLeft;

    @Property(columnName = "parent_right")
    @RemoteProperty(name = "parent_right")
    private Number parentRight;

    @Property(columnName = "sequence")
    @RemoteProperty(name = "sequence")
    private Number sequence;

    @Property(columnName = "type")
    @RemoteProperty(name = "type")
    private String type;

    public ProductCategory() {

    }

    protected ProductCategory(Long id, String name, String completeName,
            Number parentLeft, Number parentRight, Number sequence,
            String type, Number parentId) {
        super();
        this.id = id;
        this.name = name;
        this.completeName = completeName;
        this.parentLeft = parentLeft;
        this.parentRight = parentRight;
        this.sequence = sequence;
        this.type = type;
        this.parentId = parentId;
    }

    public static ProductCategory create(Long id, String name,
            String completeName, Number parentLeft, Number parentRight,
            Number sequence, String type, Number parentId) {
        return new ProductCategory(id, name, completeName, parentLeft,
                parentRight, sequence, type, parentId);
    }

    public static ProductCategory create() {
        return new ProductCategory();
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

    public String getCompleteName() {
        return completeName;
    }

    public void setCompleteName(String completeName) {
        this.completeName = completeName;
    }

    public Number getParentLeft() {
        return parentLeft;
    }

    public void setParentLeft(Number parentLeft) {
        this.parentLeft = parentLeft;
    }

    public Number getParentRight() {
        return parentRight;
    }

    public void setParentRight(Number parentRight) {
        this.parentRight = parentRight;
    }

    public Number getSequence() {
        return sequence;
    }

    public void setSequence(Number sequence) {
        this.sequence = sequence;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Number getParentId() {
        return parentId;
    }

    public void setParentId(Number parentId) {
        this.parentId = parentId;
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
