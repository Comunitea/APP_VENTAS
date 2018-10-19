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
package com.cafedered.midban.entities;

import java.io.Serializable;

import com.cafedered.midban.service.repositories.ImageCache;
import com.cafedered.midban.utils.ImageUtil;

@SuppressWarnings("serial")
public abstract class BaseEntity implements Serializable {
    protected Long id;
    protected byte[] image;

    public abstract Long getId();

    public abstract void setId(Long id);

    protected BaseEntity() {
        super();
    }

    protected BaseEntity(Long id) {
        super();
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
        if (image != null) {
            if (!ImageCache.getInstance().exists(
                    this.getClass().toString() + getId()))
                ImageCache.getInstance().putInCache(
                        this.getClass().toString() + getId(),
                        ImageUtil.byteArrayToBitmap(image));
        }
    }
}
