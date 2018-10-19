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
package com.cafedered.midban.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.cafedered.cafedroidlitedao.annotations.Entity;
import com.cafedered.cafedroidlitedao.exceptions.BadConfigurationException;
import com.cafedered.cafedroidlitedao.exceptions.DatabaseException;
import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.entities.BaseEntity;
import com.cafedered.midban.utils.ReflectionUtils;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ReflectionException;

public class BaseDAO<E extends BaseEntity> {

    private ContextDAO daoHelper;

    public ContextDAO getDaoHelper() {
        return daoHelper;
    }

    public void setDaoHelper(ContextDAO daoHelper) {
        this.daoHelper = daoHelper;
    }

    public BaseDAO() {
        try {
            daoHelper = ContextDAO.getInstance();
        } catch (BadConfigurationException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public String getTableName() throws ReflectionException {
        E e;
        e = (E) ReflectionUtils
                .createObject((Class) ((ParameterizedType) getClass()
                        .getGenericSuperclass()).getActualTypeArguments()[0]);
        return e.getClass().getAnnotation(Entity.class).tableName();
    }

    @SuppressWarnings("unchecked")
    public Integer getNextIdNumber() {
        E entity = null;
        try {
            entity = (E) ReflectionUtils
                    .getObjectParametrizedTypeInstance(ReflectionUtils
                            .getInstanceType(this));
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
        String query = "SELECT MAX(id) AS id FROM "
                + entity.getClass().getAnnotation(Entity.class).tableName();
        Cursor cursor = getDaoHelper().getReadableDatabase().rawQuery(query,
                null);
        cursor.moveToFirst();
        // FIXME return correct id
        return cursor.getInt(cursor.getColumnIndex("id")) + 1;
    }

    public Integer getCount() {
        E entity = null;
        try {
            entity = (E) ReflectionUtils
                    .getObjectParametrizedTypeInstance(ReflectionUtils
                            .getInstanceType(this));
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
        String query = "SELECT COUNT(*) FROM "
                + entity.getClass().getAnnotation(Entity.class).tableName();
        Cursor cursor = getDaoHelper().getReadableDatabase().rawQuery(query,
                null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public void delete(Long id) throws DatabaseException, ReflectionException {
        daoHelper.delete(id, ReflectionUtils
                .getObjectParametrizedTypeInstance(ReflectionUtils
                        .getInstanceType(this)));
    }

    public void saveOrUpdate(E entity) throws DatabaseException {
        daoHelper.saveOrUpdate(entity);
    }

    @SuppressWarnings("unchecked")
    public List<E> getAll(Integer numElements, Integer offset)
            throws ConfigurationException, DatabaseException {
        E entity;
        try {
            entity = (E) ReflectionUtils
                    .getObjectParametrizedTypeInstance(ReflectionUtils
                            .getInstanceType(this));
        } catch (ReflectionException e) {
            throw new ConfigurationException(
                    "Object cannot be created. Does the class have the default constructor public?",
                    e);
        }
        return (List<E>) (List<?>) daoHelper.getResults(entity,
                Restriction.VOID, false, numElements, offset);

    }

    @SuppressLint("DefaultLocale")
    public List<Object> getAllSomeField(String field)
            throws ConfigurationException, DatabaseException {
        String method = "get" + field.substring(0, 1).toUpperCase()
                + field.substring(1);
        // TODO cambiar eficiencia de este método cuando se paginen las listas
        List<E> all = getAll(0, 10000);
        List<Object> result = new ArrayList<Object>();
        for (E e : all) {
            try {
                result.add(e.getClass().getMethod(method).invoke(e));
            } catch (IllegalAccessException e1) {
                throw new ConfigurationException("Cannot access field: "
                        + field + ". Belongs this field to a BaseEntity Bean?",
                        e1);
            } catch (IllegalArgumentException e1) {
                throw new ConfigurationException("Cannot access field: "
                        + field + ". Belongs this field to a BaseEntity Bean?",
                        e1);

            } catch (InvocationTargetException e1) {
                throw new ConfigurationException("Cannot access field: "
                        + field + ". Belongs this field to a BaseEntity Bean?",
                        e1);

            } catch (NoSuchMethodException e1) {
                throw new ConfigurationException("Cannot access field: "
                        + field + ". Belongs this field to a BaseEntity Bean?",
                        e1);

            }
        }
        return result;
    }

    public Set<Object> getAllDistinctSomeField(String field)
            throws ConfigurationException, DatabaseException {
        List<Object> all = getAllSomeField(field);
        Set<Object> result = new LinkedHashSet<Object>();
        for (Object o : all) {
            if (o != null) {
                result.add(o);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public E getUniqueResult(Long id) throws ConfigurationException,
            DatabaseException {
        E entity;
        try {
            entity = (E) ReflectionUtils
                    .getObjectParametrizedTypeInstance(ReflectionUtils
                            .getInstanceType(this));
        } catch (ReflectionException e) {
            throw new ConfigurationException(
                    "Object cannot be created. Does the class have the default constructor public?",
                    e);
        }
        entity.setId(id);
        return (E) daoHelper.getUniqueResult(entity, Restriction.OR, false);

    }

    @SuppressWarnings("unchecked")
    public List<E> getByExample(E entity, Restriction restriction,
            boolean exactMatching, Integer numElements, Integer offset)
            throws DatabaseException {
        return (List<E>) (List<?>) daoHelper.getResults(entity, restriction,
                exactMatching, numElements, offset);
    }

    public int getLastSynchronizedIdNumber() {
        E entity = null;
        try {
            entity = (E) ReflectionUtils
                    .getObjectParametrizedTypeInstance(ReflectionUtils
                            .getInstanceType(this));
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
        String query = "SELECT MIN(id) AS id FROM "
                + entity.getClass().getAnnotation(Entity.class).tableName() + " WHERE pendingSynchronization = 1";
        Cursor cursor = getDaoHelper().getReadableDatabase().rawQuery(query,
                null);
        cursor.moveToFirst();
        // FIXME return correct id
        return cursor.getInt(cursor.getColumnIndex("id")) + 1;
    }
}
