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

import java.util.logging.Level;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.midban.R;

public class MessagesForUser {

    public static void showMessage(View view, String message,
            int duration, Level level) {
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) view.findViewById(R.id.custom_toast_layout));
        TextView text = (TextView) layout.findViewById(R.id.textToShow);
        text.setText(message);
        if (level.equals(Level.INFO)) {
            layout.setBackgroundColor(view.getResources().getColor(
                    R.color.ok_background));
        }
        if (level.equals(Level.WARNING)) {
            layout.setBackgroundColor(view.getResources().getColor(
                    R.color.warn_background));
        }
        if (level.equals(Level.SEVERE)) {
            layout.setBackgroundColor(view.getResources().getColor(
                    R.color.error_background));
        }

        Toast toast = new Toast(view.getContext());
        toast.setGravity(Gravity.TOP, 5, 5);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }

    public static void showMessage(Activity activity, int resource,
            int duration, Level level) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) activity.findViewById(R.id.custom_toast_layout));
        TextView text = (TextView) layout.findViewById(R.id.textToShow);
        text.setText(activity.getResources().getString(resource));
        if (level.equals(Level.INFO)) {
            layout.setBackgroundColor(activity.getResources().getColor(
                    R.color.ok_background));
        }
        if (level.equals(Level.WARNING)) {
            layout.setBackgroundColor(activity.getResources().getColor(
                    R.color.warn_background));
        }
        if (level.equals(Level.SEVERE)) {
            layout.setBackgroundColor(activity.getResources().getColor(
                    R.color.error_background));
        }

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.TOP, 5, 5);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }

    public static void showMessage(View view, int resource,
			int duration, Level level) {
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
		View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) view.findViewById(R.id.custom_toast_layout));
		TextView text = (TextView) layout.findViewById(R.id.textToShow);
        text.setText(view.getResources().getString(resource));
		if (level.equals(Level.INFO)) {
            layout.setBackgroundColor(view.getResources().getColor(
					R.color.ok_background));
		}
		if (level.equals(Level.WARNING)) {
            layout.setBackgroundColor(view.getResources().getColor(
					R.color.warn_background));
		}
		if (level.equals(Level.SEVERE)) {
            layout.setBackgroundColor(view.getResources().getColor(
					R.color.error_background));
		}

        Toast toast = new Toast(view.getContext());
		toast.setGravity(Gravity.TOP, 5, 5);
		toast.setDuration(duration);
		toast.setView(layout);
		toast.show();
	}
}
