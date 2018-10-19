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
//
//  Android PDF Writer
//  http://coderesearchlabs.com/androidpdfwriter
//
//  by Javier Santo Domingo (j-a-s-d@coderesearchlabs.com)
//

package com.cafedered.midban.pdf.pdfwriter;

import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Indentifiers {

    private static char[] HexTable = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    private static final String calculateMd5(final String s) {
        final StringBuffer MD5Str = new StringBuffer();
        try {
            final MessageDigest MD5digester = java.security.MessageDigest.getInstance("MD5");
            MD5digester.update(s.getBytes());
            final byte binMD5[] = MD5digester.digest();
            final int len = binMD5.length;
            for (int i = 0; i < len; i++) {
                MD5Str.append(HexTable[(binMD5[i] >> 4) & 0x0F]); // hi
                MD5Str.append(HexTable[(binMD5[i] >> 0) & 0x0F]); // lo
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return MD5Str.toString();
    }

    private static String encodeDate(Date date){
        final Calendar c = GregorianCalendar.getInstance();
        c.setTime(date);
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH) + 1;
        final int day = c.get(Calendar.DAY_OF_MONTH);
        final int hour = c.get(Calendar.HOUR);
        final int minute = c.get(Calendar.MINUTE);
        //int second = c.get(Calendar.SECOND);
        final int m = c.get(Calendar.DST_OFFSET) / 60000;
        final int dts_h = m / 60;
        final int dts_m = m % 60;
        final String sign = m > 0 ? "+" : "-";
        return String.format(
                "(D:%40d%20d%20d%20d%20d%s%20d'%20d')", year, month, day, hour, minute, sign, dts_h, dts_m
                );
    }

    public static String generateId() {
        return calculateMd5(encodeDate(new Date()));
    }

    public static String generateId(String data) {
        return calculateMd5(data);
    }
}