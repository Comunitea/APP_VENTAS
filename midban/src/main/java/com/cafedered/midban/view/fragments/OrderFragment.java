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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.ItemClicked;
import com.cafedered.midban.annotations.ItemSelected;
import com.cafedered.midban.annotations.Remote;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.OrderLine;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.Tax;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.OrderLineRepository;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.service.repositories.ProductRepository;
import com.cafedered.midban.service.repositories.TaxRepository;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.SessionFactory;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.LastSalesActivity;
import com.cafedered.midban.view.activities.PortadaActivity;
import com.cafedered.midban.view.activities.ProductCardActivity;
import com.cafedered.midban.view.activities.ProductCatalogActivity;
import com.cafedered.midban.view.adapter.IProductSelection;
import com.cafedered.midban.view.adapter.OrderLinesAdapter;
import com.cafedered.midban.view.adapter.OrderListItemAdapter;
import com.cafedered.midban.view.adapter.ProductAutocompleteAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.dialogs.OneFieldEditionDialog;
import com.cafedered.midban.view.dialogs.ProductToCartDialog;
import com.debortoliwines.openerp.api.FilterCollection;
import com.debortoliwines.openerp.api.ObjectAdapter;
import com.debortoliwines.openerp.api.OpenERPCommand;
import com.debortoliwines.openerp.api.Row;
import com.debortoliwines.openerp.api.RowCollection;
import com.debortoliwines.openerp.api.Session;

@Fragment(R.layout.fragment_order)
public class OrderFragment extends BaseSupportFragment implements IProductSelection {

    @Wire(view = R.id.fragment_order_partner_name_tv)
    private TextView partnerName;
    @Wire(view = R.id.fragment_order_partner_code_tv)
    private TextView partnerCode;
    @Wire(view = R.id.fragment_order_order_number_value_tv)
    private TextView orderNumber;
    @Wire(view = R.id.fragment_order_delivery_date_et)
    private EditText orderDeliveryDate;
    @Wire(view = R.id.fragment_order_address_sp)
    private Spinner orderAddresses;
    @Wire(view = R.id.fragment_order_address_result_tv)
    private TextView orderAddressSelected;
    @Wire(view = R.id.fragment_order_amount_data_tv)
    private TextView orderTotalAmount;
    @Wire(view = R.id.fragment_order_margin_data_tv)
    private TextView orderMarginAmount;
    @Wire(view = R.id.fragment_order_total_lines_data_tv)
    private TextView orderlinesCount;
    @Wire(view = R.id.fragment_order_notes)
    private TextView orderNotes;
    @Wire(view = R.id.fragment_order_false_drawer_notes_bt)
    private ImageView falseDrawerNotes;
    @Wire(view = R.id.fragment_order_lines)
    private ListView orderLines;
    @Wire(view = R.id.fragment_order_confirm_btn)
    private Button confirmButton;
    @Wire(view = R.id.fragment_order_cancel_btn)
    private Button cancelButton;
    @Wire(view = R.id.fragment_order_buttons_read_only_mode)
    LinearLayout readOnlyButtons;
    @Wire(view = R.id.fragment_order_buttons_edit_mode)
    LinearLayout editButtons;
    @Wire(view = R.id.fragment_order_risk_limit_tv)
    private TextView riskLimit;
    @Wire(view = R.id.fragment_order_add_quick_product)
    private TextView cartAddButton;
    private Float availableDebitOnline;

    private boolean readOnlyMode = false;

    private Partner partner;
    Product product;
    List<Product> products;
    ProductAutocompleteAdapter adapter;
    AutoCompleteTextView searchText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        setHasOptionsMenu(true);
        if (MidbanApplication
                .getValueFromContext(ContextAttributes.READ_ONLY_ORDER_MODE) != null
                && (Boolean)MidbanApplication.getValueFromContext(
                    ContextAttributes.READ_ONLY_ORDER_MODE)) {
            partner = OrderRepository.getCurrentOrder().getPartner();
            readOnlyMode = true;
        }
        else if (OrderRepository.getInstance().isOrderInitialized()
                && OrderRepository.getCurrentOrder().getPartnerId() != null)
            partner = OrderRepository.getCurrentOrder().getPartner();
        if (partner == null || partner.getName() == null)
            partner = (Partner) MidbanApplication
                .getValueFromContext(ContextAttributes.PARTNER_TO_ORDER);
        if (partner == null || partner.getName() == null) // it came from partner detail
            partner = (Partner) MidbanApplication
                    .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL);
        partnerName.setText(partner.getName());
        partnerCode.setText("" + partner.getRef());
        calculateRiskLimit(partner);
        OrderRepository.getCurrentOrder().setPartnerId(partner.getId());
        List<String> addressesList = new ArrayList<String>();
        addressesList.add("");
        addressesList.add(partner.getCompleteAddress());
        addressesList.add(partner.getContactAddress());
        ArrayAdapter<String> addressesAdapter = new ArrayAdapter<String>(
                getActivity(), R.layout.spinner_row, addressesList);
        addressesAdapter.setDropDownViewResource(R.layout.spinner_item);
        if (readOnlyMode) {
            orderAddresses.setEnabled(false);
            orderDeliveryDate.setEnabled(false);
            editButtons.setVisibility(View.GONE);
            readOnlyButtons.setVisibility(View.VISIBLE);
        } else {
            editButtons.setVisibility(View.VISIBLE);
            readOnlyButtons.setVisibility(View.GONE);
        }
        orderAddresses.setAdapter(addressesAdapter);
        setOrderNumber();
        setNotesVisibility();
        orderLines.setAdapter(new OrderLinesAdapter(getActivity(),
                OrderRepository.getCurrentOrder().getLines(), !readOnlyMode, this));
        orderlinesCount.setText(""
                + OrderRepository.getCurrentOrder().getLines().size());
        if (OrderRepository.getCurrentOrder().getAmountTotal() != null)
            orderTotalAmount.setText(OrderRepository.getCurrentOrder()
                    .getAmountTotal().toString()
                    + getResources().getString(R.string.currency_symbol));
        if (OrderRepository.getCurrentOrder().getDateOrder() != null) {
            try {
                orderDeliveryDate.setText(DateUtil.toFormattedString(DateUtil.parseDate(OrderRepository.getCurrentOrder().getDateOrder()), "dd.MM.yyyy"));
            } catch (ParseException e) {
                //do nothing
            }
        } else {
            OrderRepository.getCurrentOrder().setDateOrder(getDeliveryDate());
            if (OrderRepository.getCurrentOrder().getDateOrder() != null) {
                try {
                    orderDeliveryDate.setText(DateUtil.toFormattedString(DateUtil.parseDate(OrderRepository.getCurrentOrder().getDateOrder()), "dd.MM.yyyy"));
                } catch (ParseException e) {
                    //do nothing
                }
            }
        }
        if (OrderRepository.getCurrentOrder().getAddressSelected() != null) {
            orderAddressSelected.setText(OrderRepository.getCurrentOrder().getAddressSelected());
        } else {
            orderAddressSelected.setText(partner.getCompleteAddress());
            orderAddresses.setSelection(1);
        }
        cartAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product != null) {
                    searchText.setText("");
                    OrderLine lineToEdit = null;
                    for (OrderLine line : OrderRepository.getCurrentOrder().getLines()) {
                        if (line.getProduct().getId().equals(product.getId()))
                            lineToEdit = line;
                    }
                    ProductToCartDialog dialog = new ProductToCartDialog(getActivity(),
                            product, lineToEdit);
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            onResume();
                        }
                    });
                    // dialog.showData(R.layout.dialog_product_to_cart);
                    dialog.show();
                } else {
                    MessagesForUser.showMessage(rootView, getResources().getString(R.string.product_not_selected), 4000, Level.WARNING);
                }
            }
        });
        manageProductSearchViews(rootView);
        return rootView;
    }

    private void manageProductSearchViews(View rootView) {
        searchText = (AutoCompleteTextView) rootView
                .findViewById(R.id.single_field_edition_popup_textfield);
        // add the listener so it will tries to suggest while the user types
        products = new ArrayList<Product>();
        adapter = new ProductAutocompleteAdapter(
                rootView.getContext(), products, this);
        searchText.setThreshold(2);
        searchText.setAdapter(adapter);
    }

    @Override
    public void onSelectedProduct(Product product) {
        this.product = product;
        searchText.setText(product.getCode() + " - " + product.getNameTemplate());
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ProductRepository.getInstance().getCalculatedPrice(OrderFragment.this.product,
                        partner,
                        (String) MidbanApplication.getValueFromContext(ContextAttributes.ACTUAL_TARIFF),
                        MidbanApplication.getLoggedUser().getLogin(),
                        MidbanApplication.getLoggedUser().getPasswd());
                return null;
            }
        }.execute();
    }

    private void calculateRiskLimit(final Partner partner) {
        try {
            Float totalDebt = partner.getDebit().floatValue();
            final Number creditLimit = partner.getCreditLimit();
            if (availableDebitOnline != null)
                totalDebt = availableDebitOnline;
            final Float availableCredit = creditLimit.floatValue() - totalDebt - OrderRepository.getCurrentOrder()
                    .getAmountTotal().floatValue();
            if (creditLimit != null && totalDebt != null) {
                if (availableCredit < 0) {
                    riskLimit.setTextColor(MidbanApplication.getContext().getResources().getColor(R.color.red));
                } else {
                    riskLimit.setTextColor(MidbanApplication.getContext().getResources().getColor(R.color.midban_text_color));
                }
                riskLimit.setText("Crédito disponible: " + new BigDecimal(availableCredit.floatValue()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() + " €");
            }
            if (availableDebitOnline == null) {
                new AsyncTask<Void, Void, Float>() {
                    @Override
                    protected Float doInBackground(Void... params) {
                        Long initDate = new Date().getTime();
                        Session openERPSession = null;
                        try {
                            User user = (User) MidbanApplication.getValueFromContext(ContextAttributes.LOGGED_USER);
                            openERPSession = SessionFactory.getInstance(user.getLogin(), user.getPasswd()).getSession();
                        } catch (Exception e) {
                            // do nothing, openERPSession will be null
                        }
                        if (openERPSession != null) {
                            ObjectAdapter adapter;
                            try {
                                adapter = openERPSession.getObjectAdapter("res.partner");
                                FilterCollection filters = new FilterCollection();
                                filters.add("id", "=", partner.getId());
                                RowCollection entities;
                                String[] fieldsRemote = {"id", "total_debt"};
                                entities = adapter.searchAndReadObject(filters, fieldsRemote, null);
                                for (Row row : entities) {
                                    return ((Double) row.get("total_debt")).floatValue();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Float aFloat) {
                        super.onPostExecute(aFloat);
                        try {
                            if (aFloat != null) {
                                availableDebitOnline = aFloat;
                                Float disponible = creditLimit.floatValue() - aFloat - OrderRepository.getCurrentOrder()
                                        .getAmountTotal().floatValue();
                                if (disponible < 0) {
                                    riskLimit.setTextColor(MidbanApplication.getContext().getResources().getColor(R.color.red));
                                } else {
                                    riskLimit.setTextColor(MidbanApplication.getContext().getResources().getColor(R.color.midban_text_color));
                                }
                                riskLimit.setText("Crédito disponible: " + new BigDecimal(disponible.floatValue()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() + " €");
                                AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                                riskLimit.startAnimation(fadeIn);
                                fadeIn.setDuration(1500);
                                fadeIn.setFillAfter(true);

                            }
                        } catch (Exception e) {
                            //do nothing
                        }
                    }
                }.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //do nothing
        }
    }

    private String getDeliveryDate() {
        return DateUtil.toFormattedString(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm:ss");

        /* DAVID - LA PROPIEDAD delivery_days HA DESAPARECIDO.
        *
        *
        *
        *   https://bitbucket.org/noroestesoluciones/odoo-app/issues/4/modificar-la-sincronizaci-n
        *   Sobre el error sincronizando clientes:
        *   "Por lo que he podido revisar solo deben fallaros esos dos campos: (5 = "delivery_days_ids" -> "deliveryDays" y 6 = "state_id" -> "stateId")
        *   Delivery_days_id Puede daros algún problema más adelante ya que si no recuerdo mal lo usaba para calcular una fecha de entrega en un pedido. Si fuese el caso, lo ignoráis en el pedido y ponéis la fecha del día en la que se hace el pedido de momento "
        *
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        String days = partner.getDeliveryDays();
        if (days == null || days.length() == 0)
            return null;
        do {
            List<Integer> daysI = new ArrayList<Integer>();
            for (String day : days.split(";")) {
                if (day != null) {
                    int aDay = Integer.valueOf(day);
                    if (aDay == 7)
                        aDay = 1;
                    else aDay = aDay + 1;
                    daysI.add(aDay);
                }
            }
            do {
                if (daysI.contains(tomorrow.get(Calendar.DAY_OF_WEEK))) {
                    return DateUtil.toFormattedString(tomorrow.getTime(), "yyyy-MM-dd HH:mm:ss");
                } else
                    tomorrow.add(Calendar.DATE, 1);
            } while(true);

        } while (true);
        */
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if(getActivity().getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
        if (OrderRepository.getCurrentOrder().getDateOrder() != null) {
            try {
                orderDeliveryDate.setText(DateUtil.toFormattedString(DateUtil.parseDate(OrderRepository.getCurrentOrder().getDateOrder()), "dd.MM.yyyy"));
            } catch (ParseException e) {
                //do nothing
            }
        }
        if (OrderRepository.getCurrentOrder().getAddressSelected() != null) {
            orderAddressSelected.setText(OrderRepository.getCurrentOrder().getAddressSelected());
            orderAddresses.setSelection(1);
        }
        orderlinesCount.setText(""
                + OrderRepository.getCurrentOrder().getLines().size());
        orderLines.setAdapter(new OrderLinesAdapter(getActivity(),
                OrderRepository.getCurrentOrder().getLines(), !readOnlyMode, this));
        calculateAmounts();
        orderTotalAmount.setText(OrderRepository.getCurrentOrder()
                .getAmountTotal().toString()
                + getResources().getString(R.string.currency_symbol));
        if (OrderRepository.getCurrentOrder().getAmountUntaxed().floatValue() != 0)
            orderMarginAmount.setText(new BigDecimal((OrderRepository
                    .getCurrentOrder().getAmountUntaxed().floatValue()
                    * OrderRepository.getCurrentOrder().getMargin()
                            .floatValue() / 100F)).setScale(2,
                    RoundingMode.HALF_UP).toString()
                    + getResources().getString(R.string.currency_symbol)
                    + " ("
                    + new BigDecimal(OrderRepository.getCurrentOrder()
                            .getMargin().floatValue()
                            * 100F
                            / OrderRepository.getCurrentOrder()
                                    .getAmountUntaxed().floatValue()).setScale(
                            2, RoundingMode.HALF_UP).toString() + "%)");
        if (readOnlyMode) {
            orderAddresses.setEnabled(false);
            orderDeliveryDate.setEnabled(false);
            editButtons.setVisibility(View.GONE);
            readOnlyButtons.setVisibility(View.VISIBLE);
        } else {
            orderAddresses.setEnabled(true);
            orderDeliveryDate.setEnabled(true);
            editButtons.setVisibility(View.VISIBLE);
            readOnlyButtons.setVisibility(View.GONE);
        }
        calculateRiskLimit(partner);
    }

    @Click(view = R.id.fragment_order_header_favourites)
    public void clickFavourites(View view) {
        if (!readOnlyMode) {
            try {
                getFragmentManager().executePendingTransactions();
                getFragmentManager().beginTransaction()
                        .replace(this.getId(), new FavouritesPartnerFragment())
                        .commit();
            } catch (IllegalStateException e) {
                if (LoggerUtil.isDebugEnabled())
                    e.printStackTrace();
            }
        }
    }

    private void calculateAmounts() {
        double amountUntaxed = 0.0d;
        double amountTax = 0.0d;
        double margin = 0.0d;
        searchText.setText("");
        for (OrderLine line : OrderRepository.getCurrentOrder().getLines()) {
            amountUntaxed += line.getPriceSubtotal().doubleValue();
            Tax tax = null;
            try {
                if (line.getTaxesId() != null && line.getTaxesId().length > 0) {
                    for (Number taxId : line.getTaxesId()) {
                        if (taxId != null) {
                            tax = TaxRepository.getInstance().getById(taxId.longValue());
                            if (tax != null && tax.getAmount() != null)
                                amountTax += line.getPriceSubtotal().doubleValue()
                                        * tax.getAmount().doubleValue();

                        }                     }
                }
                if (tax == null) {
                    if (line != null && line.getProduct() != null &&
                            line.getProduct().getProductTemplate() != null &&
                            line.getProduct().getProductTemplate().getTaxesId() != null)
                        tax = TaxRepository.getInstance().getById(
                                line.getProduct().getProductTemplate().getTaxesId()
                                        .longValue());
                    if (tax != null) {
                        tax.getType().equals("percent");
                        amountTax += line.getPriceSubtotal().doubleValue()
                                * tax.getAmount().doubleValue();
                    }
                }
            } catch (ConfigurationException e) {
            } catch (ServiceException e) {
            }
            margin += line.getProductUomQuantity().doubleValue()
                    * (line.getPriceUnit().doubleValue() - line.getProduct()
                            .getStandardPrice().doubleValue());
        }
        OrderRepository.getCurrentOrder()
                .setAmountUntaxed(
                        new BigDecimal(amountUntaxed).setScale(2,
                                RoundingMode.HALF_UP));
        OrderRepository.getCurrentOrder().setAmountTax(
                new BigDecimal(amountTax).setScale(2, RoundingMode.HALF_UP));
        OrderRepository.getCurrentOrder().setAmountTotal(
                new BigDecimal(amountUntaxed + amountTax).setScale(2,
                        RoundingMode.HALF_UP));
        OrderRepository.getCurrentOrder().setMargin(
                new BigDecimal(margin).setScale(2, RoundingMode.HALF_UP));
    }

    private void setNotesVisibility() {
        if (orderNotes.getText().equals(
                getResources().getString(R.string.fragment_order_notes_text))) {
            orderNotes.setVisibility(View.GONE);
            falseDrawerNotes.setImageResource(R.drawable.general_flecha_abajo);
        } else {
            orderNotes.setVisibility(View.VISIBLE);
            falseDrawerNotes.setImageResource(R.drawable.general_flecha_arriba);
        }
    }

    @ItemSelected(view = R.id.fragment_order_address_sp)
    public void onAddressSelected(String addressSelected) {
        if (!readOnlyMode) {
            orderAddressSelected.setText(addressSelected);
            OrderRepository.getCurrentOrder().setAddressSelected(addressSelected);
        }
    }

    @ItemClicked(view = R.id.fragment_order_lines)
    public void onLineSelected(final OrderLine line) {
        if (!readOnlyMode) {
            final String items[] = {
                    getResources().getString(
                            R.string.fragment_order_modify_line_option),
                    getResources().getString(
                            R.string.fragment_order_last_sold_option),
                    getResources().getString(
                            R.string.fragment_order_view_product_card_option)};
            final AlertDialog.Builder dialog = new AlertDialog.Builder(
                    getActivity());
            dialog.setItems(items, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface d, int choice) {
                    if (choice == 0) {
                        Dialog dialog = new ProductToCartDialog(OrderFragment.this
                                .getView().getContext(), line.getProduct(), line);
                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface dialog) {
                                orderLines
                                        .setAdapter(new OrderLinesAdapter(
                                                getActivity(), OrderRepository
                                                .getCurrentOrder()
                                                .getLines(), !readOnlyMode, OrderFragment.this));
                                orderlinesCount.setText(""
                                        + OrderRepository.getCurrentOrder()
                                        .getLines().size());
                                calculateAmounts();
                                orderTotalAmount.setText(OrderRepository
                                        .getCurrentOrder().getAmountTotal()
                                        .toString()
                                        + getResources().getString(
                                        R.string.currency_symbol));
                            }
                        });
                        dialog.show();
                    } else if (choice == 1) {
                        MidbanApplication.putValueInContext(
                                ContextAttributes.PRODUCT_TO_LAST_SALES,
                                line.getProduct());
                        startActivityForResult(
                                getNextIntent(new Bundle(),
                                        OrderFragment.this.getView(),
                                        LastSalesActivity.class), 0);
                    } else if (choice == 2) {
                        MidbanApplication.putValueInContext(
                                ContextAttributes.PRODUCT_TO_DETAIL,
                                line.getProduct());

                        startActivityForResult(
                                getNextIntent(new Bundle(),
                                        OrderFragment.this.getView(),
                                        ProductCardActivity.class), 0);
                    }
                }
            });
            dialog.show();
        }
    }

    @Click(view = R.id.fragment_order_delivery_date_et)
    public void orderDeliveryDateClicked() {
        if (!readOnlyMode)
            DatePickerFragment.create(orderDeliveryDate).show(getFragmentManager(),
                    "datePicker");
    }

    @Click(view = R.id.fragment_order_notes)
    public void clickNotes() {
        if (!readOnlyMode)
            new OneFieldEditionDialog(orderNotes)
                .openDialogForUniqueTextField(orderNotes.getTag().toString());
    }

    @Click(view = R.id.fragment_order_repeat_btn)
    public void repeatOrder() {
        MidbanApplication.putValueInContext(ContextAttributes.READ_ONLY_ORDER_MODE, Boolean.FALSE);
        readOnlyMode = false;
        this.onResume();
    }

    @Click(view = R.id.fragment_order_false_drawer_notes_bt)
    public void clickFalseDrawerNotesButton() {
        if (orderNotes.getVisibility() == View.GONE) {
            falseDrawerNotes.setImageResource(R.drawable.general_flecha_arriba);
            orderNotes.setVisibility(View.VISIBLE);
        } else {
            falseDrawerNotes.setImageResource(R.drawable.general_flecha_abajo);
            orderNotes.setVisibility(View.GONE);
        }
    }

    @Click(view = R.id.fragment_order_cancel_btn)
    public void onCancelButtonPressed() {
        clearContext();
        getActivity().finish();
    }

    @Click(view = R.id.fragment_order_confirm_btn)
    public void onConfirmButtonPressed() {
        if (!readOnlyMode) {
            if (validateOrder()) {
                User user = (User) MidbanApplication
                        .getValueFromContext(ContextAttributes.LOGGED_USER);
                try {
                    OrderRepository.getCurrentOrder().setCreateDate(
                            DateUtil.toFormattedString(new Date()));
                    OrderRepository.getCurrentOrder().setDateOrder(
                            DateUtil.toFormattedString(DateUtil.parseDate(
                                    orderDeliveryDate.getText().toString(),
                                    "dd.MM.yyyy")));
                    OrderRepository.getCurrentOrder().setMargin(0.0);
                    OrderRepository
                            .getCurrentOrder()
                            .setNote(
                                    orderNotes
                                            .getText()
                                            .toString()
                                            .replace(
                                                    getResources()
                                                            .getString(
                                                                    R.string.fragment_order_notes_text),
                                                    ""));
                    OrderRepository.getCurrentOrder().setPartnerId(partner.getId());
                    OrderRepository.getCurrentOrder().setPartnerInvoiceId(
                            partner.getId());
                    OrderRepository.getCurrentOrder().setPartnerShippingId(
                            partner.getId());
                    OrderRepository.getCurrentOrder().setState("progress");
                    calculateAmounts();
                    OrderRepository.getCurrentOrder().setPricelistId(
                            partner.getPricelistId());
                    OrderRepository.getCurrentOrder().setName("/");
                    OrderRepository.getCurrentOrder().setChannel("tablet");
                    orderlinesCount.setText(""
                            + OrderRepository.getCurrentOrder().getLines().size());
                    orderTotalAmount.setText(OrderRepository.getCurrentOrder()
                            .getAmountTotal().toString());
                    orderLines.setAdapter(new OrderLinesAdapter(getActivity(),
                            OrderRepository.getCurrentOrder().getLines(), !readOnlyMode, this));
                } catch (java.text.ParseException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                }
                new AsyncTask<User, Void, Boolean>() {

                    ProgressDialog progress;

                    protected void onPreExecute() {
                        progress = ProgressDialog.show(OrderFragment.this.getActivity(), "Sincronizando...",
                                "Enviando pedido", true);
                    }

                    @Override
                    protected Boolean doInBackground(User... user) {
                        try {
                            if (OrderRepository.getCurrentOrder().getId() == null) {
                                Long orderId = OrderRepository.getInstance()
                                        .createRemoteObject(
                                                OrderRepository.getCurrentOrder(),
                                                user[0].getLogin(), user[0].getPasswd());
                                OrderRepository.getCurrentOrder().setId(orderId);
                            } else {
                                OrderRepository.getInstance().updateRemoteObject(OrderRepository.getCurrentOrder(), user[0].getLogin(), user[0].getPasswd());
                            }
                            OrderRepository.getCurrentOrder()
                                    .setPendingSynchronization(1);
                            int i = 0;
                            for (OrderLine line : OrderRepository.getCurrentOrder()
                                    .getLines()) {
                                line.setOrderId(OrderRepository.getCurrentOrder().getId());
                                line.setName(line.getOrderId() + "-" + i);
                                if (line.getId() == null) {
                                    Long idLine = OrderLineRepository.getInstance()
                                            .createRemoteObject(line, user[0].getLogin(),
                                                    user[0].getPasswd());
                                    line.setId(idLine);
                                } else {
                                    OrderLineRepository.getInstance().updateRemoteObject(line, user[0].getLogin(), user[0].getPasswd());
                                }
                                OrderLineRepository.getInstance().saveOrUpdate(line);
                                i++;
                            }
                            OpenERPCommand command = new OpenERPCommand(SessionFactory
                                    .getInstance(user[0].getLogin(),
                                            user[0].getPasswd()).getSession());
                            command.callObjectFunction("sale.order",
                                    "action_button_confirm", new Integer[] {OrderRepository
                                            .getCurrentOrder().getId().intValue()});
                            OrderRepository.getCurrentOrder()
                                    .setPendingSynchronization(0);
                            OrderRepository.getInstance().saveOrUpdate(
                                    OrderRepository.getCurrentOrder());
                        } catch (ServiceException e) {
                            if (LoggerUtil.isDebugEnabled())
                                e.printStackTrace();
                            try {
                                OrderRepository.getInstance().saveOrUpdate(
                                        OrderRepository.getCurrentOrder());
                            } catch (ServiceException e1) {
                                if (LoggerUtil.isDebugEnabled())
                                    e1.printStackTrace();
                                return false;
                            }
                            return true;
                        } catch (Exception e) {
                            if (LoggerUtil.isDebugEnabled())
                                e.printStackTrace();
                            try {
                                OrderRepository.getInstance().saveOrUpdate(
                                        OrderRepository.getCurrentOrder());
                            } catch (ServiceException e1) {
                                if (LoggerUtil.isDebugEnabled())
                                    e1.printStackTrace();
                                return false;
                            }
                            return true;
                        }
                        return true;
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        super.onPostExecute(result);
                        progress.dismiss();
                        if (!result) {
                            MessagesForUser.showMessage(getActivity(),
                                    R.string.cannot_save, Toast.LENGTH_LONG,
                                    Level.SEVERE);
                        } else {
                            clearContext();
                            MessagesForUser.showMessage(getActivity(), R.string.saved,
                                    Toast.LENGTH_LONG, Level.INFO);
                            getActivity().finish();
                        }
                    }
                }.execute(user);
            }
        } else {
            MessagesForUser.showMessage(getView(), "El pedido ya ha sido confirmado anteriormente", 4000, Level.SEVERE);
        }
    }

    private boolean validateOrder() {
        boolean validated = true;
        StringBuilder validationMessages = new StringBuilder("");
        if (OrderRepository.getCurrentOrder().getLines() == null || OrderRepository.getCurrentOrder().getLines().size() == 0) {
            validated = false;
            validationMessages.append(getResources().getString(R.string.order_must_have_items));
            validationMessages.append("\n");
        }
        // no se quiere que sea obligatoria
        // https://bitbucket.org/noroestesoluciones/odoo-app/issues/103/correcciones-en-la-operativa-de-la-fecha
        /*
        if (orderDeliveryDate.getText() == null || orderDeliveryDate.getText().toString().length() == 0) {
            validated = false;
            validationMessages.append(getResources().getString(R.string.order_must_have_a_date));
            validationMessages.append("\n");
        }
        hasta aquí */
        if (orderAddressSelected.getText() == null || orderAddressSelected.getText().toString().length() == 0) {
            validated = false;
            validationMessages.append(getResources().getString(R.string.order_must_have_an_address));
            validationMessages.append("\n");
        }
        if (validationMessages.length() > 0)
            MessagesForUser.showMessage(getView(), validationMessages.toString(), Toast.LENGTH_LONG, Level.SEVERE);
        return validated;
    }

    @Click(view = R.id.fragment_order_catalog_btn)
    public void onCatalogButtonPressed() {
        if (!readOnlyMode)
            startActivityForResult(
                getNextIntent(new Bundle(), getView(),
                        ProductCatalogActivity.class), 0);
        else
            MessagesForUser.showMessage(getView(), "No puede añadir productos a un pedido ya confirmado.", Toast.LENGTH_LONG, Level.SEVERE);
    }

    private void setOrderNumber() {
        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... params) {
                return OrderRepository.getInstance().getNextIdNumber()
                        .longValue();
            }

            protected void onPostExecute(Long result) {
                orderNumber.setText("" + result);
            };
        }.execute();
    }

    private void clearContext() {
        MidbanApplication
                .removeValueInContext(ContextAttributes.PARTNER_TO_ORDER);
        OrderRepository.clearCurrentOrder();
    }

    public static class DatePickerFragment extends android.support.v4.app.DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        public static EditText editText;
        DatePicker dpResult;

        public DatePickerFragment() {
            super();
        }

        public static DatePickerFragment create(EditText edition) {
            DatePickerFragment result = new DatePickerFragment();
            editText = edition;
            return result;
        }

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            editText.setText(String.format("%02d", day) + "."
                    + String.format("%02d", (month + 1)) + "."
                    + String.valueOf(year));
            try {
                OrderRepository.getCurrentOrder().setDateOrder(
                        DateUtil.toFormattedString(DateUtil.parseDate(
                                editText.getText().toString(),
                                "dd.MM.yyyy")));
            } catch (ParseException e) {
                //do nothing
            }
            this.dismiss();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.order, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
        case R.id.home_item:
            startActivityForResult(
                    getNextIntent(new Bundle(), getView(),
                            PortadaActivity.class), 0);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
