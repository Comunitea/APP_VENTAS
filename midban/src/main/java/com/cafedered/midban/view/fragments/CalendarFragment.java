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

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.ItemClicked;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.entities.Task;
import com.cafedered.midban.service.repositories.TaskRepository;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.NewCalendarTaskActivity;
import com.cafedered.midban.view.activities.PortadaActivity;
import com.cafedered.midban.view.activities.TaskDetailActivity;
import com.cafedered.midban.view.adapter.TasksAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.dialogs.FilterTasksDialog;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

@Fragment(R.layout.fragment_calendar)
public class CalendarFragment extends BaseSupportFragment {

    @Wire(view = R.id.fragment_calendar_selected_date_tv)
    TextView selectedDate;
    @Wire(view = R.id.fragment_calendar_listview)
    ListView list;
    Map<Date, List<Task>> tasks;

    private boolean isFirst = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        setHasOptionsMenu(true);
        prepareCalendar(rootView, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareCalendar(getView(), false);
    }

    private void prepareCalendar(View rootView, boolean alreadySetTasks) {
        final CaldroidFragment caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        caldroidFragment.setArguments(args);
        try {
            if (!alreadySetTasks)
                tasks = TaskRepository.getInstance()
                    .getTasksInFuture();
            HashMap<Date, Integer> backgrounds = new HashMap<Date, Integer>();
            for (Date aDate : tasks.keySet()) {
                if (tasks.get(aDate).size() == 1)
                    backgrounds.put(aDate, R.drawable.agenda_1tarea);
                if (tasks.get(aDate).size() == 2)
                    backgrounds.put(aDate, R.drawable.agenda_2tareas);
                if (tasks.get(aDate).size() >= 3)
                    backgrounds.put(aDate, R.drawable.agenda_3tareas);
            }
            caldroidFragment.setBackgroundResourceForDates(backgrounds);
            if (tasks.containsKey(getDateFor(new Date()))) {
                list.setAdapter(new TasksAdapter(rootView.getContext(), tasks
                        .get(getDateFor(new Date()))));
            } else {
                list.setAdapter(new TasksAdapter(rootView.getContext(),
                        new ArrayList<Task>()));
            }
            selectedDate.setText(DateUtil.toFormattedString(new Date(),
                    "dd.MM.yyyy"));

        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        CaldroidListener listener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                if (tasks.containsKey(getDateFor(date))) {
                    list.setAdapter(new TasksAdapter(getView().getContext(),
                            tasks.get(getDateFor(date))));
                } else {
                    list.setAdapter(new TasksAdapter(getView().getContext(),
                            new ArrayList<Task>()));
                }
                selectedDate.setText(DateUtil.toFormattedString(date,
                        "dd.MM.yyyy"));
            }
        };
        caldroidFragment.setCaldroidListener(listener);
        FragmentTransaction t = getChildFragmentManager().beginTransaction();
        t.replace(R.id.container, caldroidFragment);
        t.commit();
    }

    private Date getDateFor(Date date) {
        try {
            return DateUtil.parseDate(
                    DateUtil.toFormattedString(date, "ddMMyyyy"), "ddMMyyyy");
        } catch (ParseException e) {
            // unreachable
            return null;
        }
    }

    @ItemClicked(view = R.id.fragment_calendar_listview)
    public void taskSelected(final Task task) {
        putValueInContext(ContextAttributes.TASK_TO_DETAIL, task);
        startActivityForResult(
                getNextIntent(new Bundle(), getView(), TaskDetailActivity.class),
                0);
    }

    @Click(view = R.id.fragment_calendar_new_task_tv)
    public void newTask() {
        startActivityForResult(
                getNextIntent(new Bundle(), getView(),
                        NewCalendarTaskActivity.class), 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.calendar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
        case R.id.search_item:
            final FilterTasksDialog filterDialog = FilterTasksDialog
                    .getInstance(
                    getActivity(), this, tasks, isFirst);
            isFirst = false;
            Window window = filterDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            window.setGravity(Gravity.TOP | Gravity.RIGHT);
            wlp.y = 80;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            filterDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            tasks = filterDialog.getTasksOperator();
                            prepareCalendar(getView(), true);
                        }
                    });
            filterDialog.show();
            return true;
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
