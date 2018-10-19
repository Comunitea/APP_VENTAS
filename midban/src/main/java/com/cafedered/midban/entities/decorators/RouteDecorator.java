package com.cafedered.midban.entities.decorators;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Created by nacho on 11/05/15.
 */
public class RouteDecorator {
    private Long id;
    private String hour;
    private String name;
    private String partnerContact;
    private String phone;
    private String address;
    private String zip;
    private String town;
    private String email;
    private String horarioApertura;
    private BigDecimal amountToIncome;
    private Integer numOrders;
    private String mediumText;
    private Integer customerListId;


    private RouteDecorator(Long id, String hour, String name, String partnerContact, String phone, String address,
                           String zip, String town, String email, String horarioApertura,
                           BigDecimal amountToIncome, Integer numOrders, String mediumText, Integer customerListId) {
        this.id = id;
        this.hour = hour;
        this.name = name;
        this.phone = phone;
        this.partnerContact = partnerContact;
        this.address = address;
        this.zip = zip;
        this.town = town;
        this.email = email;
        this.horarioApertura = horarioApertura;
        this.amountToIncome = amountToIncome;
        this.numOrders = numOrders;
        this.mediumText = mediumText;
        this.customerListId = customerListId;
    }

    public static RouteDecorator createRouteDecorator(Long id, String hour, String name, String partnerContact, String phone,
                                                      String address, String zip, String town, String email,
                                                      String horarioApertura, BigDecimal amountToIncome,
                                                      Integer numOrders, String mediumText, Integer customerListId) {
        return new RouteDecorator(id, hour, name, partnerContact, phone, address, zip, town, email, horarioApertura, amountToIncome, numOrders, mediumText, customerListId);
    }


    public Integer getCustomerListId() {
        return customerListId;
    }

    public void setCustomerListId(Integer customerListId) {
        this.customerListId = customerListId;
    }

    public String getPartnerContact() {
        return partnerContact;
    }

    public void setPartnerContact(String partnerContact) {
        this.partnerContact = partnerContact;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHorarioApertura() {
        return horarioApertura;
    }

    public void setHorarioApertura(String horarioApertura) {
        this.horarioApertura = horarioApertura;
    }


    public BigDecimal getAmountToIncome() {
        return amountToIncome;
    }

    public void setAmountToIncome(BigDecimal amountToIncome) {
        this.amountToIncome = amountToIncome;
    }


    public String getMediumText() {
        return mediumText;
    }

    public void setMediumText(String mediumText) {
        this.mediumText = mediumText;
    }

    public Integer getNumOrders() {
        return numOrders;
    }

    public void setNumOrders(Integer numOrders) {
        this.numOrders = numOrders;
    }

    public boolean isInPast() {
        Calendar cal = Calendar.getInstance();
        Integer h = Integer.valueOf(hour.split(":")[0]);
        Integer m = Integer.valueOf(hour.split(":")[1]);
        return cal.get(Calendar.HOUR_OF_DAY) > h || (cal.get(Calendar.HOUR_OF_DAY) == h.intValue()
                && cal.get(Calendar.MINUTE) > m.intValue());

    }
}
