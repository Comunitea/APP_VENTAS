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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Background;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.ItemClicked;
import com.cafedered.midban.annotations.ItemSelected;
import com.cafedered.midban.annotations.LongClick;
import com.cafedered.midban.annotations.TextChanged;
import com.cafedered.midban.annotations.Transformer;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ReflectionException;
import com.cafedered.midban.view.base.BaseSupportActivity;

public class InjectionUtils {
    public static void injectAnnotatedFieldsAndMethods(final View rootView,
            final Object object) throws ConfigurationException {
        final Boolean debugEnabled = Boolean.valueOf(rootView.getResources()
                .getString(R.string.configuration_debug_enabled));
        List<Field> fields = ReflectionUtils.getFieldsAnnotatedAs(
                object.getClass(), Wire.class);
        for (Field field : fields) {
            try {
                if (!field.isAccessible())
                    field.setAccessible(true);
                field.set(object, rootView.findViewById(field.getAnnotation(
                        Wire.class).view()));
            } catch (IllegalAccessException e) {
                if (debugEnabled)
                    e.printStackTrace();
                throw new ConfigurationException(e);
            } catch (IllegalArgumentException e) {
                if (debugEnabled)
                    e.printStackTrace();
                throw new ConfigurationException(e);
            }
        }

        List<Field> transformers = ReflectionUtils.getFieldsAnnotatedAs(
                object.getClass(), Transformer.class);
        for (Field transformer : transformers) {
            try {
                if (!transformer.isAccessible())
                    transformer.setAccessible(true);
                transformer.set(object,
                        ReflectionUtils.createObject(transformer.getType()));
            } catch (IllegalAccessException e) {
                if (debugEnabled)
                    e.printStackTrace();
                throw new ConfigurationException(e);
            } catch (IllegalArgumentException e) {
                if (debugEnabled)
                    e.printStackTrace();
                throw new ConfigurationException(e);
            } catch (ReflectionException e) {
                if (debugEnabled)
                    e.printStackTrace();
                throw new ConfigurationException(e);
            }
        }

        injectClicks(rootView, object, debugEnabled);
        injectItemSelection(rootView, object, debugEnabled);
        injectItemClicks(rootView, object, debugEnabled);

        List<Method> longclicks = ReflectionUtils.getMethodsAnnotatedAs(
                object.getClass(), LongClick.class);
        for (final Method method : longclicks) {
            try {

                List<View> allViews = new ArrayList<View>();
                if (method.getAnnotation(LongClick.class).view() == -1) {
                    int[] viewIds = method.getAnnotation(LongClick.class)
                            .views();
                    for (int i = 0; i < viewIds.length; i++) {
                        allViews.add((View) rootView.findViewById(viewIds[i]));
                    }
                } else {
                    allViews.add((View) rootView.findViewById(method
                            .getAnnotation(LongClick.class).view()));
                }

                for (final View theView : allViews) {
                    theView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (method.getAnnotation(Background.class) != null) {
                                new AsyncTask<Void, Void, Boolean>() {

                                    @Override
                                    protected Boolean doInBackground(
                                            Void... params) {
                                        try {
                                            method.invoke(object, theView);
                                        } catch (IllegalAccessException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        } catch (IllegalArgumentException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        } catch (InvocationTargetException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        } catch (RuntimeException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        }
                                        return true;
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean result) {
                                        super.onPostExecute(result);
                                        if (result) {
                                            if (method.getAnnotation(
                                                    Background.class).onOK() != -1)
                                                MessagesForUser.showMessage(
                                                        ((Activity) rootView
                                                                .getContext()),
                                                        method.getAnnotation(
                                                                Background.class)
                                                                .onOK(),
                                                        Toast.LENGTH_LONG,
                                                        Level.INFO);
                                            if (!method
                                                    .getAnnotation(
                                                            Background.class)
                                                    .onOKRedirect()
                                                    .getName()
                                                    .equals(Void.class
                                                            .getName())) {
                                                ((Activity) rootView
                                                        .getContext())
                                                        .startActivityForResult(
                                                                ((BaseSupportActivity) rootView
                                                                        .getContext())
                                                                        .getNextIntent(
                                                                                new Bundle(),
                                                                                rootView.getContext(),
                                                                                method.getAnnotation(
                                                                                        Background.class)
                                                                                        .onOKRedirect()),
                                                                0);
                                            }
                                        } else {
                                            MessagesForUser.showMessage(
                                                    ((Activity) rootView
                                                            .getContext()),
                                                    method.getAnnotation(
                                                            Background.class)
                                                            .onKO(),
                                                    Toast.LENGTH_LONG,
                                                    Level.SEVERE);
                                        }
                                    }
                                }.execute();
                            } else {
                                try {
                                    method.invoke(object, theView);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                } catch (RuntimeException e) {
                                    e.printStackTrace();
                                }
                            }
                            return true;
                        }
                    });
                }
            } catch (IllegalArgumentException e) {
                if (debugEnabled)
                    e.printStackTrace();
                throw new ConfigurationException(e);
            }
        }

        List<Method> textsChanged = ReflectionUtils.getMethodsAnnotatedAs(
                object.getClass(), TextChanged.class);
        for (final Method method : textsChanged) {
            try {
                List<TextView> allViews = new ArrayList<TextView>();
                if (method.getAnnotation(TextChanged.class).view() != -1)
                    allViews.add(((TextView) rootView.findViewById(method
                            .getAnnotation(TextChanged.class).view())));
                else {
                    int[] resources = method.getAnnotation(TextChanged.class)
                            .views();
                    for (int i = 0; i < resources.length; i++) {
                        allViews.add(((TextView) rootView
                                .findViewById(resources[i])));
                    }
                }
                for (final TextView theView : allViews) {
                    theView.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence s, int start,
                                int before, int count) {
                            if (method.getAnnotation(Background.class) != null) {
                                new AsyncTask<Void, Void, Boolean>() {
                                    @Override
                                    protected Boolean doInBackground(
                                            Void... params) {
                                        Object[] obj = null;
                                        if (method.getParameterTypes().length == 1)
                                            obj = new Object[] { theView };
                                        try {
                                            method.invoke(object, obj);
                                        } catch (IllegalAccessException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        } catch (IllegalArgumentException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        } catch (InvocationTargetException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        } catch (RuntimeException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        }
                                        return true;
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean result) {
                                        super.onPostExecute(result);
                                        if (result) {
                                            if (method.getAnnotation(
                                                    Background.class).onOK() != -1)
                                                MessagesForUser.showMessage(
                                                        ((Activity) rootView
                                                                .getContext()),
                                                        method.getAnnotation(
                                                                Background.class)
                                                                .onOK(),
                                                        Toast.LENGTH_LONG,
                                                        Level.INFO);
                                            if (!method
                                                    .getAnnotation(
                                                            Background.class)
                                                    .onOKRedirect()
                                                    .getName()
                                                    .equals(Void.class
                                                            .getName())) {
                                                ((Activity) rootView
                                                        .getContext())
                                                        .startActivityForResult(
                                                                ((BaseSupportActivity) rootView
                                                                        .getContext())
                                                                        .getNextIntent(
                                                                                new Bundle(),
                                                                                rootView.getContext(),
                                                                                method.getAnnotation(
                                                                                        Background.class)
                                                                                        .onOKRedirect()),
                                                                0);
                                            }
                                        } else {
                                            if (method.getAnnotation(
                                                    Background.class).onKO() != -1)
                                                MessagesForUser.showMessage(
                                                        ((Activity) rootView
                                                                .getContext()),
                                                        method.getAnnotation(
                                                                Background.class)
                                                                .onKO(),
                                                        Toast.LENGTH_LONG,
                                                        Level.SEVERE);
                                        }
                                    }
                                }.execute();
                            } else {
                                Object[] obj = null;
                                if (method.getParameterTypes().length == 1)
                                    obj = new Object[] { theView };
                                try {
                                    method.invoke(object, obj);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                } catch (RuntimeException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s,
                                int start, int count, int after) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
            } catch (IllegalArgumentException e) {
                if (debugEnabled)
                    e.printStackTrace();
                throw new ConfigurationException(e);
            }
        }
    }

    private static void injectClicks(final View rootView, final Object object,
            final boolean debugEnabled) throws ConfigurationException {
        List<Method> clicks = ReflectionUtils.getMethodsAnnotatedAs(
                object.getClass(), Click.class);
        for (final Method method : clicks) {
            try {
                List<View> allViews = new ArrayList<View>();
                if (method.getAnnotation(Click.class).view() == -1) {
                    int[] viewIds = method.getAnnotation(Click.class).views();
                    for (int i = 0; i < viewIds.length; i++) {
                        allViews.add((View) rootView.findViewById(viewIds[i]));
                    }
                } else {
                    allViews.add((View) rootView.findViewById(method
                            .getAnnotation(Click.class).view()));
                }

                for (final View theView : allViews) {
                    theView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (method.getAnnotation(Background.class) != null) {
                                new AsyncTask<Void, Void, Boolean>() {
                                    @Override
                                    protected Boolean doInBackground(
                                            Void... params) {
                                        Object[] obj = null;
                                        try {
                                            method.invoke(object, obj);
                                        } catch (IllegalAccessException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        } catch (IllegalArgumentException e) {
                                            try {
                                                method.invoke(object, theView);
                                            } catch (IllegalAccessException e1) {
                                                if (debugEnabled)
                                                    e1.printStackTrace();
                                                return false;
                                            } catch (IllegalArgumentException e1) {
                                                if (debugEnabled)
                                                    e1.printStackTrace();
                                                return false;
                                            } catch (InvocationTargetException e1) {
                                                if (debugEnabled)
                                                    e1.printStackTrace();
                                                return false;
                                            }
                                        } catch (InvocationTargetException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        } catch (RuntimeException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        }
                                        return true;
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean result) {
                                        super.onPostExecute(result);
                                        if (result) {
                                            if (method.getAnnotation(
                                                    Background.class).onOK() != -1)
                                                MessagesForUser.showMessage(
                                                        ((Activity) rootView
                                                                .getContext()),
                                                        method.getAnnotation(
                                                                Background.class)
                                                                .onOK(),
                                                        Toast.LENGTH_LONG,
                                                        Level.INFO);
                                            if (method.getAnnotation(
                                                    Background.class)
                                                    .onOKFinish() == true) {
                                                ((Activity) rootView
                                                        .getContext()).finish();
                                            }

                                            if (!method
                                                    .getAnnotation(
                                                            Background.class)
                                                    .onOKRedirect()
                                                    .getName()
                                                    .equals(Void.class
                                                            .getName())) {
                                                ((Activity) rootView
                                                        .getContext())
                                                        .startActivityForResult(
                                                                ((BaseSupportActivity) rootView
                                                                        .getContext())
                                                                        .getNextIntent(
                                                                                new Bundle(),
                                                                                rootView.getContext(),
                                                                                method.getAnnotation(
                                                                                        Background.class)
                                                                                        .onOKRedirect()),
                                                                0);
                                            }
                                        } else {
                                            MessagesForUser.showMessage(
                                                    ((Activity) rootView
                                                            .getContext()),
                                                    method.getAnnotation(
                                                            Background.class)
                                                            .onKO(),
                                                    Toast.LENGTH_LONG,
                                                    Level.SEVERE);
                                        }
                                    }
                                }.execute();
                            } else {
                                Object[] obj = null;
                                try {
                                    method.invoke(object, obj);
                                } catch (IllegalAccessException e) {
                                    if (debugEnabled)
                                        e.printStackTrace();
                                } catch (IllegalArgumentException e) {
                                    try {
                                        method.invoke(object, theView);
                                    } catch (IllegalAccessException e1) {
                                        if (debugEnabled)
                                            e1.printStackTrace();
                                    } catch (IllegalArgumentException e1) {
                                        if (debugEnabled)
                                            e1.printStackTrace();
                                    } catch (InvocationTargetException e1) {
                                        if (debugEnabled)
                                            e1.printStackTrace();
                                    }
                                } catch (InvocationTargetException e) {
                                    if (debugEnabled)
                                        e.printStackTrace();
                                } catch (RuntimeException e) {
                                    if (debugEnabled)
                                        e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            } catch (IllegalArgumentException e) {
                if (debugEnabled)
                    e.printStackTrace();
                throw new ConfigurationException(e);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static void injectItemSelection(final View rootView,
            final Object object, final boolean debugEnabled)
            throws ConfigurationException {
        List<Method> itemsSelected = ReflectionUtils.getMethodsAnnotatedAs(
                object.getClass(), ItemSelected.class);
        for (final Method method : itemsSelected) {
            try {
                List<AdapterView> allViews = new ArrayList<AdapterView>();
                if (method.getAnnotation(ItemSelected.class).view() == -1) {
                    int[] viewIds = method.getAnnotation(ItemSelected.class)
                            .views();
                    for (int i = 0; i < viewIds.length; i++) {
                        allViews.add((AdapterView) rootView
                                .findViewById(viewIds[i]));
                    }
                } else {
                    allViews.add((AdapterView) rootView.findViewById(method
                            .getAnnotation(ItemSelected.class).view()));
                }

                for (final AdapterView theView : allViews) {
                    theView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                View view, int position, long id) {
                            if (method.getAnnotation(Background.class) != null) {
                                new AsyncTask<Object, Void, Boolean>() {
                                    @Override
                                    protected Boolean doInBackground(
                                            Object... params) {
                                        try {
                                            method.invoke(object, params[0]);
                                        } catch (IllegalAccessException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        } catch (IllegalArgumentException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        } catch (InvocationTargetException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        } catch (RuntimeException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        }
                                        return true;
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean result) {
                                        super.onPostExecute(result);
                                        if (result) {
                                            if (method.getAnnotation(
                                                    Background.class).onOK() != -1)
                                                MessagesForUser.showMessage(
                                                        ((Activity) rootView
                                                                .getContext()),
                                                        method.getAnnotation(
                                                                Background.class)
                                                                .onOK(),
                                                        Toast.LENGTH_LONG,
                                                        Level.INFO);
                                            if (method.getAnnotation(
                                                    Background.class)
                                                    .onOKFinish() == true) {
                                                ((Activity) rootView
                                                        .getContext()).finish();
                                            }

                                            if (!method
                                                    .getAnnotation(
                                                            Background.class)
                                                    .onOKRedirect()
                                                    .getName()
                                                    .equals(Void.class
                                                            .getName())) {
                                                ((Activity) rootView
                                                        .getContext())
                                                        .startActivityForResult(
                                                                ((BaseSupportActivity) rootView
                                                                        .getContext())
                                                                        .getNextIntent(
                                                                                new Bundle(),
                                                                                rootView.getContext(),
                                                                                method.getAnnotation(
                                                                                        Background.class)
                                                                                        .onOKRedirect()),
                                                                0);
                                            }
                                        } else {
                                            MessagesForUser.showMessage(
                                                    ((Activity) rootView
                                                            .getContext()),
                                                    method.getAnnotation(
                                                            Background.class)
                                                            .onKO(),
                                                    Toast.LENGTH_LONG,
                                                    Level.SEVERE);
                                        }
                                    }
                                }.execute(parent.getItemAtPosition(position));
                            } else {
                                try {
                                    method.invoke(object,
                                            parent.getItemAtPosition(position));
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                } catch (RuntimeException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // do nothing
                        }
                    });
                }
            } catch (IllegalArgumentException e) {
                if (debugEnabled)
                    e.printStackTrace();
                throw new ConfigurationException(e);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static void injectItemClicks(final View rootView,
            final Object object, final boolean debugEnabled)
            throws ConfigurationException {
        List<Method> itemsSelected = ReflectionUtils.getMethodsAnnotatedAs(
                object.getClass(), ItemClicked.class);
        for (final Method method : itemsSelected) {
            try {
                List<AdapterView> allViews = new ArrayList<AdapterView>();
                if (method.getAnnotation(ItemClicked.class).view() == -1) {
                    int[] viewIds = method.getAnnotation(ItemClicked.class)
                            .views();
                    for (int i = 0; i < viewIds.length; i++) {
                        allViews.add((AdapterView) rootView
                                .findViewById(viewIds[i]));
                    }
                } else {
                    allViews.add((AdapterView) rootView.findViewById(method
                            .getAnnotation(ItemClicked.class).view()));
                }

                for (final AdapterView theView : allViews) {
                    theView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                View view, int position, long id) {
                            if (method.getAnnotation(Background.class) != null) {
                                new AsyncTask<Object, Void, Boolean>() {
                                    @Override
                                    protected Boolean doInBackground(
                                            Object... params) {
                                        try {
                                            method.invoke(object, params[0]);
                                        } catch (IllegalAccessException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        } catch (IllegalArgumentException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        } catch (InvocationTargetException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        } catch (RuntimeException e) {
                                            if (debugEnabled)
                                                e.printStackTrace();
                                            return false;
                                        }
                                        return true;
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean result) {
                                        super.onPostExecute(result);
                                        if (result) {
                                            if (method.getAnnotation(
                                                    Background.class).onOK() != -1)
                                                MessagesForUser.showMessage(
                                                        ((Activity) rootView
                                                                .getContext()),
                                                        method.getAnnotation(
                                                                Background.class)
                                                                .onOK(),
                                                        Toast.LENGTH_LONG,
                                                        Level.INFO);
                                            if (method.getAnnotation(
                                                    Background.class)
                                                    .onOKFinish() == true) {
                                                ((Activity) rootView
                                                        .getContext()).finish();
                                            }

                                            if (!method
                                                    .getAnnotation(
                                                            Background.class)
                                                    .onOKRedirect()
                                                    .getName()
                                                    .equals(Void.class
                                                            .getName())) {
                                                ((Activity) rootView
                                                        .getContext())
                                                        .startActivityForResult(
                                                                ((BaseSupportActivity) rootView
                                                                        .getContext())
                                                                        .getNextIntent(
                                                                                new Bundle(),
                                                                                rootView.getContext(),
                                                                                method.getAnnotation(
                                                                                        Background.class)
                                                                                        .onOKRedirect()),
                                                                0);
                                            }
                                        } else {
                                            MessagesForUser.showMessage(
                                                    ((Activity) rootView
                                                            .getContext()),
                                                    method.getAnnotation(
                                                            Background.class)
                                                            .onKO(),
                                                    Toast.LENGTH_LONG,
                                                    Level.SEVERE);
                                        }
                                    }
                                }.execute(parent.getItemAtPosition(position));
                            } else {
                                try {
                                    method.invoke(object,
                                            parent.getItemAtPosition(position));
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                } catch (RuntimeException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            } catch (IllegalArgumentException e) {
                if (debugEnabled)
                    e.printStackTrace();
                throw new ConfigurationException(e);
            }
        }
    }
}
