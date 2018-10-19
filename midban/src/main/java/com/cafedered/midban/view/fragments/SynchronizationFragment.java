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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.async.SynchronizationAsyncTask;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.SynchronizationSummary;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.SynchronizationSummaryRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.PortadaActivity;
import com.cafedered.midban.view.adapter.SynchronizationListItemAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;

@Fragment(R.layout.fragment_synchronization)
public class SynchronizationFragment extends BaseSupportFragment {

    @Wire(view = R.id.fragment_synchronization_table_synchronizations)
    ListView list;
    @Wire(view = R.id.fragment_synchronization_progress_bar)
    ProgressBar progressBar;
    @Wire(view = R.id.fragment_synchronization_messages_tv)
    TextView messagesArea;
    SynchronizationFragment fragment;
    SynchronizationListItemAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list = (ListView) getActivity().findViewById(R.id.fragment_synchronization_table_synchronizations);
        loadList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        setHasOptionsMenu(true);
        loadList();
        return rootView;
    }


    private void loadList() {
        new AsyncTask<Void, Void, List<SynchronizationSummary>>() {
            @Override
            protected List<SynchronizationSummary> doInBackground(
                    Void... params) {
                try {
                    List<SynchronizationSummary> theList = SynchronizationSummaryRepository
                            .getInstance().getAll(0, 500000000);
                    Collections.reverse(theList);
                    if (theList.size() > 5) {
                        return theList.subList(0, 5);
                    } else
                        return theList;
                } catch (ConfigurationException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                } catch (ServiceException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                }
                return new ArrayList<SynchronizationSummary>();
            }

            @Override
            protected void onPostExecute(List<SynchronizationSummary> result) {
                super.onPostExecute(result);
                adapter = new SynchronizationListItemAdapter(
                        SynchronizationFragment.this, result);
                list.setAdapter(adapter);
            }
        }.execute();
    }

    @Click(view = R.id.fragment_synchronization_button_synchronize)
    public void synchronizate() {
        loadList();
        SynchronizationAsyncTask instance = SynchronizationAsyncTask.getInstance(progressBar, messagesArea, getActivity());
        if (instance.isCancelled() ||
                instance.getCurrentProgress() == null || instance.getCurrentProgress() == -1 || instance.getCurrentProgress() == instance.MAX_PROGRESS)
            SynchronizationAsyncTask.getInstance(progressBar, messagesArea, getActivity())
                .execute((User) MidbanApplication
                        .getValueFromContext(ContextAttributes.LOGGED_USER));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.synchronization_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        if (item.getItemId() == R.id.home_item_synchronization) {
            startActivityForResult(
                    getNextIntent(new Bundle(), getView(),
                            PortadaActivity.class), 0);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
