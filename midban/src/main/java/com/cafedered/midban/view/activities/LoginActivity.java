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
package com.cafedered.midban.view.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Activity;
import com.cafedered.midban.annotations.Background;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Configuration;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.ConfigurationRepository;
import com.cafedered.midban.service.repositories.UserRepository;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.SessionFactory;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.utils.exceptions.UserNotFoundException;
import com.cafedered.midban.view.base.BaseSupportActivity;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

@Activity(layout = R.layout.activity_login,
        title = R.string.activity_login_title,
        rootView = R.id.activity_login_container)
public class LoginActivity extends BaseSupportActivity {

    @Wire(view = R.id.activity_login_username)
    private EditText username;
    @Wire(view = R.id.activity_login_password)
    private EditText password;

    private Configuration configuration;


    @Override
    protected void onResume() {
        super.onResume();
        MidbanApplication.removeValueInContext(ContextAttributes.LOGGED_USER);
        try {
            configuration = null;
            configuration = ConfigurationRepository.getInstance()
                    .getConfiguration();
        } catch (Exception e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
            startActivityForResult(
                    getNextIntent(new Bundle(), this,
                            ConfigurationActivity.class), 0);
        }
        if (configuration == null) {
            startActivityForResult(
                    getNextIntent(new Bundle(), this,
                            ConfigurationActivity.class), 0);
        }
        if (UserRepository.getInstance().isUserLoggedLastTwoHours()) {
            MidbanApplication.putValueInContext(ContextAttributes.LOGGED_USER, UserRepository.getInstance().getLastUserLogged());
            startActivity(getNextIntent(new Bundle(), LoginActivity.this, PortadaActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MidbanApplication.removeValueInContext(ContextAttributes.LOGGED_USER);
        configuration = null;
        try {
            configuration = ConfigurationRepository.getInstance()
                    .getConfiguration();
            if (configuration == null) {
                startActivityForResult(
                        getNextIntent(new Bundle(), this,
                                ConfigurationActivity.class), 0);
            } else {
                username.setText(configuration.getUsername());
            }
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
            startActivityForResult(
                    getNextIntent(new Bundle(), this,
                            ConfigurationActivity.class), 0);
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
            startActivityForResult(
                    getNextIntent(new Bundle(), this,
                            ConfigurationActivity.class), 0);
        }
        if (UserRepository.getInstance().isUserLoggedLastTwoHours()) {
            MidbanApplication.putValueInContext(ContextAttributes.LOGGED_USER, UserRepository.getInstance().getLastUserLogged());
            startActivity(getNextIntent(new Bundle(), LoginActivity.this, PortadaActivity.class));
        }
        ImageView iv = findViewById(R.id.activity_login_midban_logo_iv);
        iv.setImageResource(MidbanApplication.getResourceLogo());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.login_action_configuration:
            startActivityForResult(
                    getNextIntent(new Bundle(), this,
                            ConfigurationActivity.class), 0);
            break;
        default:
            break;
        }

        return true;
    }

//    @Background(onOKRedirect = PortadaActivity.class,
//            onOK = R.string.activity_login_ok,
//            onKO = R.string.activity_login_ko,
//            showDialog = true)
    @Click(view = R.id.activity_login_button_enter)
    public void login() throws ServiceException, UserNotFoundException {
        final String usernameString = username.getText().toString();
        final String paswordString = password.getText().toString();
        new AsyncTask<Void, Void, Boolean>() {

            ProgressDialog progress;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = ProgressDialog.show(LoginActivity.this, getResources().getString(R.string.loading),
                        getResources().getString(R.string.loading), true);
            }

            @Override
            protected Boolean doInBackground(
                    Void... params) {
                try {
                    SessionFactory.getInstance(usernameString, paswordString);
                    if (UserRepository.getInstance().getByExample(User.create(usernameString,
                            null, null), Restriction.AND, true, 0, 1) == null || UserRepository.getInstance().getByExample(User.create(usernameString,
                            null, null), Restriction.AND, true, 0, 1).size() == 0)
                        UserRepository.getInstance().synchronizeUsers(usernameString, paswordString);
                    MidbanApplication.putValueInContext(ContextAttributes.LOGGED_USER,
                            User.create(
                                    usernameString, paswordString, SessionFactory.getInstance(usernameString,
                                            paswordString).getUserId(usernameString,
                                            paswordString)));
                    User user = new User();
                    user.setLogin(usernameString);
                    List<User> users =  UserRepository.getInstance().getByExample(user, Restriction.AND, true, 0, 100);
                    if  (users != null && users.size() > 0)
                        user = users.get(0);
                    user.setPasswd(paswordString);
                    user.setFechaLogin(DateUtil.toFormattedString(new Date(), "ddMMyyyyHHmmss"));

                    // comprobación de si el usuario pertenece a la compañía
/*
                    if (!(MidbanApplication.activeCompany == user.getCompanyId().intValue())){
                        return false;
                    }
*/


                    UserRepository.getInstance().saveOrUpdate(user);
                    return true;
                } catch (Exception e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                    boolean userOk = false;
                    try {
                        userOk = UserRepository.getInstance().authenticateUserInDB(
                                usernameString,
                                paswordString);
                    } catch (ServiceException e1) {
                        if (LoggerUtil.isDebugEnabled())
                            e1.printStackTrace();
                        return false;
                    }
                    if (userOk) {
                        try {
                            User user = User.create(
                                    usernameString,
                                    paswordString,
                                    UserRepository.getInstance().getByExample(
                                            User.create(usernameString,null, null),
                                            Restriction.AND,
                                            true,
                                            0,
                                            1).get(0).getId().intValue());
                            user.setFechaLogin(DateUtil.toFormattedString(new Date(), "ddMMyyyyHHmmss"));
                            UserRepository.getInstance().saveOrUpdate(user);
                            MidbanApplication.putValueInContext(
                                    ContextAttributes.LOGGED_USER, user);
                        } catch (ServiceException e1) {
                            return false;
                        }
                        return true;
                    } else
                        return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                progress.dismiss();
                if (result) {
                        MessagesForUser.showMessage(
                                LoginActivity.this,
                                R.string.activity_login_ok,
                                Toast.LENGTH_LONG,
                                Level.INFO);
                    startActivity(getNextIntent(new Bundle(), LoginActivity.this, PortadaActivity.class));
                } else {
                    MessagesForUser.showMessage(
                            LoginActivity.this,
                            R.string.activity_login_ko,
                            Toast.LENGTH_LONG,
                            Level.SEVERE);
                }
            }
        }.execute();
    }
}
