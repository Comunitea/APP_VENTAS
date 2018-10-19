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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.Task;
import com.cafedered.midban.service.repositories.TaskRepository;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.PortadaActivity;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.dialogs.SelectPartnerDialog;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

@Fragment(R.layout.fragment_new_calendar_task)
public class NewTaskFragment extends BaseSupportFragment {

    @Wire(view = R.id.fragment_new_calendar_task_type)
    private Spinner taskTypes;
    @Wire(view = R.id.fragment_new_calendar_task_partner)
    private TextView partnerText;
    @Wire(view = R.id.fragment_new_calendar_task_init_date_et)
    private EditText initDate;
    @Wire(view = R.id.fragment_new_calendar_task_init_time_et)
    private TimePicker initHour;
    @Wire(view = R.id.fragment_new_calendar_task_end_date_et)
    private EditText endDate;
    @Wire(view = R.id.fragment_new_calendar_task_end_time_et)
    private TimePicker endHour;
    @Wire(view = R.id.fragment_new_calendar_task_address)
    private EditText address;
    @Wire(view = R.id.fragment_new_calendar_task_title)
    private EditText title;
    @Wire(view = R.id.fragment_new_calendar_task_description_et)
    private EditText description;

    private Partner partner;
    private Task editionTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        setHasOptionsMenu(true);
        initHour.setIs24HourView(true);
        endHour.setIs24HourView(true);
        if (getValueFromContext(ContextAttributes.PARTNER_TO_TASK) != null)
            partner = (Partner) getValueFromContext(ContextAttributes.PARTNER_TO_TASK);
        loadFieldsIfNeeded();
        return rootView;
    }

    private void loadFieldsIfNeeded() {
        if (getValueFromContext(ContextAttributes.TASK_TO_EDIT) != null) {
            editionTask = (Task) getValueFromContext(ContextAttributes.TASK_TO_EDIT);
            partner = editionTask.getPartner();
            partnerText.setText(partner.getName());
            address.setText(editionTask.getAddress());
            try {
                initDate.setText(DateUtil.toFormattedString(DateUtil.parseDate(
                        editionTask.getInitDate(), "yyyy-MM-dd"), "dd.MM.yyyy"));
                endDate.setText(DateUtil.toFormattedString(DateUtil.parseDate(
                        editionTask.getEndDate(), "yyyy-MM-dd"), "dd.MM.yyyy"));
            } catch (Exception e) {
                // unreachable
            }
            initHour.setCurrentHour(Integer.parseInt(editionTask.getInitHour()
                    .split(":")[0]));
            initHour.setCurrentMinute(Integer.parseInt(editionTask
                    .getInitHour().split(":")[1]));
            endHour.setCurrentHour(Integer.parseInt(editionTask.getEndHour()
                    .split(":")[0]));
            endHour.setCurrentMinute(Integer.parseInt(editionTask.getEndHour()
                    .split(":")[1]));
            title.setText(editionTask.getTitle());
            description.setText(editionTask.getDescription());
        } else if (partner != null) {
            partnerText.setText(partner.getName());
            address.setText(partner.getCompleteAddress());
        }
    }

    @Click(views = { R.id.fragment_new_calendar_task_init_date_et,
            R.id.fragment_new_calendar_task_end_date_et })
    public void selectDate(final View v) {
        final CaldroidFragment dialogCaldroidFragment = CaldroidFragment
                .newInstance(
                        getResources()
                                .getString(
                                        R.string.fragment_new_calendar_task_select_date),
                        Calendar.getInstance().get(Calendar.MONTH) + 1,
                        Calendar.getInstance().get(Calendar.YEAR));
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        try {
            Map<Date, List<Task>> tasks = TaskRepository.getInstance()
                    .getTasksInFuture();
            HashMap<Date, Integer> backgrounds = new HashMap<Date, Integer>();
            for (Date aDate : tasks.keySet()) {
                if (tasks.get(aDate).size() == 1)
                    backgrounds.put(aDate, R.drawable.agenda_nuevo_1tarea);
                if (tasks.get(aDate).size() == 2)
                    backgrounds.put(aDate, R.drawable.agenda_nuevo_2tareas);
                if (tasks.get(aDate).size() >= 3)
                    backgrounds.put(aDate, R.drawable.agenda_nuevo_3tareas);
            }
            dialogCaldroidFragment.setBackgroundResourceForDates(backgrounds);
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        args.putBoolean(CaldroidFragment.SHOW_BIG_DAY, true);
        args.putBoolean(CaldroidFragment.BUTTONS_ENABLED, true);
        dialogCaldroidFragment.setArguments(args);
        dialogCaldroidFragment.setMinDate(new Date());
        final CaldroidListener listener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                if (date != null)
                    ((EditText) v).setText(DateUtil.toFormattedString(date,
                            "dd.MM.yyyy"));
                else
                    ((EditText) v).setText("");
            }
        };
        dialogCaldroidFragment.setCaldroidListener(listener);
        dialogCaldroidFragment.show(getFragmentManager(), "CALDROID_FRAG");
    }

    @Click(view = R.id.fragment_new_calendar_task_cancel_btn)
    public void cancel() {
        removeValueInContext(ContextAttributes.PARTNER_TO_TASK);
        removeValueInContext(ContextAttributes.TASK_TO_EDIT);
        getActivity().finish();
    }

    @Click(view = R.id.fragment_new_calendar_task_confirm_btn)
    public void createTask() {
        List<String> validationErrors = validateData();
        if (validationErrors.size() > 0) {
            String completeMessage = "";
            for (String error : validationErrors) {
                completeMessage += "\n" + error;
            }
            MessagesForUser.showMessage(getView(), completeMessage,
                    Toast.LENGTH_LONG, Level.SEVERE);
        } else {
            final Task task;
            if (editionTask == null)
                task = new Task();
            else
                task = editionTask;
            task.setAddress(address.getText().toString());
            task.setDescription(description.getText().toString());
            try {
                task.setInitDate(DateUtil.toFormattedString(DateUtil.parseDate(
                        initDate.getText().toString(), "dd.MM.yyyy"),
                        "yyyy-MM-dd"));
                if (endDate.getText() != null
                        && endDate.getText().toString().length() > 0)
                    task.setEndDate(DateUtil.toFormattedString(DateUtil
                            .parseDate(endDate.getText().toString(),
                                    "dd.MM.yyyy"), "yyyy-MM-dd"));
            } catch (ParseException e) {
                // unreachable since it is validated in method validateData()
            }
            task.setEndHour(String.format("%02d", endHour.getCurrentHour())
                    + ":" + String.format("%02d", endHour.getCurrentMinute()));
            task.setInitHour(String.format("%02d", initHour.getCurrentHour())
                    + ":" + String.format("%02d", initHour.getCurrentMinute()));
            task.setPartnerId(partner.getId());
            task.setTitle(title.getText().toString());
            task.setType(taskTypes.getSelectedItem().toString());
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        TaskRepository.getInstance().saveOrUpdate(task);
                        return true;
                    } catch (ServiceException e) {
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    super.onPostExecute(result);
                    if (result) {
                        MessagesForUser.showMessage(getView(), R.string.saved,
                                Toast.LENGTH_LONG, Level.INFO);
                        removeValueInContext(ContextAttributes.TASK_TO_EDIT);
                        putValueInContext(ContextAttributes.TASK_TO_DETAIL,
                                task);
                        getActivity().finish();
                    } else
                        MessagesForUser.showMessage(getView(),
                                R.string.cannot_save, Toast.LENGTH_LONG,
                                Level.SEVERE);
                }
            }.execute();
        }

    }

    private List<String> validateData() {
        List<String> results = new ArrayList<String>();
        if (partner == null)
            results.add(getResources().getString(
                    R.string.fragment_calendar_new_task_partner_null));
        if (title.getText() == null || title.getText().toString().length() == 0)
            results.add(getResources().getString(
                    R.string.fragment_calendar_new_task_title_null));
        if (address.getText() == null
                || address.getText().toString().length() == 0)
            results.add(getResources().getString(
                    R.string.fragment_calendar_new_task_address_null));
        if (initDate.getText() == null
                || initDate.getText().toString().length() == 0)
            results.add(getResources().getString(
                    R.string.fragment_calendar_new_task_init_date_null));
        else {
            try {
                DateUtil.parseDate(initDate.getText().toString(), "dd.MM.yyyy");
            } catch (ParseException e) {
                results.add(getResources().getString(
                        R.string.fragment_calendar_new_task_init_date_null));
            }
        }
        if ((endDate.getText() == null || endDate.getText().toString().length() == 0)
                && endHour.getCurrentHour() < initHour.getCurrentHour()
                || (endHour.getCurrentHour().equals(initHour.getCurrentHour()) && endHour
                        .getCurrentMinute() < initHour.getCurrentMinute()))
            results.add(getResources()
                    .getString(
                            R.string.fragment_calendar_new_task_end_date_less_than_init_date));
        // if (endDate.getText() == null
        // || endDate.getText().toString().length() == 0)
        // results.add(getResources().getString(
        // R.string.fragment_calendar_new_task_end_date_null));
        // else {
        // try {
        // DateUtil.parseDate(endDate.getText().toString(), "dd.MM.yyyy");
        // } catch (ParseException e) {
        // results.add(getResources().getString(
        // R.string.fragment_calendar_new_task_end_date_null));
        // }
        // }
        // if (description.getText() == null
        // || description.getText().toString().length() == 0)
        // results.add(getResources().getString(
        // R.string.fragment_calendar_new_task_description_null));
        return results;
    }

    @Click(view = R.id.fragment_new_calendar_task_partner)
    public void selectPartner() {
        final SelectPartnerDialog selectDialog = new SelectPartnerDialog(
                getView().getContext());
        DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                partner = selectDialog.getPartner();
                if (partner != null) {
                    partnerText.setText(partner.getName());
                    address.setText(partner.getCompleteAddress());
                }
            }
        };
        selectDialog.setCanceledOnTouchOutside(false);
        selectDialog.openDialogForSelectingPartner(
                getResources().getString(
                        R.string.fragment_new_calendar_task_select_partner),
                ContextAttributes.PARTNER_TO_TASK, null, dismissListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.new_calendar_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
        case R.id.home_item:
            startActivityForResult(
                    getNextIntent(new Bundle(), getView(),
                            PortadaActivity.class), 0);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
