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
package com.cafedered.midban.service.repositories;

import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cafedered.midban.R;

public class ImageCache {
    private int LIMIT_SIZE = 1000;
    private Map<String, Bitmap> objects = new LinkedHashMap<String, Bitmap>(
            LIMIT_SIZE);
    private static ImageCache instance;

    public static ImageCache getInstance() {
        if (null == instance) {
            instance = new ImageCache();
            instance.putInCache("not_available", BitmapFactory.decodeResource(
                    null, R.drawable.not_available));
        }
        return instance;
    }

    public void putInCache(String key, Bitmap object) {
        if (objects.size() == LIMIT_SIZE)
            objects.remove(0);
        objects.put(key, object);
    }

    public boolean exists(String key) {
        return objects.containsKey(key);
    }

    public Bitmap getFromCache(String key) {
        return objects.get(key);
    }
}
