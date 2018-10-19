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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.R;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Order;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.PartnerCategory;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.service.repositories.PartnerCategoryRepository;
import com.cafedered.midban.service.repositories.StateRepository;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.GoogleMapsActivity;
import com.cafedered.midban.view.activities.PartnerDetailActivity;
import com.cafedered.midban.view.fragments.PartnerListFragment;

public class PartnerListItemAdapter extends BaseAdapter {

    private List<Partner> partners;
    private static LayoutInflater inflater = null;

    public PartnerListItemAdapter(PartnerListFragment fragment,
            List<Partner> partners) {
        this.partners = partners;
        inflater = (LayoutInflater) fragment.getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return partners.size();
    }

    public Object getItem(int position) {
        return partners.get(position);
    }

    public long getItemId(int position) {
        return partners.get(position).getId();
    }

    public static class ViewHolder {
        public TextView name;
        public TextView code;
        public TextView type;
        public TextView city;
        public TextView province;
        public ViewGroup icons;
        public ImageView geolocalizationIcon;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        final Partner partner = partners.get(position);
        if (convertView == null) {
            vi = inflater.inflate(R.layout.partner_list_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) vi
                    .findViewById(R.id.partner_list_item_name);
            holder.code = (TextView) vi
                    .findViewById(R.id.partner_list_item_code);
            holder.type = (TextView) vi
                    .findViewById(R.id.partner_list_item_type);
            holder.province = (TextView) vi
                    .findViewById(R.id.partner_list_item_city_province);
            holder.icons = (ViewGroup) vi
                    .findViewById(R.id.partner_list_item_icons);
            holder.geolocalizationIcon = (ImageView) vi
                    .findViewById(R.id.partner_list_item_geo);
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();
        vi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MidbanApplication.putValueInContext(
                        ContextAttributes.PARTNER_TO_DETAIL, partner);
                Intent intent = new Intent(view.getContext(),
                        PartnerDetailActivity.class);
                intent.putExtras(new Bundle());
                view.getContext().startActivity(intent);
            }
        });
        holder.name.setText(partner.getName());
        holder.name.setTag(partner.getId());
        holder.code.setText(partner.getRef());
        holder.type.setText(partner.getPhone() != null ? partner.getPhone() : "");
        holder.province.setText(partner.getCity());


//        for (String aType : partner.getType().split(",")) {
        try {
            PartnerCategory category = PartnerCategoryRepository.getInstance().getById(partner.getCategoryId().longValue());
            String aType = "";
            if (category != null)
                aType = category.getName();
            if (aType.toLowerCase().equals("Restaurante".toLowerCase()) || aType.toLowerCase().equals("Restaurantes".toLowerCase())) {
                if (holder.icons.getChildCount() == 0) {
                    ImageView anIcon = new ImageView(vi.getContext());
                    anIcon.setImageDrawable(vi.getResources().getDrawable(
                            R.drawable.tipo_cliente_restaurante));

                    holder.icons.addView(anIcon);
                }
            }
            if (aType.toLowerCase().equals("Comercios".toLowerCase())) {
                if (holder.icons.getChildCount() == 0) {
                    ImageView anIcon = new ImageView(vi.getContext());
                    anIcon.setImageDrawable(vi.getResources().getDrawable(
                            R.drawable.header_cliente_cliente_header_cesta));

                    holder.icons.addView(anIcon);
                }
            }
            if (aType.toLowerCase().equals("Ocio".toLowerCase())) {
                if (holder.icons.getChildCount() == 0) {
                    ImageView anIcon = new ImageView(vi.getContext());
                    anIcon.setImageDrawable(vi.getResources().getDrawable(
                            R.drawable.tipo_cliente_bar));
                    holder.icons.addView(anIcon);
                }
            }
            if (aType.toLowerCase().equals("Bares y cafeterias".toLowerCase())) {
                if (holder.icons.getChildCount() == 0) {
                    ImageView anIcon = new ImageView(vi.getContext());
                    anIcon.setImageDrawable(vi.getResources().getDrawable(
                            R.drawable.tipo_cliente_cafe));
                    holder.icons.addView(anIcon);
                }
            }
            if (aType.toLowerCase().equals("Otros sectores".toLowerCase())) {
                if (holder.icons.getChildCount() == 0) {
                    ImageView anIcon = new ImageView(vi.getContext());
                    anIcon.setImageDrawable(vi.getResources().getDrawable(
                            R.drawable.transparent));
                    holder.icons.addView(anIcon);
                }
            }
            if (aType.toLowerCase().equals("Restauración social".toLowerCase())) {
                if (holder.icons.getChildCount() == 0) {
                    ImageView anIcon = new ImageView(vi.getContext());
                    anIcon.setImageDrawable(vi.getResources().getDrawable(
                            R.drawable.restauracion_social));
                    holder.icons.addView(anIcon);
                }
            }
            if (aType == null || aType.toLowerCase().equals("Sin categorizar".toLowerCase())) {
                if (holder.icons.getChildCount() == 0) {
                    ImageView anIcon = new ImageView(vi.getContext());
                    anIcon.setImageDrawable(vi.getResources().getDrawable(
                            R.drawable.no_categoria));
                    holder.icons.addView(anIcon);
                }
            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
            if (holder.icons.getChildCount() == 0) {
                ImageView anIcon = new ImageView(vi.getContext());
                anIcon.setImageDrawable(vi.getResources().getDrawable(
                        R.drawable.no_categoria));
                holder.icons.addView(anIcon);
            }
        } catch (ServiceException e) {
            e.printStackTrace();
            if (holder.icons.getChildCount() == 0) {
                ImageView anIcon = new ImageView(vi.getContext());
                anIcon.setImageDrawable(vi.getResources().getDrawable(
                        R.drawable.no_categoria));
                holder.icons.addView(anIcon);
            }
        }
        // FIXME fix the hardcoded client types and mock data
            holder.geolocalizationIcon.setTag(partner.getId());
//        }
        holder.geolocalizationIcon
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("partner_address", partner.getStreet()
                                + ", " + partner.getCity());
                        bundle.putString("partner_name", partner.getName());
                        bundle.putString("partner_code", "" + partner.getId());
                        Order order = new Order();
                        order.setPartnerId(partner.getId());
                        List<Order> orders;
                        try {
                            orders = OrderRepository.getInstance()
                                    .getByExample(order, Restriction.AND,
                                            false, 0, 100);

                            Collections.sort(orders);
                            if (orders != null && orders.size() > 0)
                                bundle.putString("partner_last_sell_amount",
                                    new BigDecimal(orders.get(0)
                                            .getAmountTotal().doubleValue())
                                            .setScale(2, RoundingMode.HALF_UP)
                                            .toString());
                            else
                                new BigDecimal("0.0");
                        } catch (ServiceException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(v.getContext(),
                                GoogleMapsActivity.class);
                        intent.putExtras(bundle);
                        v.getContext().startActivity(intent, bundle);
                    }
                });
        return vi;
    }
}