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
package com.cafedered.midban.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import android.util.Base64;

import com.cafedered.midban.dao.BaseDAO;
import com.cafedered.midban.entities.BaseEntity;
import com.cafedered.midban.service.repositories.BaseRepository;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ReflectionException;

public class ReflectionUtils {

    @SuppressWarnings("rawtypes")
    public static Class getInstanceType(Object c) {
        return (Class) ((ParameterizedType) c.getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    public static Object getObjectParametrizedTypeInstance(Class<?> c)
            throws ReflectionException {
        try {
            return c.newInstance();
        } catch (InstantiationException e) {
            throw new ReflectionException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    public static Object getObjectInstance(Class<?> c)
            throws ReflectionException {
        try {
            return c.newInstance();
        } catch (InstantiationException e) {
            throw new ReflectionException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Class getFieldType(Object entity, String field)
            throws ReflectionException {
        try {
            return entity.getClass().getDeclaredField(field).getType();
        } catch (NoSuchFieldException e) {
            throw new ReflectionException(e);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<Field> getFieldsAnnotatedAs(Class clazz, Class annotation) {
        List<Field> result = new ArrayList<Field>();
        for (Field field : clazz.getDeclaredFields())
            if (field.getAnnotation(annotation) != null)
                result.add(field);
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<Method> getMethodsAnnotatedAs(Class clazz,
            Class annotation) {
        List<Method> result = new ArrayList<Method>();
        for (Method method : clazz.getMethods())
            if (method.getAnnotation(annotation) != null)
                result.add(method);
        return result;
    }

    public static void setValue(Object receptor, String fieldName, Object value)
            throws ReflectionException {
        try {
            Field field = receptor.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            if (field.getType().equals(byte[].class)) {
                field.set(receptor, Base64.decode((String) value, 0));
            } else
                field.set(receptor, value);
        } catch (NoSuchFieldException e) {
            throw new ReflectionException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        } catch (IllegalArgumentException e) {
            throw new ReflectionException(e);
        }
    }

    public static Object getValue(Object object, Field field)
            throws ReflectionException {
        try {
            if (!field.isAccessible())
                field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Object createObject(Class clazz) throws ReflectionException {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new ReflectionException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Field getField(Class clazz, String field)
            throws ReflectionException {
        try {
            return clazz.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            throw new ReflectionException(e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static BaseRepository<BaseEntity, BaseDAO<BaseEntity>> getInstancedRepositoryFor(Class repositoryRef)
            throws ConfigurationException {
        try {
            Method method = repositoryRef.getMethod("getInstance");
            return (BaseRepository<BaseEntity, BaseDAO<BaseEntity>>) method.invoke(null);
        } catch (NoSuchMethodException e) {
            throw new ConfigurationException(
                    "Cannot find dao with getInstance for " + repositoryRef);
        } catch (IllegalAccessException e) {
            throw new ConfigurationException(
                    "Cannot find dao with getInstance for " + repositoryRef);
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException(
                    "Cannot find dao with getInstance for " + repositoryRef);
        } catch (InvocationTargetException e) {
            throw new ConfigurationException(
                    "Cannot find dao with getInstance for " + repositoryRef);
        }
    }
}
