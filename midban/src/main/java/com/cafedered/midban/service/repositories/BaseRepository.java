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
package com.cafedered.midban.service.repositories;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xmlrpc.XmlRpcException;

import android.util.Log;

import com.cafedered.cafedroidlitedao.exceptions.DatabaseException;
import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.annotations.Remote;
import com.cafedered.midban.annotations.RemoteProperty;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.dao.BaseDAO;
import com.cafedered.midban.entities.AccountJournal;
import com.cafedered.midban.entities.AccountMoveLine;
import com.cafedered.midban.entities.BaseEntity;
import com.cafedered.midban.entities.BaseRemoteEntity;
import com.cafedered.midban.entities.Order;
import com.cafedered.midban.entities.OrderLine;
import com.cafedered.midban.entities.StockPickingOut;
import com.cafedered.midban.entities.Synchronization;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.ReflectionUtils;
import com.cafedered.midban.utils.SessionFactory;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ObjectArrayToStringFilter;
import com.cafedered.midban.utils.exceptions.ReflectionException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.debortoliwines.openerp.api.FilterCollection;
import com.debortoliwines.openerp.api.ObjectAdapter;
import com.debortoliwines.openerp.api.OpeneERPApiException;
import com.debortoliwines.openerp.api.Row;
import com.debortoliwines.openerp.api.RowCollection;
import com.debortoliwines.openerp.api.Session;

public class BaseRepository<E extends BaseEntity, D extends BaseDAO<E>> {

    protected D dao;

    private Map<Long, E> cache = new LinkedHashMap<Long, E>(MAX_CACHE_SIZE);
    private static final int MAX_CACHE_SIZE = 100;

    public void saveOrUpdate(E entity) throws ServiceException {
        try {
            dao.saveOrUpdate(entity);
            // DAVID - DESACTIVO CACHE TEMPORALMENTE cache.put(entity.getId(), entity);
        } catch (DatabaseException e) {
            e.printStackTrace();
            throw new ServiceException("Cannot access data.", e);
        }
    }

    public Integer getCount() { return dao.getCount();}

    public Integer getNextIdNumber() {
        return dao.getNextIdNumber();
    }

    public int getLastSynchronizedIdNumber() {
        return dao.getLastSynchronizedIdNumber();
    }

    public List<E> getAll(Integer numElements, Integer offset)
            throws ConfigurationException, ServiceException {
        try {
            return dao.getAll(numElements, offset);
        } catch (DatabaseException e) {
            throw new ServiceException("Cannot retrieve all objects", e);
        }
    }

    public List<Object> getAllSomeProperty(String field)
            throws ServiceException {
        try {
            return dao.getAllSomeField(field);
        } catch (ConfigurationException e) {
            throw new ServiceException("Cannot retrieve all objects", e);
        } catch (DatabaseException e) {
            throw new ServiceException("Cannot retrieve all objects", e);
        }
    }

    public Set<Object> getAllDistinctSomeProperty(String field)
            throws ServiceException {
        try {
            return dao.getAllDistinctSomeField(field);
        } catch (ConfigurationException e) {
            e.printStackTrace();
            throw new ServiceException("Cannot retrieve all objects", e);
        } catch (DatabaseException e) {
            e.printStackTrace();
            throw new ServiceException("Cannot retrieve all objects", e);
        }
    }

    public void delete(Long id) throws ServiceException {
        try {
            dao.delete(id);
        } catch (DatabaseException e) {
            throw new ServiceException("Error al intentar borrar.", e);
        } catch (ReflectionException e) {
            throw new ServiceException("Error al intentar borrar.", e);
        }
    }

    public E getById(Long id) throws ConfigurationException, ServiceException {
        try {
            E entity = null;
            if (!cache.containsKey(id) && id != null && id != 0L) {
                entity = dao.getUniqueResult(id);
                if (entity != null)
                    addToCache(id, entity);
                return entity;
            } else
                return cache.get(id);
        } catch (DatabaseException e) {
            throw new ServiceException("Cannot retrieve object", e);
        }
    }

    private void addToCache(Long id, E value) {
        if (cache.size() == MAX_CACHE_SIZE)
            cache.clear();
        // DAVID - DESACTIVO CACHE TEMPORALMENTE  cache.put(id, value);
    }

    public List<E> getByExample(E entity, Restriction restriction,
                                boolean exactMatching, Integer numElements, Integer offset)
            throws ServiceException {
        try {
            return dao.getByExample(entity, restriction, exactMatching,
                    numElements, offset);
        } catch (DatabaseException e) {
            throw new ServiceException("Cannot retrieve objects", e);
        }
    }

    @SuppressWarnings("unchecked")
    public int getRemoteObjects(BaseRemoteEntity entity, String login,
            String passwd, boolean doDeletes) throws ConfigurationException {

        int result = 0;
        Long initDate = new Date().getTime();
        String error = "";
        Remote annotation = entity.getClass().getAnnotation(Remote.class);
        if (annotation == null)
            throw new ConfigurationException(
                    "Entity class is not properly annotated. Cannot synchronize entity without @Remote annotation.");
        Session openERPSession;
        try {
            openERPSession = SessionFactory.getInstance(login, passwd)
                    .getSession();
        } catch (Exception e) {
            throw new ConfigurationException(e);
        }
        ObjectAdapter adapter;
        try {
            Long timeInit = new Date().getTime();
            adapter = openERPSession.getObjectAdapter(annotation.object());
            FilterCollection filters = entity.getRemoteFilters();
            RowCollection entities;
            Map<String, String> remoteFields = getRemoteFields(entity);
            String[] fieldsRemote = new String[remoteFields.size()];
            fieldsRemote = remoteFields.keySet().toArray(fieldsRemote);
            String maxDateOld = SynchronizationRepository.getInstance().getMaxDateFor(
                    entity.getClass());
            String utcDate = DateUtil.convertToUTC(maxDateOld);
            // PETICIÓN EXPRESA DEL INTEGRADOR. No enviar la fecha en la primera sincronización
            if ("0001-01-01 00:00:00".equals(utcDate)) {
                utcDate = null;
            }
            // en la primera sincronización no hay que comprobar los eliminados, no tiene sentido
            if ((utcDate != null) && doDeletes) {
                String[] fieldId = new String[1];
                fieldId[0] = "id";
                // obtengo todos los ids de servidor
                RowCollection serverIds = adapter.searchAndReadObject(
                        filters, fieldId, null);
                try {
                        // obtengo todos los ids en local
                        List<E> localIds = dao.getAll(0, 1000000);
                        // Probamos este nuevo método para eliminar lo que sobra - Pedro Gómez - 05/03/2020
                        List<Long> serverIdsList = new ArrayList<>();
                        for (Row row : serverIds) {
                            serverIdsList.add(((Integer) row.get("id")).longValue());
                        }
                        for (int i = 0; i < localIds.size(); i++) {
                            Long id = localIds.get(i).getId();
                            if (!serverIdsList.contains(id)) {
                                try {
                                    dao.delete(id);
                                } catch (ReflectionException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        // Hasta aquí - Pedro Gómez - 05/03/2020
                        /* Este era el anterior - Pedro Gómez - 05/03/2020
                        // para cada id en local ...
                        for (int i = 0; i < localIds.size(); i++) {
                            boolean exists = false;
                            Long id = localIds.get(i).getId();
                            // ... lo busco en la lista que me llegó de servidor
                            for (Row row : serverIds) {
                                // si lo encuentro dejo una marca como que sigue existiendo
                                if (id == ((Integer) row.get("id")).longValue()) {
                                    exists = true;
                                    break;
                                }
                            }
                            // si no lo ha encontrado lo elimino de local
                            if (!exists) {
                                try {
                                    dao.delete(id);
                                } catch (ReflectionException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        */
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
            }
            entities = adapter.searchAndReadObject(
                    filters, fieldsRemote, utcDate);
            List<E> toSave = new ArrayList<E>();
            if (LoggerUtil.isDebugEnabled()) {
                System.out.println("Procesada petición de red. " + entities.size() + " elementos. Tiempo: " + (new Date().getTime() - timeInit) / 1000L);
            }
            for (Row row : entities) {
                if (LoggerUtil.isDebugEnabled()) {
                    Iterator<com.debortoliwines.openerp.api.Field> fieldsOfRow = row.getFields().iterator();
                    while (fieldsOfRow.hasNext()) {
                        com.debortoliwines.openerp.api.Field aField = fieldsOfRow.next();
                    }
                }
                E anEntity;
                try {
                    anEntity = (E) ReflectionUtils.createObject(entity
                            .getClass());
                    for (String field : remoteFields.keySet()) {
                        try {
                            if (field.equals("id")) {
                                anEntity.setId(((Integer) row.get(field))
                                        .longValue());
                            } else if (row.get(field) != null
                                    && row.get(field) instanceof Object[]) {
                                RemoteProperty annotationProperty = ReflectionUtils
                                        .getField(anEntity.getClass(),
                                                remoteFields.get(field))
                                        .getAnnotation(RemoteProperty.class);
                                if (annotationProperty.filter().equals(ObjectArrayToStringFilter.class)) {
                                    ReflectionUtils.setValue(anEntity,
                                            remoteFields.get(field),
                                            ObjectArrayToStringFilter.doFilter((Object[])row.get(field)));
                                } else {
                                    ReflectionUtils.setValue(anEntity,
                                            remoteFields.get(field),
                                            ((Object[]) row.get(field))[0]);
                                    if (!annotationProperty.entityRef().equals(
                                            Void.class)) {
                                        BaseEntity innerEntity = (BaseEntity) ReflectionUtils
                                                .createObject(annotationProperty
                                                        .entityRef());
                                        int propertiesIterator = 0;
                                        for (String innerProperty : annotationProperty
                                                .orderedProperties()) {
                                            if (innerProperty.equals("id"))
                                                innerEntity
                                                        .setId(((Integer) ((Object[]) row
                                                                .get(field))[propertiesIterator])
                                                                .longValue());
                                            else
                                                ReflectionUtils
                                                        .setValue(
                                                                innerEntity,
                                                                innerProperty,
                                                                ((Object[]) row
                                                                        .get(field))[propertiesIterator]);
                                            propertiesIterator++;
                                        }
                                        ((BaseRemoteEntity) innerEntity)
                                                .setPendingSynchronization(0);
                                        try {
                                            ReflectionUtils.getInstancedRepositoryFor(
                                                    annotationProperty.repositoryRef())
                                                    .saveOrUpdate(innerEntity);
                                        } catch (ServiceException e) {
                                            error += "ServiceException";
                                            throw new ConfigurationException(e);
                                        }
                                    }
                                }
                            } else if (row.get(field) != null
                                    && !row.get(field).getClass().equals(Date.class))
                                ReflectionUtils.setValue(anEntity,
                                                remoteFields.get(field),
                                                row.get(field));
                            else {
                                try {
                                    if (row.get(field) instanceof Date) {
                                        // DAVID - si la propiedad que va a recibir el dato es String hacemos la transformación, sino lo pasamos como Date
                                        Field f = anEntity.getClass().getDeclaredField(remoteFields.get(field));
                                        f.setAccessible(true);

                                        if (f.getType().equals(String.class)) {
                                            ReflectionUtils.setValue(anEntity,
                                                    remoteFields.get(field),
                                                    DateUtil.toFormattedString(
                                                            (Date) row.get(field),
                                                            "yyyy-MM-dd HH:mm:ss"));
                                        }
                                        else{
                                            if (f.getType().equals(Date.class)) {
                                                ReflectionUtils.setValue(anEntity,
                                                        remoteFields.get(field),
                                                        row.get(field));
                                            }
                                        }
                                    }
                                } catch (Exception e1) {
                                    Log.e(this.getClass().getName(),
                                            "Error setting field: " + field
                                                    + ", value: " + row.get(field));
                                    error += this.getClass().getName()
                                            + " Error setting field: " + field
                                            + ", value: " + row.get(field);
                                    throw new ConfigurationException(e1);
                                }
                            }
                        } catch (ReflectionException e) {
                            try {
                                if (row.get(field) instanceof Date) {
                                    ReflectionUtils.setValue(anEntity,
                                            remoteFields.get(field),
                                            DateUtil.toFormattedString(
                                                    (Date) row.get(field),
                                                    "yyyy-MM-dd HH:mm:ss"));
                                }
                            } catch (Exception e1) {
                                Log.e(this.getClass().getName(),
                                        "Error setting field: " + field
                                                + ", value: " + row.get(field));
                                error += this.getClass().getName()
                                        + " Error setting field: " + field
                                        + ", value: " + row.get(field);
                                throw new ConfigurationException(e1);
                            }
                        }
                    }
                    ((BaseRemoteEntity) anEntity).setPendingSynchronization(0);
                    toSave.add(anEntity);
                } catch (ReflectionException e) {
                    error += "ConfigurationException";
                    throw new ConfigurationException(e);
                }
            }
            for (E eToSave : toSave) {
                try {
                    if (eToSave instanceof StockPickingOut) {
                        // TODO controlar OpenERP v7 y cambiar 2 por los que
                        // sean albaranes
                        if (((StockPickingOut) eToSave).getPickingTypeId() != null
                                && ((StockPickingOut) eToSave)
                                        .getPickingTypeId().equals(2))
                            dao.saveOrUpdate(eToSave);
                    } else {
                        if (eToSave instanceof OrderLine || eToSave instanceof Order) {
                            dao.delete(eToSave.getId());
                        }
                        dao.saveOrUpdate(eToSave);
                    }
                } catch (DatabaseException e) {
                    error += "ConfigurationException";
                    throw new ConfigurationException(e);
                } catch (ReflectionException e) {
                    e.printStackTrace();
                    error += "ConfigurationException";
                    throw new ConfigurationException(e);
                }
                result++;
            }
        } catch (XmlRpcException e) {
            error += "ConfigurationException";
            throw new ConfigurationException(e);
        } catch (OpeneERPApiException e) {
            error += "ConfigurationException";
            throw new ConfigurationException(e);
        }
        try {
            Date endDataSynchro = new Date();
            Long duration = endDataSynchro.getTime() - initDate;
            SynchronizationRepository.getInstance().saveOrUpdate(
                    Synchronization.create(entity.getClass().getName(),
                            DateUtil.toFormattedString(endDataSynchro), result,
                            duration, error));
        } catch (ServiceException e) {
            throw new ConfigurationException(e);
        }
        return result;
    }

    public Long createRemoteObject(E entity, String login,
            String passwd) throws ServiceException {
        try {
            Session openERPSession;
            Remote remoteAnnotation = entity.getClass().getAnnotation(
                    Remote.class);
            openERPSession = SessionFactory.getInstance(login, passwd)
                    .getSession();
            // petición del integrador, quiere que se envíe este campo siempre
            openERPSession.getContext().put("app_company_id", MidbanApplication.activeCompany);
            ObjectAdapter adapter = openERPSession
                    .getObjectAdapter(remoteAnnotation.object());
            Map<String, String> remoteFields = getRemoteFields((BaseRemoteEntity)entity);
            if (entity.getClass().equals(Order.class))
                remoteFields.put("warehouse_id", ConfigurationRepository.getInstance()
                    .getConfiguration().getWarehouseId().toString());
            String[] fieldsRemote = new String[remoteFields.size()];
            fieldsRemote = remoteFields.keySet().toArray(fieldsRemote);
            Row newRow = adapter.getNewRow(fieldsRemote);
            for (String remoteField : fieldsRemote) {
                Object value;
                try {
                    if (!remoteField.equals("warehouse_id")) {
                        value = ReflectionUtils.getValue(entity, ReflectionUtils
                                .getField(entity.getClass(),
                                        remoteFields.get(remoteField)));
                        if (value != null)
                            newRow.put(remoteField, value);
                    }
                } catch (ReflectionException e) {
                    throw new ServiceException("Cannot get value for field: "
                            + remoteFields.get(remoteField), e);
                }
            }
            if (entity.getClass().equals(Order.class))
                newRow.put("warehouse_id", ConfigurationRepository.getInstance()
                        .getConfiguration().getWarehouseId().toString());
            adapter.createObject(newRow);
            ((BaseRemoteEntity) entity).setPendingSynchronization(0);
            ((BaseRemoteEntity) entity).setId(Long.valueOf(newRow.getID()));
            dao.saveOrUpdate(entity);
            return Long.valueOf(newRow.getID());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("Exception accessing OpenERP", e);
        }
    }

    public boolean updateRemoteObject(E entity, String login, String passwd) {
        List<E> wrapper = new ArrayList<E>();
        wrapper.add(entity);
        try {
            return updateRemoteObjects(wrapper, login, passwd) == 1;
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
            return false;
        }
    }

    public int updateRemoteObjects(List<E> entities, String login, String passwd)
            throws ServiceException {
        int numUpdated = 0;
        for (E entity : entities) {
            Remote remoteAnnotation = entity.getClass().getAnnotation(
                    Remote.class);
            Session openERPSession;
            try {
                openERPSession = SessionFactory.getInstance(login, passwd)
                        .getSession();
                ObjectAdapter adapter = openERPSession
                        .getObjectAdapter(remoteAnnotation.object());
                FilterCollection filters = new FilterCollection();
                if (entity.getId() != null)
                    filters.add("id", "=", entity.getId());
                Map<String, String> remoteFields = getRemoteFields((BaseRemoteEntity) entity);
                remoteFields.put("warehouse_id", ConfigurationRepository.getInstance()
                        .getConfiguration().getWarehouseId().toString());
                String[] fieldsRemote = new String[remoteFields.size()];
                fieldsRemote = remoteFields.keySet().toArray(fieldsRemote);
                RowCollection rows = adapter.searchAndReadObject(filters,
                        fieldsRemote, "1970-01-01");
                Row updateRow = rows.get(0);
                for (String remoteField : remoteFields.keySet()) {
                    Object value;
                    try {
                        value = ReflectionUtils.getValue(entity,
                                ReflectionUtils.getField(entity.getClass(),
                                        remoteFields.get(remoteField)));
                        if (value != null)
                            updateRow.put(remoteField, value);
                    } catch (ReflectionException e) {
                        throw new ServiceException(
                                "Cannot get value for field: "
                                        + remoteFields.get(remoteField), e);
                    }
                }
                updateRow.put("warehouse_id", remoteFields.get("warehouse_id"));
                boolean success = adapter.writeObject(updateRow, true);
                if (success) {
                    numUpdated++;
                    ((BaseRemoteEntity) entity).setPendingSynchronization(0);
                    dao.saveOrUpdate(entity);
                }
            } catch (Exception e) {
                throw new ServiceException("Exception accessing OpenERP", e);
            }
        }
        return numUpdated;
    }

    protected Map<String, String> getRemoteFields(BaseRemoteEntity entity) {
        List<Field> fields = ReflectionUtils.getFieldsAnnotatedAs(
                entity.getClass(), RemoteProperty.class);
        Map<String, String> result = new HashMap<String, String>();
        for (Field field : fields) {
            result.put(field.getAnnotation(RemoteProperty.class).name(),
                    field.getName());
        }
        return result;
    }
}
