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

import java.util.logging.Level;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.VoidFragment;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.utils.InjectionUtils;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.ReflectionUtils;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ReflectionException;

@SuppressLint("Registered")
public class BaseSupportActivity extends AppCompatActivity {

    @SuppressWarnings("rawtypes")
    protected Intent getNextIntent(Bundle bundle, View view, Class clazz) {
        Intent intent = new Intent(view.getContext(), clazz);
        if (bundle != null)
            intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.cafedered.midban.annotations.Activity annotation = getClass()
                .getAnnotation(com.cafedered.midban.annotations.Activity.class);
        if (annotation != null) {
            setContentView(annotation.layout());
            getSupportActionBar().setDisplayHomeAsUpEnabled(
                    annotation.displayAppIcon());
            getSupportActionBar().setLogo(R.drawable.transparent);
            if (annotation.displayAppIcon()) {
//                ImageView view = (ImageView) findViewById(android.R.id.home);
//                view.setPadding(0, 0, 30, 0);
//                view.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        finish();
//                    }
//                });
            }
            getSupportActionBar().setTitle(annotation.title());
            try {
                InjectionUtils.injectAnnotatedFieldsAndMethods(
                        findViewById(annotation.rootView()), this);
            } catch (ConfigurationException e) {
                if (LoggerUtil.isDebugEnabled())
                    e.printStackTrace();
            }
            if (savedInstanceState == null
                    && !(annotation.initFragment().equals(VoidFragment.class))) {
                try {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(annotation.fragmentContainerView(),
                                    (Fragment) ReflectionUtils
                                            .createObject(annotation
                                                    .initFragment())).commit();
                } catch (ReflectionException e) {
                    MessagesForUser.showMessage(this,
                            R.string.error_fragment_cannot_be_invoked,
                            Toast.LENGTH_LONG, Level.SEVERE);
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public Intent getNextIntent(Bundle bundle, Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        if (bundle != null)
            intent.putExtras(bundle);
        return intent;
    }

    protected void putValueInContext(String name, Object value) {
        ((MidbanApplication) getApplicationContext()).getValues().put(
                MidbanApplication.PREFIX + name, value);
    }

    protected void removeValueInContext(String name) {
        ((MidbanApplication) getApplicationContext()).getValues().remove(
                MidbanApplication.PREFIX + name);
    }

    protected Object getValueFromContext(String name) {
        return ((MidbanApplication) getApplicationContext()).getValues().get(
                MidbanApplication.PREFIX + name);
    }
}
