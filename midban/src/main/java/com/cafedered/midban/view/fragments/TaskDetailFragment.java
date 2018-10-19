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
package com.cafedered.midban.view.fragments;

import java.text.ParseException;
import java.util.logging.Level;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Task;
import com.cafedered.midban.service.repositories.TaskRepository;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.view.activities.GoogleMapsActivity;
import com.cafedered.midban.view.activities.NewCalendarTaskActivity;
import com.cafedered.midban.view.activities.PortadaActivity;
import com.cafedered.midban.view.base.BaseSupportFragment;

@Fragment(R.layout.fragment_task_detail)
public class TaskDetailFragment extends BaseSupportFragment {

    @Wire(view = R.id.fragment_task_detail_partner_name_tv)
    private TextView partnerName;
    @Wire(view = R.id.fragment_task_detail_partner_code_tv)
    private TextView partnerCode;
    @Wire(view = R.id.fragment_task_detail_task_type_tv)
    private TextView taskType;
    @Wire(view = R.id.fragment_task_detail_task_title)
    private TextView taskTitle;
    @Wire(view = R.id.fragment_task_detail_task_date)
    private TextView taskDate;
    @Wire(view = R.id.fragment_task_detail_task_hours)
    private TextView taskHours;
    @Wire(view = R.id.fragment_task_detail_task_complete_address)
    private TextView taskAddress;
    @Wire(view = R.id.fragment_task_detail_task_description)
    private TextView taskDescription;

    private Task task;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        setHasOptionsMenu(true);
        loadData();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        task = (Task) MidbanApplication
                .getValueFromContext(ContextAttributes.TASK_TO_DETAIL);
        partnerName.setText(task.getPartner().getName());
        partnerCode.setText(task.getPartnerId().toString());
        taskType.setText(task.getType());
        taskTitle.setText(task.getTitle());
        try {
            if (task.getInitDate().equals(task.getEndDate())
                    || task.getEndDate() == null)
                taskDate.setText(DateUtil.toFormattedString(
                        DateUtil.parseDate(task.getInitDate(), "yyyy-MM-dd"),
                        "dd.MM.yyyy"));
            else
                taskDate.setText(DateUtil.toFormattedString(
                        DateUtil.parseDate(task.getInitDate(), "yyyy-MM-dd"),
                        "dd.MM.yyyy")
                        + " - "
                        + DateUtil.toFormattedString(DateUtil.parseDate(
                                task.getEndDate(), "yyyy-MM-dd"), "dd.MM.yyyy"));
        } catch (ParseException e) {
            // unreachable
        }
        taskHours.setText(task.getInitHour() + " - " + task.getEndHour());
        taskAddress.setText(task.getAddress());
        taskDescription.setText(task.getDescription());
    }

    @Click(view = R.id.fragment_task_detail_task_complete_address)
    public void goToGeolocalization() {
        Bundle bundle = new Bundle();
        bundle.putString("partner_address", task.getAddress());
        bundle.putString("partner_name", task.getPartner().getName());
        bundle.putString("partner_code", task.getPartnerId().toString());
        startActivityForResult(
                getNextIntent(bundle, getView(), GoogleMapsActivity.class), 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.task_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
        case R.id.edit_item:
            MidbanApplication.putValueInContext(ContextAttributes.TASK_TO_EDIT,
                    task);
            startActivityForResult(
                    getNextIntent(new Bundle(), getView(),
                            NewCalendarTaskActivity.class), 0);
            return true;
        case R.id.home_item:
            startActivityForResult(
                    getNextIntent(new Bundle(), getView(),
                            PortadaActivity.class), 0);
            return true;
        case R.id.delete_item:
            new AlertDialog.Builder(getView().getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(
                            getResources().getString(
                                    R.string.confirmation_dialog_generic_title))
                    .setMessage(
                            getResources().getString(R.string.sure_to_delete))
                    .setPositiveButton(R.string.confirm,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(
                                        final DialogInterface dialog,
                                        int which) {
                                    new AsyncTask<Void, Void, Boolean>() {
                                        @Override
                                        protected Boolean doInBackground(
                                                Void... params) {
                                            try {
                                                TaskRepository.getInstance()
                                                        .delete(task.getId());
                                            } catch (Exception e) {
                                                return false;
                                            }
                                            return true;
                                        }

                                        @Override
                                        protected void onPostExecute(
                                                Boolean result) {
                                            super.onPostExecute(result);
                                            if (result) {
                                                MessagesForUser.showMessage(
                                                        getView(),
                                                                getResources()
                                                                        .getString(
                                                                                R.string.deleted),
                                                        Toast.LENGTH_LONG,
                                                        Level.INFO);
                                                dialog.dismiss();
                                                getActivity().finish();
                                            } else {
                                                MessagesForUser.showMessage(
                                                        getView(),
                                                                getResources()
                                                                        .getString(
                                                                                R.string.cannot_delete),
                                                        Toast.LENGTH_LONG,
                                                        Level.INFO);
                                            }
                                        }
                                    }.execute();
                                }

                            }).setNegativeButton(R.string.cancel, null).show();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
