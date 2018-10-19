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
package com.cafedered.midban.view.transformers;

import java.lang.reflect.Field;
import java.util.List;

import android.app.Fragment;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.entities.BaseEntity;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.ReflectionUtils;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ReflectionException;
import com.cafedered.midban.view.base.BaseSupportFragment;

public class BaseTransformer<E extends BaseEntity> {

    private Boolean debugEnabled = LoggerUtil.isDebugEnabled();

    public void transformEntityToUi(E entity, android.support.v4.app.Fragment rootView)
            throws ConfigurationException {
        List<Field> fields = ReflectionUtils.getFieldsAnnotatedAs(
                rootView.getClass(), Wire.class);
        if (null == entity) {
            throw new ConfigurationException(
                    "The object provided to transformer must not be null.");
        }
        for (Field field : fields) {
            try {
                String nameType = field.getType().getName();
                if ((nameType.equals(EditText.class.getName()) || (nameType
                        .equals(TextView.class.getName())))
                        && !field.getAnnotation(Wire.class).field().equals("")) {
                    if (ReflectionUtils.getValue(entity, ReflectionUtils
                            .getField(entity.getClass(),
                                    field.getAnnotation(Wire.class).field())) != null
                            && !ReflectionUtils
                                    .getValue(
                                            entity,
                                            ReflectionUtils.getField(entity
                                                    .getClass(), field
                                                    .getAnnotation(Wire.class)
                                                    .field())).toString()
                                    .equals("null"))
                        ((TextView) ReflectionUtils.getValue(rootView, field))
                                .setText(ReflectionUtils.getValue(
                                        entity,
                                        ReflectionUtils.getField(entity
                                                .getClass(), field
                                                .getAnnotation(Wire.class)
                                                .field())).toString());
                    else
                        ((TextView) ReflectionUtils.getValue(rootView, field))
                                .setText("");

                }

            } catch (ReflectionException e) {
                if (debugEnabled) {
                    Log.e(this.getClass().getName(),
                            "Error al procesar el campo: " + field.getName());
                    e.printStackTrace();
                }
                throw new ConfigurationException(e);
            }
        }
    }

    public void transformUiToEntity(E entity, android.support.v4.app.Fragment rootView)
            throws ConfigurationException {
        List<Field> fields = ReflectionUtils.getFieldsAnnotatedAs(
                rootView.getClass(), Wire.class);
        if (null == entity)
            throw new ConfigurationException(
                    "The object provided to transformer must not be null.");
        for (Field field : fields) {
            try {
                if ((field.getType().getName().equals(EditText.class.getName()) || field
                        .getType().getName().equals(TextView.class.getName()))
                        && !field.getAnnotation(Wire.class).field().equals("")) {
                    if (field.getType().getName()
                            .equals(EditText.class.getName()))
                        ReflectionUtils.setValue(
                                entity,
                                field.getAnnotation(Wire.class).field(),
                                getTypedValue(((EditText) ReflectionUtils
                                        .getValue(rootView, field)).getText()
                                        .toString(), ReflectionUtils
                                        .getFieldType(entity, field
                                                .getAnnotation(Wire.class)
                                                .field())));
                    else
                        ReflectionUtils.setValue(
                                entity,
                                field.getAnnotation(Wire.class).field(),
                                getTypedValue(((TextView) ReflectionUtils
                                        .getValue(rootView, field)).getText()
                                        .toString(), ReflectionUtils
                                        .getFieldType(entity, field
                                                .getAnnotation(Wire.class)
                                                .field())));
                }
            } catch (ReflectionException e) {
                if (debugEnabled)
                    e.printStackTrace();
                throw new ConfigurationException(e);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private Object getTypedValue(String value, Class type) {
        if (type.getName().equals(Number.class.getName())) {
            try {
                return (Number) Integer.parseInt(value);
            } catch (Exception e) {
                return (Number) Double.parseDouble(value);
            }
        }
        return value;
    }
}
