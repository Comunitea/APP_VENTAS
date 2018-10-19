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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class DateUtil {

    public static final String DEFAULT_FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";

    public static String toFormattedString(Date aDate) {
        return toFormattedString(aDate, DEFAULT_FORMAT_DATETIME);
    }

    public static String toFormattedString(Date aDate, String aFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(aFormat);
        return formatter.format(aDate);
    }

    public static Date parseDate(String aDate) throws ParseException {
        return parseDate(aDate, DEFAULT_FORMAT_DATETIME);
    }

    public static Date parseDate(String aDate, String aFormat)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(aFormat);
        return formatter.parse(aDate);
    }

    public static String convertToUTC(String aDate) {
        try {
            Date date = parseDate(aDate);
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
            sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return aDate;
        }
    }
}
