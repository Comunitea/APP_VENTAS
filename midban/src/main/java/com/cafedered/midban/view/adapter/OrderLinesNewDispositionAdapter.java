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

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.OrderLine;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.ProductUom;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.service.repositories.ProductUomRepository;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.fragments.OrderNewDispositionFragment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Level;

public class OrderLinesNewDispositionAdapter extends BaseAdapter {

    private List<OrderLine> lines;
    private static LayoutInflater inflater = null;
    private OrderNewDispositionFragment fragment;

    public OrderLinesNewDispositionAdapter(Context context, List<OrderLine> lines, OrderNewDispositionFragment fragment) {
        this.lines = lines;
        this.fragment = fragment;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return lines.size();
    }

    public Object getItem(int position) {
        return lines.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public ImageView image;
        public TextView code;
        public TextView name;
        public TextView quantityUom;
        public TextView unitUom;
        public TextView quantityUos;
        public TextView unitUos;
        public TextView price;
        public TextView total;
        public EditText discount;
        public ImageView deleteIcon;
        public ImageView lessIconUom;
        public ImageView plusIconUom;
        public ImageView lessIconUos;
        public ImageView plusIconUos;
    }

    public View getView(int position, final View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.order_line_list_item_new_disposition, null);
            holder = new ViewHolder();
            holder.image = (ImageView) vi
                    .findViewById(R.id.order_line_list_item_new_disposition_product_image);
            holder.name = (TextView) vi
                    .findViewById(R.id.order_line_list_item_new_disposition_product_name);
            holder.code = (TextView) vi
                    .findViewById(R.id.order_line_list_item_new_disposition_product_code);
            holder.quantityUom = (TextView) vi
                    .findViewById(R.id.order_line_list_item_new_disposition_quantity_uom);
            holder.unitUom = (TextView) vi
                    .findViewById(R.id.order_line_list_item_new_disposition_unit_uom);
            holder.quantityUos = (TextView) vi
                    .findViewById(R.id.order_line_list_item_new_disposition_quantity_uos);
            holder.unitUos = (TextView) vi
                    .findViewById(R.id.order_line_list_item_new_disposition_unit_uos);
            holder.price = (TextView) vi
                    .findViewById(R.id.order_line_list_item_new_disposition_price);
            holder.total = (TextView) vi
                    .findViewById(R.id.order_line_list_item_new_disposition_total);
            holder.deleteIcon = (ImageView) vi
                    .findViewById(R.id.order_line_list_item_new_disposition_delete_icon);
            holder.lessIconUom = (ImageView) vi.findViewById(R.id.order_line_list_item_new_disposition_less_icon_uom);
            holder.plusIconUom = (ImageView) vi.findViewById(R.id.order_line_list_item_new_disposition_plus_icon_uom);
            holder.lessIconUos = (ImageView) vi.findViewById(R.id.order_line_list_item_new_disposition_less_icon_uos);
            holder.plusIconUos = (ImageView) vi.findViewById(R.id.order_line_list_item_new_disposition_plus_icon_uos);
            holder.discount = (EditText) vi.findViewById(R.id.order_line_list_item_new_disposition_discount);
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();
        final OrderLine line = lines.get(position);
        holder.name.setText(line.getProduct().getNameTemplate());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.onLineSelected(line);
            }
        });
        holder.code.setText(line.getProduct().getCode());
//        if (!ImageCache.getInstance().exists(
//                line.getProduct().getClass().getName()
//                        + line.getProduct().getId()))
//            ImageCache.getInstance().putInCache(
//                    line.getProduct().getClass().getName()
//                            + line.getProduct().getId(),
//                    ImageUtil.byteArrayToBitmap(line.getProduct()
//                            .getImageMedium()));
//        holder.image.setImageBitmap(ImageCache.getInstance().getFromCache(
//                line.getProduct().getClass().getName()
//                        + line.getProduct().getId()));
        holder.image.setVisibility(View.GONE);
        try {
            if (line.getProductUosQuantity() != null) {
                String quantityUos = line.getProductUosQuantity().toString();
                if (quantityUos != null && quantityUos.endsWith(".0"))
                    quantityUos = quantityUos.replace(".0", "");
                holder.quantityUos.setText(quantityUos);
            }
            if (line.getProductUomQuantity() != null) {
                String quantityUom = line.getProductUomQuantity().toString();
                if (quantityUom != null && quantityUom.endsWith(".0"))
                    quantityUom = quantityUom.replace(".0", "");
                holder.quantityUom.setText(quantityUom);
            }
            if (line.getProductUos() != null)
                holder.unitUos.setText(ProductUomRepository.getInstance().getById(line.getProductUos().longValue()).getName());
            if (line.getProductUom() != null)
                holder.unitUom.setText(ProductUomRepository.getInstance().getById(line.getProductUom().longValue()).getName());
            /*  DAVID - NADA DE CAMBIAR DE UNIDAD DE MEDIDA
            try {
                holder.unit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragment.onUnitChanged(line);
                        notifyDataSetChanged();
                        fragment.loadOnResume();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
        } catch (Exception e) {
            //do nothing
        }
        if (line.getDiscount() != null) {
            holder.discount.setText("" + line.getDiscount());
        }
        holder.discount.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        float descuento = Float.parseFloat(v.getText().toString());
                        if (descuento < 0 || descuento > line.getProduct().getMaxDiscount())
                            throw new Exception("Descuento incorrecto");
                        line.setDiscount(descuento);
                        OrderLinesNewDispositionAdapter.this.fragment.loadOnResume();
                    } catch (Exception e) {
                        MessagesForUser.showMessage(OrderLinesNewDispositionAdapter.this.fragment.getView(), "Descuento ha de ser un valor entre 0.0 y " + line.getProduct().getMaxDiscount().toString(), 3000, Level.SEVERE);
                    }
                    return true;
                }
                return false;
            }
        });
        if (line.getPriceUnit() != null && line.getPriceUnit().floatValue() != 0.0000000001)
            // DAVID - CAMBIÉ getPriceUdv por getPriceUnit
            // cambio a 3 decimales
            holder.price.setText(new BigDecimal(line.getPriceUnit()
                    .doubleValue()).setScale(3, RoundingMode.HALF_UP) + "");
        else
            holder.price.setText("Cargando...");
        if (line.getPriceSubtotal() != null)
            holder.total.setText(new BigDecimal(line.getPriceSubtotal()
                    .doubleValue()).setScale(2, RoundingMode.HALF_UP) + "");
        boolean showDeleteIcon = fragment.isEditable();
        if (showDeleteIcon) {
            holder.deleteIcon.setVisibility(View.VISIBLE);
            holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderRepository.getCurrentOrder().getLines().remove(line);
                    notifyDataSetChanged();
                    fragment.loadOnResume();
                }
            });
        }
        else
            holder.deleteIcon.setVisibility(View.GONE);
        if (fragment == null || !showDeleteIcon) {
            holder.lessIconUos.setVisibility(View.GONE);
            holder.plusIconUos.setVisibility(View.GONE);
            holder.lessIconUom.setVisibility(View.GONE);
            holder.plusIconUom.setVisibility(View.GONE);
        } else {
            holder.lessIconUos.setVisibility(View.VISIBLE);
            holder.plusIconUos.setVisibility(View.VISIBLE);
            holder.lessIconUom.setVisibility(View.VISIBLE);
            holder.plusIconUom.setVisibility(View.VISIBLE);
            holder.lessIconUos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (line.getProductUosQuantity().floatValue() >= 1.0){
                    // de esta forma redondeo
                    if (line.getProductUosQuantity().floatValue() == line.getProductUosQuantity().longValue()) {
                        line.setProductUosQuantity(line.getProductUosQuantity().floatValue() - 1.0);
                    } else {
                        line.setProductUosQuantity(line.getProductUosQuantity().longValue());
                    }
                    if (line.getProductUomQuantity() != null) {
                        String quantityUom = line.getProductUomQuantity().toString();
                        if (quantityUom != null && quantityUom.endsWith(".0"))
                            quantityUom = quantityUom.replace(".0", "");
                        holder.quantityUom.setText(quantityUom);
                    }
                    line.setPriceSubtotal(line.getProductUosQuantity().floatValue()
                            * line.getPriceUdv().floatValue()
                            - (line.getProductUosQuantity().floatValue()
                            * line.getPriceUdv().floatValue()
                            * line.getDiscount().floatValue() / 100F));
                    if (line.getPriceSubtotal() != null)
                        holder.total.setText(new BigDecimal(line.getPriceSubtotal()
                                .doubleValue()).setScale(2, RoundingMode.HALF_UP) + "");
                    fragment.loadOnResume();
                }
                }
            });
            holder.plusIconUos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // de esta forma redondeo
                    if (line.getProductUosQuantity().floatValue() == line.getProductUosQuantity().longValue()){
                        line.setProductUosQuantity(line.getProductUosQuantity().floatValue() + 1.0);
                    } else {
                        line.setProductUosQuantity(line.getProductUosQuantity().longValue() + 1);
                    }
                    if (line.getProductUomQuantity() != null) {
                        String quantityUom = line.getProductUomQuantity().toString();
                        if (quantityUom != null && quantityUom.endsWith(".0"))
                            quantityUom = quantityUom.replace(".0", "");
                        holder.quantityUom.setText(quantityUom);
                    }
                    line.setPriceSubtotal(line.getProductUosQuantity().floatValue()
                            * line.getPriceUdv().floatValue()
                            - (line.getProductUosQuantity().floatValue()
                            * line.getPriceUdv().floatValue()
                            * line.getDiscount().floatValue() / 100F));
                    if (line.getPriceSubtotal() != null)
                        holder.total.setText(new BigDecimal(line.getPriceSubtotal()
                                .doubleValue()).setScale(2, RoundingMode.HALF_UP) + "");
                    fragment.loadOnResume();
                }
            });
            holder.lessIconUom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (line.getProductUomQuantity().floatValue() >= 1.0) {
                        line.setProductUomQuantity(line.getProductUomQuantity().floatValue() - 1.0);
                        if (line.getProductUosQuantity() != null) {
                            String quantityUos = line.getProductUosQuantity().toString();
                            if (quantityUos != null && quantityUos.endsWith(".0"))
                                quantityUos = quantityUos.replace(".0", "");
                            holder.quantityUos.setText(quantityUos);
                        }
                        line.setPriceSubtotal(line.getProductUomQuantity().floatValue()
                                * line.getPriceUnit().floatValue()
                                - (line.getProductUomQuantity().floatValue()
                                * line.getPriceUnit().floatValue()
                                * line.getDiscount().floatValue() / 100F));
                        if (line.getPriceSubtotal() != null)
                            holder.total.setText(new BigDecimal(line.getPriceSubtotal()
                                    .doubleValue()).setScale(2, RoundingMode.HALF_UP) + "");
                        fragment.loadOnResume();
                    }
                }
            });
            holder.plusIconUom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    line.setProductUomQuantity(line.getProductUomQuantity().floatValue() + 1.0);
                    if (line.getProductUosQuantity() != null) {
                        String quantityUos = line.getProductUosQuantity().toString();
                        if (quantityUos != null && quantityUos.endsWith(".0"))
                            quantityUos = quantityUos.replace(".0", "");
                        holder.quantityUos.setText(quantityUos);
                    }
                    line.setPriceSubtotal(line.getProductUomQuantity().floatValue()
                            * line.getPriceUnit().floatValue()
                            - (line.getProductUomQuantity().floatValue()
                            * line.getPriceUnit().floatValue()
                            * line.getDiscount().floatValue() / 100F));

                    if (line.getPriceSubtotal() != null)
                        holder.total.setText(new BigDecimal(line.getPriceSubtotal()
                                .doubleValue()).setScale(2, RoundingMode.HALF_UP) + "");
                    fragment.loadOnResume();
                }
            });
        }
        return vi;
    }

    public interface OrderLineUnitChangedListener {
        public void onUnitChanged(OrderLine line);
    }

    public interface OrderLineSelected {
        public void onLineSelected(OrderLine line);
    }

}