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
package com.cafedered.midban.view.activities;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import android.app.ActionBar;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback{

    // This is just to set the colour to default on unselect
    private Marker previousMarker;
    double maxLatitude = 360d;
    double maxLongitude = 360d;
    double minLatitude = 0d;
    double minLongitude = 0d;
    private GoogleMap map = null;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_maps_layout);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setLogo(R.drawable.transparent);
            actionBar.setTitle(R.string.activity_google_maps_title);
        }
        ImageView view = (ImageView) findViewById(android.R.id.home);
        if (view != null) {
            view.setPadding(0, 0, 30, 0);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMapAsync(this);
        if (MidbanApplication.instance.getValues().containsKey(
                MidbanApplication.PREFIX + "partners")) {
            List<Partner> partners = (List<Partner>) MidbanApplication.instance
                    .getValues().get(MidbanApplication.PREFIX + "partners");
            for (Partner partner : partners) {
                final Geocoder geocoder = new Geocoder(this);
                String address = partner.getStreet() + ", "
                        + partner.getCity();
                final String name = partner.getName();
                final String code = "" + partner.getId();

                new AsyncTask<String, Void, List<Address>>() {
                    @Override
                    protected List<Address> doInBackground(String... params) {
                        try {
                            return geocoder.getFromLocationName(params[0], 1);
                        } catch (IOException e) {
                            if (LoggerUtil.isDebugEnabled())
                                e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<Address> addresses) {
                        super.onPostExecute(addresses);
                        LatLng location = new LatLng(0, 0);
                        if (addresses.size() > 0) {
                            double latitude = addresses.get(0).getLatitude();
                            if (latitude > maxLatitude)
                                maxLatitude = latitude;
                            if (latitude < minLatitude && latitude > 0)
                                minLatitude = latitude;
                            double longitude = addresses.get(0).getLongitude();
                            if (longitude > maxLongitude)
                                maxLongitude = longitude;
                            if (longitude < minLongitude && longitude > 0)
                                minLongitude = longitude;
                            location = new LatLng(latitude, longitude);
                        }
                        if (location.latitude != 0 && location.longitude != 0) {
                            map.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(name)
                                    .snippet((code != null ? code : ""))
                                    .icon(BitmapDescriptorFactory
                                            .fromResource(R.drawable.mapas_localizacion1)));
                        }
                        LatLngBounds partnersBounds = new LatLngBounds(
                                new LatLng(minLatitude, minLongitude),
                                new LatLng(maxLatitude, maxLongitude));
                        map.setMyLocationEnabled(true);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                partnersBounds.getCenter(), 1));
                        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                if (previousMarker != null)
                                    previousMarker.setIcon(BitmapDescriptorFactory
                                            .fromResource(R.drawable.mapas_localizacion1));
                                marker.setIcon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.mapas_localizacion2));
                                previousMarker = marker;
                                return false;
                            }
                        });
                        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                            @Override
                            public View getInfoWindow(Marker arg0) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {
                                View v = getLayoutInflater()
                                        .inflate(
                                                R.layout.google_maps_activity_custom_infowindow,
                                                null);
                                ((TextView) v
                                        .findViewById(R.id.google_maps_custom_infowindow_name))
                                        .setText(marker.getTitle());
                                ((TextView) v
                                        .findViewById(R.id.google_maps_custom_infowindow_code))
                                        .setText(marker.getSnippet());
                                return v;

                            }
                        });
                        MidbanApplication.instance.getValues().remove(
                                MidbanApplication.PREFIX + "partners");

                    }
                }.execute(address);
            }
        } else {
            final Geocoder geocoder = new Geocoder(this);
            String address = (String) this.getIntent().getExtras()
                    .getString("partner_address");
            final String name = (String) this.getIntent().getExtras()
                    .getString("partner_name");
            final String code = (String) this.getIntent().getExtras()
                    .getString("partner_code");
            final String lastSellAmount = (String) this.getIntent().getExtras()
                    .getString("partner_last_sell_amount");

            new AsyncTask<String, Void, List<Address>>() {
                @Override
                protected List<Address> doInBackground(String... params) {
                    try {
                        return geocoder.getFromLocationName(params[0], 1);
                    } catch (IOException e) {
                        if (LoggerUtil.isDebugEnabled())
                            e.printStackTrace();
                        return null;
                    }
                }

                protected void onPostExecute(java.util.List<Address> addresses) {
                    LatLng location = new LatLng(0, 0);
                    if (addresses.size() > 0) {
                        double latitude = addresses.get(0).getLatitude();
                        double longitude = addresses.get(0).getLongitude();
                        location = new LatLng(latitude, longitude);
                    }
                    if (location.latitude == 0 && location.longitude == 0) {
                        MessagesForUser
                                .showMessage(
                                        GoogleMapsActivity.this,
                                        R.string.maps_partner_location_cannot_be_retrieved,
                                        Toast.LENGTH_LONG, Level.WARNING);
                    } else {
                        map.addMarker(new MarkerOptions()
                                .position(location)
                                .title(name)
                                .snippet(
                                        code != null ? code
                                                : "" + lastSellAmount != null ? ", Importe último pedido: "
                                                + lastSellAmount
                                                : "")
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.mapas_localizacion2)));
                        map.setMyLocationEnabled(true);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(location.latitude,
                                        location.longitude), 16.0f));
                    }
                }

            }.execute(address);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }
}