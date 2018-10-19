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

@Entity(tableName = "synchronizations")
public class Synchronization extends BaseEntity {

    private static final long serialVersionUID = 4812034992153127116L;

    @Id(column = "id", autoIncrement = true)
    private Long id;
    @Property(columnName = "class_name")
    private String className;
    @Property(columnName = "date")
    private String date;
    @Property(columnName = "num_elements")
    private Integer numElements;
    @Property(columnName = "duration")
    private Long duration;
    @Property(columnName = "error")
    private String error;

    public static Synchronization create(String className, String date,
            Integer numElements, Long duration, String error) {
        Synchronization synchro = new Synchronization();
        synchro.setClassName(className);
        synchro.setDate(date);
        synchro.setNumElements(numElements);
        synchro.setDuration(duration);
        synchro.setError(error);
        return synchro;
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getNumElements() {
        return numElements;
    }

    public void setNumElements(Integer numElements) {
        this.numElements = numElements;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
