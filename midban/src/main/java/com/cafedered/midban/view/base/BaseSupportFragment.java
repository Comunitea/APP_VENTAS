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
package com.cafedered.midban.view.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.utils.InjectionUtils;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;

public class BaseSupportFragment extends Fragment {

    @SuppressWarnings("rawtypes")
    protected Intent getNextIntent(Bundle bundle, View view, Class clazz) {
        Intent intent = new Intent(view.getContext(), clazz);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        if (getClass().getAnnotation(
                com.cafedered.midban.annotations.Fragment.class) != null) {
            rootView = inflater.inflate(
                    getClass().getAnnotation(
                            com.cafedered.midban.annotations.Fragment.class)
                            .value(), container, false);
            try {
                InjectionUtils.injectAnnotatedFieldsAndMethods(rootView, this);
            } catch (ConfigurationException e) {
                if (LoggerUtil.isDebugEnabled())
                    e.printStackTrace();
            }
        }
        return rootView;
    }

    protected void putValueInContext(String name, Object value) {
        ((MidbanApplication) getActivity().getApplicationContext()).getValues()
                .put(MidbanApplication.PREFIX + name, value);
    }

    protected void removeValueInContext(String name) {
        ((MidbanApplication) getActivity().getApplicationContext()).getValues()
                .remove(MidbanApplication.PREFIX + name);
    }

    protected Object getValueFromContext(String name) {
        return ((MidbanApplication) getActivity().getApplicationContext())
                .getValues().get(MidbanApplication.PREFIX + name);
    }
}