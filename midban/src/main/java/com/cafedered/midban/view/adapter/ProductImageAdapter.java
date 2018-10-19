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
package com.cafedered.midban.view.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.cafedered.midban.entities.Product;
import com.cafedered.midban.service.repositories.ImageCache;
import com.cafedered.midban.utils.ImageUtil;
import com.cafedered.midban.view.fragments.ProductCardFragment;

public class ProductImageAdapter extends BaseAdapter {
    private Context context;
    private List<byte[]> images = new ArrayList<byte[]>();
    private Long productId;
    private ProductCardFragment fragment;

    public ProductImageAdapter(ProductCardFragment fragment, Context c, List<byte[]> bitmaps, Long productId) {
        context = c;
        images = bitmaps;
        this.productId = productId;
        this.fragment = fragment;
    }

    public int getCount() {
        return images.size();
    }

    public Object getItem(int position) {
        return ImageCache.getInstance().getFromCache(
                Product.class.getName() + productId + "" + position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        ImageView iview;
        if (view == null) {
            iview = new ImageView(context);
            iview.setLayoutParams(new GridView.LayoutParams(120, 120));
            iview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iview.setPadding(5, 5, 5, 5);
        } else {
            iview = (ImageView) view;
        }
        if (!ImageCache.getInstance().exists(
                Product.class.getName() + productId + "" + position))
            ImageCache.getInstance().putInCache(
                    Product.class.getName() + productId + "" + position,
                    ImageUtil.byteArrayToBitmap(images.get(position)));
        iview.setImageBitmap(ImageCache.getInstance().getFromCache(
                Product.class.getName() + productId + "" + position));
        return iview;
    }
}
