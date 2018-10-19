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
package com.cafedered.midban.view.dialogs;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.Task;
import com.cafedered.midban.service.repositories.TaskRepository;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.fragments.CalendarFragment;

public class FilterTasksDialog extends Dialog {

    private String text;
    private Map<Date, List<Task>> tasksOperator = new HashMap<Date, List<Task>>();

    public Map<Date, List<Task>> getTasksOperator() {
        return tasksOperator;
    }

    private static FilterTasksDialog instance = null;

    public static FilterTasksDialog getInstance(Context context,
            final CalendarFragment fragment, Map<Date, List<Task>> tasks,
            boolean first) {
        if (first)
            instance = null;
        if (instance == null)
            instance = new FilterTasksDialog(context, fragment, tasks);
        return instance;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public FilterTasksDialog(Context context, final CalendarFragment fragment,
            Map<Date, List<Task>> tasks) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_task_filters);
        final EditText freeText = (EditText) findViewById(R.id.dialog_task_filters_editext_free);

        Button apply = (Button) findViewById(R.id.dialog_task_filters_button_apply);
        apply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                text = freeText.getText().toString();
                tasksOperator = new HashMap<Date, List<Task>>();
                Partner partnerExample = new Partner();
                if (!text.isEmpty())
                    partnerExample.setName(text);
                try {
                    List<Task> tasksExample = TaskRepository.getInstance()
                            .getAll(10000, 0);
                    for (Task task : tasksExample) {
                        if (text.length() == 0
                                || task.getPartner().getName().toLowerCase()
                                        .contains(text.toLowerCase())) {
                            if (tasksOperator.get(DateUtil.parseDate(
                                    task.getInitDate(), "yyyy-MM-dd")) == null)
                                tasksOperator.put(DateUtil.parseDate(
                                        task.getInitDate(), "yyyy-MM-dd"),
                                        new ArrayList<Task>());
                            tasksOperator.get(
                                    DateUtil.parseDate(task.getInitDate(),
                                            "yyyy-MM-dd")).add(task);
                        }
                    }
                    instance.cancel();
                } catch (ServiceException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                } catch (ConfigurationException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                } catch (ParseException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                }
            }
        });

        TextView clearFilters = (TextView) findViewById(R.id.dialog_task_filters_clear);
        clearFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                freeText.setText("");
            }
        });
    }

}
