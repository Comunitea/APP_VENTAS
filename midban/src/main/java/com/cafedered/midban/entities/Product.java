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

import com.cafedered.cafedroidlitedao.annotations.Entity;
import com.cafedered.cafedroidlitedao.annotations.Id;
import com.cafedered.cafedroidlitedao.annotations.Property;
import com.cafedered.midban.annotations.Remote;
import com.cafedered.midban.annotations.RemoteProperty;
import com.cafedered.midban.service.repositories.ProductTemplateRepository;
import com.cafedered.midban.service.repositories.ProductUlRepository;
import com.cafedered.midban.service.repositories.ProductUomRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.debortoliwines.openerp.api.FilterCollection;
import com.debortoliwines.openerp.api.OpeneERPApiException;

import java.math.BigDecimal;

@Entity(tableName = "product_product")
@Remote(object = "product.product")
public class Product extends BaseRemoteEntity {

    private static final long serialVersionUID = 2325116999521804028L;

    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

    @Property(columnName = "image_medium")
    @RemoteProperty(name = "image_medium")
    private byte[] imageMedium;

    @Property(columnName = "image_small")
    @RemoteProperty(name = "image_small")
    private byte[] imageSmall;

    @Property(columnName = "name_template")
    @RemoteProperty(name = "name_template")
    private String nameTemplate;

    @Property(columnName = "product_tmpl_id")
    @RemoteProperty(name = "product_tmpl_id",
            entityRef = ProductTemplate.class,
            repositoryRef = ProductTemplateRepository.class,
            orderedProperties = { "id", "name" })
    private Number productTmplId;

    @Property(columnName = "ul")
    @RemoteProperty(name = "ul",
            entityRef = ProductUl.class,
            repositoryRef = ProductUlRepository.class,
            orderedProperties = { "id", "name" })
    private Number ul;

//    @Property(columnName = "code")
//    @RemoteProperty(name = "code")
//    private String code;

    @Property(columnName = "color")
    @RemoteProperty(name = "color")
    private Number color;

    @Property(columnName = "lst_price")
    @RemoteProperty(name = "lst_price")
    private Number lstPrice;

    @Property(columnName = "partner_ref")
    @RemoteProperty(name = "partner_ref")
    private String partnerRef;
    //
    // @Property(columnName = "price", type = Property.SQLType.REAL)
    // @RemoteProperty(name = "price")
    // private Number price;

    @Property(columnName = "price_extra")
    @RemoteProperty(name = "price_extra")
    private Number priceExtra;

    @Property(columnName = "price_margin")
    @RemoteProperty(name = "price_margin")
    private Number priceMargin;

    @Property(columnName = "sale_ok")
    @RemoteProperty(name = "sale_ok")
    private Boolean saleOk;

    @Property(columnName = "sale_app")
    @RemoteProperty(name = "sale_app")
    private Boolean saleApp;

    @Property(columnName = "purchase_ok")
    @RemoteProperty(name = "purchase_ok")
    private Boolean purchaseOk;

    @Property(columnName = "var_weight")
    @RemoteProperty(name = "var_weight")
    private Boolean varWeight;

    @Property(columnName = "consignment")
    @RemoteProperty(name = "consignment")
    private Boolean consignment;

    @Property(columnName = "bulk")
    @RemoteProperty(name = "bulk")
    private Boolean bulk;

    @Property(columnName = "type")
    @RemoteProperty(name = "type")
    private String type;

    @Property(columnName = "product_class")
    @RemoteProperty(name = "product_class")
    private String productClass;

    @Property(columnName = "uom_id")
    @RemoteProperty(name = "uom_id")
    private Number uomId;

    @Property(columnName = "uos_id")
    @RemoteProperty(name = "uos_id")
    private Number uosId;

    @Property(columnName = "list_price")
    @RemoteProperty(name = "list_price")
    private Number listPrice;

    @Property(columnName = "last_purchase_price")
    @RemoteProperty(name = "last_purchase_price")
    private Number lastPurchasePrice;

    @Property(columnName = "default_code")
    @RemoteProperty(name = "default_code")
    private String defaultCode;

    @Property(columnName = "ean13")
    @RemoteProperty(name = "ean13")
    private String ean13;

    @Property(columnName = "dun14")
    @RemoteProperty(name = "dun14")
    private String dun14;

    @Property(columnName = "ean14")
    @RemoteProperty(name = "ean14")
    private String ean14;

    @Property(columnName = "sale_type")
    @RemoteProperty(name = "sale_type")
    private String saleType;

    @Property(columnName = "product_type")
    @RemoteProperty(name = "product_type")
    private String productType;

    @Property(columnName = "temperature", type = Property.SQLType.REAL)
    @RemoteProperty(name = "temperature")
    private Number temperature;

    @Property(columnName = "mark")
    @RemoteProperty(name = "mark")
    private String mark;

    @Property(columnName = "min_unit")
    @RemoteProperty(name = "min_unit")
    private String minUnit;

    @Property(columnName = "scientific_name")
    @RemoteProperty(name = "scientific_name")
    private String scientificName;

    @Property(columnName = "web")
    @RemoteProperty(name = "web")
    private String web;

    @Property(columnName = "glazed")
    @RemoteProperty(name = "glazed")
    private Boolean glazed;

    @Property(columnName = "first_course")
    @RemoteProperty(name = "first_course")
    private Boolean firstCourse;

    @Property(columnName = "second_course")
    @RemoteProperty(name = "second_course")
    private Boolean secondCourse;

    @Property(columnName = "dessert")
    @RemoteProperty(name = "dessert")
    private Boolean dessert;

    @Property(columnName = "breakfast_snack")
    @RemoteProperty(name = "breakfast_snack")
    private Boolean breakfastSnack;

    @Property(columnName = "accompaniment")
    @RemoteProperty(name = "accompaniment")
    private Boolean accompaniment;

    @Property(columnName = "procure_method")
    @RemoteProperty(name = "procure_method")
    private String procureMethod;

    @Property(columnName = "supply_method")
    @RemoteProperty(name = "supply_method")
    private String supplyMethod;

    @Property(columnName = "purchase_requisition")
    @RemoteProperty(name = "purchase_requisition")
    private Boolean purchaseRequisition;

    @Property(columnName = "cost_method")
    @RemoteProperty(name = "cost_method")
    private String costMethod;

    @Property(columnName = "standard_price")
    @RemoteProperty(name = "standard_price")
    private Number standardPrice;

    @Property(columnName = "cmc")
    @RemoteProperty(name = "cmc")
    private Number cmc;

    @Property(columnName = "sec_margin")
    @RemoteProperty(name = "sec_margin")
    private Number secMargin;

    @Property(columnName = "produce_delay")
    @RemoteProperty(name = "produce_delay")
    private Number produceDelay;

    @Property(columnName = "active")
    @RemoteProperty(name = "active")
    private Boolean active;

    @Property(columnName = "uom_po_id")
    @RemoteProperty(name = "uom_po_id")
    private Number uomPoId;

    @Property(columnName = "manufacturer_pname")
    @RemoteProperty(name = "manufacturer_pname")
    private String manufacturerPname;

    @Property(columnName = "manufacturer_pref")
    @RemoteProperty(name = "manufacturer_pref")
    private String manufacturerPref;

    @Property(columnName = "supplier_un_ca")
    @RemoteProperty(name = "supplier_un_ca")
    private Number supplierUnCa;

    @Property(columnName = "supplier_ca_ma")
    @RemoteProperty(name = "supplier_ca_ma")
    private Number supplierCaMa;

    @Property(columnName = "supplier_ma_pa")
    @RemoteProperty(name = "supplier_ma_pa")
    private Number supplierMaPA;

    @Property(columnName = "supplier_ca_width")
    @RemoteProperty(name = "supplier_ca_width")
    private Number supplierCaWidth;

    @Property(columnName = "supplier_ma_width")
    @RemoteProperty(name = "supplier_ma_width")
    private Number supplierMaWidth;

    @Property(columnName = "supplier_pa_width")
    @RemoteProperty(name = "supplier_pa_width")
    private Number supplierPaWidth;

    @Property(columnName = "supplier_ca_heigth")
    @RemoteProperty(name = "supplier_ca_heigth")
    private Number supplierCaHeigth;

    @Property(columnName = "supplier_ma_heigth")
    @RemoteProperty(name = "supplier_ma_heigth")
    private Number supplierMaHeigth;

    @Property(columnName = "supplier_pa_heigth")
    @RemoteProperty(name = "supplier_pa_heigth")
    private Number supplierPaHeigth;

    @Property(columnName = "supplier_ca_length")
    @RemoteProperty(name = "supplier_ca_length")
    private Number supplierCaLength;

    @Property(columnName = "supplier_ma_length")
    @RemoteProperty(name = "supplier_ma_length")
    private Number supplierMaLength;

    @Property(columnName = "life")
    @RemoteProperty(name = "life")
    private Number life;

    @Property(columnName = "qty_available", type = Property.SQLType.REAL)
    @RemoteProperty(name = "qty_available")
    private Number qtyAvailable;

    @Property(columnName = "incoming_qty", type = Property.SQLType.REAL)
    @RemoteProperty(name = "incoming_qty")
    private Number incomingQty;

    @Property(columnName = "outgoing_qty", type = Property.SQLType.REAL)
    @RemoteProperty(name = "outgoing_qty")
    private Number outgoingQty;

    @Property(columnName = "virtual_available")
    @RemoteProperty(name = "virtual_available")
    private Number virtualAvailable;

    @Property(columnName = "track_production")
    @RemoteProperty(name = "track_production")
    private Boolean trackProduction;

    @Property(columnName = "track_incoming")
    @RemoteProperty(name = "track_incoming")
    private Boolean trackIncoming;

    @Property(columnName = "track_outgoing")
    @RemoteProperty(name = "track_outgoing")
    private Boolean trackOutgoing;

    @Property(columnName = "state")
    @RemoteProperty(name = "state")
    private String state;

    @Property(columnName = "loc_rack")
    @RemoteProperty(name = "loc_rack")
    private String locRack;

    @Property(columnName = "loc_row")
    @RemoteProperty(name = "loc_row")
    private String locRow;

    @Property(columnName = "loc_case")
    @RemoteProperty(name = "loc_case")
    private String locCase;

    @Property(columnName = "volume", type = Property.SQLType.REAL)
    @RemoteProperty(name = "volume")
    private Number volume;

    @Property(columnName = "weight", type = Property.SQLType.REAL)
    @RemoteProperty(name = "weight")
    private Number weight;

    @Property(columnName = "weight_net", type = Property.SQLType.REAL)
    @RemoteProperty(name = "weight_net")
    private Number weightNet;

    @Property(columnName = "life_time")
    @RemoteProperty(name = "life_time")
    private Number lifeTime;

    @Property(columnName = "use_time")
    @RemoteProperty(name = "use_time")
    private Number useTime;

    @Property(columnName = "removal_time")
    @RemoteProperty(name = "removal_time")
    private Number removalTime;

    @Property(columnName = "alert_time")
    @RemoteProperty(name = "alert_time")
    private Number alertTime;

    @Property(columnName = "warranty")
    @RemoteProperty(name = "warranty")
    private Number warranty;

    @Property(columnName = "sale_delay")
    @RemoteProperty(name = "sale_delay")
    private Number saleDelay;

    @Property(columnName = "kg_un")
    @RemoteProperty(name = "kg_un")
    private Number kgUn;

    @Property(columnName = "un_ca")
    @RemoteProperty(name = "un_ca")
    private Number unCa;

    @Property(columnName = "ca_ma")
    @RemoteProperty(name = "ca_ma")
    private Number caMa;

    @Property(columnName = "ma_pa")
    @RemoteProperty(name = "ma_pa")
    private Number maPa;

    @Property(columnName = "un_width")
    @RemoteProperty(name = "un_width")
    private Number unWidth;

    @Property(columnName = "ca_width")
    @RemoteProperty(name = "ca_width")
    private Number caWidth;

    @Property(columnName = "ma_width")
    @RemoteProperty(name = "ma_width")
    private Number maWidth;

    @Property(columnName = "pa_width")
    @RemoteProperty(name = "pa_width")
    private Number paWidth;

    @Property(columnName = "un_height")
    @RemoteProperty(name = "un_height")
    private Number unHeight;

    @Property(columnName = "ca_height")
    @RemoteProperty(name = "ca_height")
    private Number caHeight;

    @Property(columnName = "ma_height")
    @RemoteProperty(name = "ma_height")
    private Number maHeight;

    @Property(columnName = "pa_height")
    @RemoteProperty(name = "pa_height")
    private Number paHeight;

    @Property(columnName = "un_length")
    @RemoteProperty(name = "un_length")
    private Number unLength;

    @Property(columnName = "ca_length")
    @RemoteProperty(name = "ca_length")
    private Number caLength;

    @Property(columnName = "ma_length")
    @RemoteProperty(name = "ma_length")
    private Number maLength;

    @Property(columnName = "pa_length")
    @RemoteProperty(name = "pa_length")
    private Number paLength;

    @Property(columnName = "sale_line_warn")
    @RemoteProperty(name = "sale_line_warn")
    private String saleLineWarn;

    @Property(columnName = "sale_line_warn_msg")
    @RemoteProperty(name = "sale_line_warn_msg")
    private String saleLineWarnMsg;

    @Property(columnName = "purchase_line_warn")
    @RemoteProperty(name = "purchase_line_warn")
    private String purchaseLineWarn;

    @Property(columnName = "purchase_line_warn_msg")
    @RemoteProperty(name = "purchase_line_warn_msg")
    private String purchaseLineWarnMsg;

    @Property(columnName = "box_units")
    @RemoteProperty(name = "box_units")
    private Number boxUnits;

    @Property(columnName = "pallet_boxes_pallet")
    @RemoteProperty(name = "pallet_boxes_pallet")
    private Number boxesPerPallet;

    @Property(columnName = "pallet_gross_weight")
    @RemoteProperty(name = "pallet_gross_weight")
    private Number palletGrossWeight;

    @Property(columnName = "pallet_total_height")
    @RemoteProperty(name = "pallet_total_height")
    private Number palletTotalHeight;

    @Property(columnName = "pallet_ul")
    @RemoteProperty(name = "pallet_ul")
    private Number typeOfPallet;


    @Property(columnName = "substitute_products")
    private String substituteProducts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getImageMedium() {
        return imageMedium;
    }

    public void setImageMedium(byte[] imageMedium) {
        this.imageMedium = imageMedium;
    }

    public byte[] getImageSmall() {
        return imageSmall;
    }

    public void setImageSmall(byte[] imageSmall) {
        this.imageSmall = imageSmall;
    }

    public String getNameTemplate() {
        return nameTemplate;
    }

    public void setNameTemplate(String nameTemplate) {
        this.nameTemplate = nameTemplate;
    }

    public Number getProductTmplId() {
        return productTmplId;
    }

    public void setProductTmplId(Number productTmplId) {
        this.productTmplId = productTmplId;
    }

    public String getCode() {
        return defaultCode;
    }

    public void setCode(String code) {
        this.defaultCode = code;
    }

    public Number getColor() {
        return color;
    }

    public void setColor(Number color) {
        this.color = color;
    }

    public Number getLstPrice() {
        return lstPrice;
    }

    public void setLstPrice(Number lstPrice) {
        this.lstPrice = lstPrice;
    }

    public String getPartnerRef() {
        return partnerRef;
    }

    public void setPartnerRef(String partnerRef) {
        this.partnerRef = partnerRef;
    }

    // public Number getPrice() {
    // return price;
    // }
    //
    // public void setPrice(Number price) {
    // this.price = price;
    // }

    public Number getPriceExtra() {
        return priceExtra;
    }

    public void setPriceExtra(Number priceExtra) {
        this.priceExtra = priceExtra;
    }

    public Number getPriceMargin() {
        return priceMargin;
    }

    public void setPriceMargin(Number priceMargin) {
        this.priceMargin = priceMargin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProductClass() {
        return productClass;
    }

    public void setProductClass(String productClass) {
        this.productClass = productClass;
    }

    public Number getUomId() {
        return uomId;
    }

    public void setUomId(Number uomId) {
        this.uomId = uomId;
    }

    public Number getListPrice() {
        return listPrice;
    }

    public void setListPrice(Number listPrice) {
        this.listPrice = listPrice;
    }

    public Number getLastPurchasePrice() {
        return lastPurchasePrice;
    }

    public void setLastPurchasePrice(Number lastPurchasePrice) {
        this.lastPurchasePrice = lastPurchasePrice;
    }

    public String getDefaultCode() {
        return defaultCode;
    }

    public void setDefaultCode(String defaultCode) {
        this.defaultCode = defaultCode;
    }

    public String getEan13() {
        return ean13;
    }

    public void setEan13(String ean13) {
        this.ean13 = ean13;
    }

    public String getDun14() {
        return dun14;
    }

    public void setDun14(String dun14) {
        this.dun14 = dun14;
    }

    public String getEan14() {
        return ean14;
    }

    public void setEan14(String ean14) {
        this.ean14 = ean14;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Number getTemperature() {
        return temperature;
    }

    public void setTemperature(Number temperature) {
        this.temperature = temperature;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getProcureMethod() {
        return procureMethod;
    }

    public void setProcureMethod(String procureMethod) {
        this.procureMethod = procureMethod;
    }

    public String getSupplyMethod() {
        return supplyMethod;
    }

    public void setSupplyMethod(String supplyMethod) {
        this.supplyMethod = supplyMethod;
    }

    public String getCostMethod() {
        return costMethod;
    }

    public void setCostMethod(String costMethod) {
        this.costMethod = costMethod;
    }

    public Number getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(Number standardPrice) {
        this.standardPrice = standardPrice;
    }

    public Number getCmc() {
        return cmc;
    }

    public void setCmc(Number cmc) {
        this.cmc = cmc;
    }

    public Number getSecMargin() {
        return secMargin;
    }

    public void setSecMargin(Number secMargin) {
        this.secMargin = secMargin;
    }

    public Number getProduceDelay() {
        return produceDelay;
    }

    public void setProduceDelay(Number produceDelay) {
        this.produceDelay = produceDelay;
    }

    public Number getUomPoId() {
        return uomPoId;
    }

    public void setUomPoId(Number uomPoId) {
        this.uomPoId = uomPoId;
    }

    public String getManufacturerPname() {
        return manufacturerPname;
    }

    public void setManufacturerPname(String manufacturerPname) {
        this.manufacturerPname = manufacturerPname;
    }

    public String getManufacturerPref() {
        return manufacturerPref;
    }

    public void setManufacturerPref(String manufacturerPref) {
        this.manufacturerPref = manufacturerPref;
    }

    public Number getSupplierUnCa() {
        return supplierUnCa;
    }

    public void setSupplierUnCa(Number supplierUnCa) {
        this.supplierUnCa = supplierUnCa;
    }

    public Number getSupplierCaMa() {
        return supplierCaMa;
    }

    public void setSupplierCaMa(Number supplierCaMa) {
        this.supplierCaMa = supplierCaMa;
    }

    public Number getSupplierMaPA() {
        return supplierMaPA;
    }

    public void setSupplierMaPA(Number supplierMaPA) {
        this.supplierMaPA = supplierMaPA;
    }

    public Number getSupplierCaWidth() {
        return supplierCaWidth;
    }

    public void setSupplierCaWidth(Number supplierCaWidth) {
        this.supplierCaWidth = supplierCaWidth;
    }

    public Number getSupplierMaWidth() {
        return supplierMaWidth;
    }

    public void setSupplierMaWidth(Number supplierMaWidth) {
        this.supplierMaWidth = supplierMaWidth;
    }

    public Number getSupplierPaWidth() {
        return supplierPaWidth;
    }

    public void setSupplierPaWidth(Number supplierPaWidth) {
        this.supplierPaWidth = supplierPaWidth;
    }

    public Number getSupplierCaHeigth() {
        return supplierCaHeigth;
    }

    public void setSupplierCaHeigth(Number supplierCaHeigth) {
        this.supplierCaHeigth = supplierCaHeigth;
    }

    public Number getSupplierMaHeigth() {
        return supplierMaHeigth;
    }

    public void setSupplierMaHeigth(Number supplierMaHeigth) {
        this.supplierMaHeigth = supplierMaHeigth;
    }

    public Number getSupplierPaHeigth() {
        return supplierPaHeigth;
    }

    public void setSupplierPaHeigth(Number supplierPaHeigth) {
        this.supplierPaHeigth = supplierPaHeigth;
    }

    public Number getSupplierCaLength() {
        return supplierCaLength;
    }

    public void setSupplierCaLength(Number supplierCaLength) {
        this.supplierCaLength = supplierCaLength;
    }

    public Number getSupplierMaLength() {
        return supplierMaLength;
    }

    public void setSupplierMaLength(Number supplierMaLength) {
        this.supplierMaLength = supplierMaLength;
    }

    public Number getLife() {
        return life;
    }

    public void setLife(Number life) {
        this.life = life;
    }

    public Number getQtyAvailable() {
        return qtyAvailable;
    }

    public void setQtyAvailable(Number qtyAvailable) {
        this.qtyAvailable = qtyAvailable;
    }

    public Number getIncomingQty() {
        return incomingQty;
    }

    public void setIncomingQty(Number incomingQty) {
        this.incomingQty = incomingQty;
    }

    public Number getOutgoingQty() {
        return outgoingQty;
    }

    public void setOutgoingQty(Number outgoingQty) {
        this.outgoingQty = outgoingQty;
    }

    public Number getVirtualAvailable() {
        if (virtualAvailable != null)
            return (Number) new BigDecimal(virtualAvailable.floatValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
        return virtualAvailable;
    }

    public void setVirtualAvailable(Number virtualAvailable) {
        this.virtualAvailable = virtualAvailable;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocRack() {
        return locRack;
    }

    public void setLocRack(String locRack) {
        this.locRack = locRack;
    }

    public String getLocRow() {
        return locRow;
    }

    public void setLocRow(String locRow) {
        this.locRow = locRow;
    }

    public String getLocCase() {
        return locCase;
    }

    public void setLocCase(String locCase) {
        this.locCase = locCase;
    }

    public Number getVolume() {
        return volume;
    }

    public void setVolume(Number volume) {
        this.volume = volume;
    }

    public Number getWeight() {
        return weight;
    }

    public void setWeight(Number weight) {
        this.weight = weight;
    }

    public Number getWeightNet() {
        return weightNet;
    }

    public void setWeightNet(Number weightNet) {
        this.weightNet = weightNet;
    }

    public Number getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(Number lifeTime) {
        this.lifeTime = lifeTime;
    }

    public Number getUseTime() {
        return useTime;
    }

    public void setUseTime(Number useTime) {
        this.useTime = useTime;
    }

    public Number getRemovalTime() {
        return removalTime;
    }

    public void setRemovalTime(Number removalTime) {
        this.removalTime = removalTime;
    }

    public Number getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(Number alertTime) {
        this.alertTime = alertTime;
    }

    public Number getWarranty() {
        return warranty;
    }

    public void setWarranty(Number warranty) {
        this.warranty = warranty;
    }

    public Number getSaleDelay() {
        return saleDelay;
    }

    public void setSaleDelay(Number saleDelay) {
        this.saleDelay = saleDelay;
    }

    public Number getKgUn() {
        return kgUn;
    }

    public void setKgUn(Number kgUn) {
        this.kgUn = kgUn;
    }

    public Number getUnCa() {
        return unCa;
    }

    public void setUnCa(Number unCa) {
        this.unCa = unCa;
    }

    public Number getCaMa() {
        return caMa;
    }

    public void setCaMa(Number caMa) {
        this.caMa = caMa;
    }

    public Number getMaPa() {
        return maPa;
    }

    public void setMaPa(Number maPa) {
        this.maPa = maPa;
    }

    public Number getUnWidth() {
        return unWidth;
    }

    public void setUnWidth(Number unWidth) {
        this.unWidth = unWidth;
    }

    public Number getCaWidth() {
        return caWidth;
    }

    public void setCaWidth(Number caWidth) {
        this.caWidth = caWidth;
    }

    public Number getMaWidth() {
        return maWidth;
    }

    public void setMaWidth(Number maWidth) {
        this.maWidth = maWidth;
    }

    public Number getPaWidth() {
        return paWidth;
    }

    public void setPaWidth(Number paWidth) {
        this.paWidth = paWidth;
    }

    public Number getUnHeight() {
        return unHeight;
    }

    public void setUnHeight(Number unHeight) {
        this.unHeight = unHeight;
    }

    public Number getCaHeight() {
        return caHeight;
    }

    public void setCaHeight(Number caHeight) {
        this.caHeight = caHeight;
    }

    public Number getMaHeight() {
        return maHeight;
    }

    public void setMaHeight(Number maHeight) {
        this.maHeight = maHeight;
    }

    public Number getPaHeight() {
        return paHeight;
    }

    public void setPaHeight(Number paHeight) {
        this.paHeight = paHeight;
    }

    public Number getUnLength() {
        return unLength;
    }

    public void setUnLength(Number unLength) {
        this.unLength = unLength;
    }

    public Number getCaLength() {
        return caLength;
    }

    public void setCaLength(Number caLength) {
        this.caLength = caLength;
    }

    public Number getMaLength() {
        return maLength;
    }

    public void setMaLength(Number maLength) {
        this.maLength = maLength;
    }

    public Number getPaLength() {
        return paLength;
    }

    public void setPaLength(Number paLength) {
        this.paLength = paLength;
    }

    public String getSaleLineWarn() {
        return saleLineWarn;
    }

    public void setSaleLineWarn(String saleLineWarn) {
        this.saleLineWarn = saleLineWarn;
    }

    public String getSaleLineWarnMsg() {
        return saleLineWarnMsg;
    }

    public void setSaleLineWarnMsg(String saleLineWarnMsg) {
        this.saleLineWarnMsg = saleLineWarnMsg;
    }

    public String getPurchaseLineWarn() {
        return purchaseLineWarn;
    }

    public void setPurchaseLineWarn(String purchaseLineWarn) {
        this.purchaseLineWarn = purchaseLineWarn;
    }

    public String getPurchaseLineWarnMsg() {
        return purchaseLineWarnMsg;
    }

    public void setPurchaseLineWarnMsg(String purchaseLineWarnMsg) {
        this.purchaseLineWarnMsg = purchaseLineWarnMsg;
    }

    public Boolean getSaleOk() {
        return saleOk;
    }

    public void setSaleOk(Boolean saleOk) {
        this.saleOk = saleOk;
    }

    public Boolean getSaleApp() {
        return saleApp;
    }

    public void setSaleApp(Boolean saleApp) {
        this.saleApp = saleApp;
    }

    public Boolean getPurchaseOk() {
        return purchaseOk;
    }

    public void setPurchaseOk(Boolean purchaseOk) {
        this.purchaseOk = purchaseOk;
    }

    public Boolean getVarWeight() {
        return varWeight;
    }

    public void setVarWeight(Boolean varWeight) {
        this.varWeight = varWeight;
    }

    public Boolean getConsignment() {
        return consignment;
    }

    public void setConsignment(Boolean consignment) {
        this.consignment = consignment;
    }

    public Boolean getBulk() {
        return bulk;
    }

    public void setBulk(Boolean bulk) {
        this.bulk = bulk;
    }

    public Boolean getGlazed() {
        return glazed;
    }

    public void setGlazed(Boolean glazed) {
        this.glazed = glazed;
    }

    public Boolean getFirstCourse() {
        return firstCourse;
    }

    public void setFirstCourse(Boolean firstCourse) {
        this.firstCourse = firstCourse;
    }

    public Boolean getSecondCourse() {
        return secondCourse;
    }

    public void setSecondCourse(Boolean secondCourse) {
        this.secondCourse = secondCourse;
    }

    public Boolean getDessert() {
        return dessert;
    }

    public void setDessert(Boolean dessert) {
        this.dessert = dessert;
    }

    public Boolean getBreakfastSnack() {
        return breakfastSnack;
    }

    public void setBreakfastSnack(Boolean breakfastSnack) {
        this.breakfastSnack = breakfastSnack;
    }

    public Boolean getAccompaniment() {
        return accompaniment;
    }

    public void setAccompaniment(Boolean accompaniment) {
        this.accompaniment = accompaniment;
    }

    public Boolean getPurchaseRequisition() {
        return purchaseRequisition;
    }

    public void setPurchaseRequisition(Boolean purchaseRequisition) {
        this.purchaseRequisition = purchaseRequisition;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getTrackProduction() {
        return trackProduction;
    }

    public void setTrackProduction(Boolean trackProduction) {
        this.trackProduction = trackProduction;
    }

    public Boolean getTrackIncoming() {
        return trackIncoming;
    }

    public void setTrackIncoming(Boolean trackIncoming) {
        this.trackIncoming = trackIncoming;
    }

    public Boolean getTrackOutgoing() {
        return trackOutgoing;
    }

    public void setTrackOutgoing(Boolean trackOutgoing) {
        this.trackOutgoing = trackOutgoing;
    }

    public String getMinUnit() {
        return minUnit;
    }

    public void setMinUnit(String minUnit) {
        this.minUnit = minUnit;
    }

    public String getSubstituteProducts() {
        return substituteProducts;
    }

    public void setSubstituteProducts(String substituteProducts) {
        this.substituteProducts = substituteProducts;
    }

    public Number getUl() {
        return ul;
    }

    public void setUl(Number ul) {
        this.ul = ul;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public FilterCollection getRemoteFilters() {
        FilterCollection filters = new FilterCollection();
        try {
            filters.add("sale_ok", "=", true);
        } catch (OpeneERPApiException e) {
            e.printStackTrace();
        }
        return filters;
    }

    @Override
    public Integer getPendingSynchronization() {
        return null;
    }

    @Override
    public void setPendingSynchronization(Integer pendingSynchronization) {

    }

    public ProductTemplate getProductTemplate() {
        try {
            return ProductTemplateRepository.getInstance().getById(
                    this.productTmplId.longValue());
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        return null;//TODO no enviar
    }

    public ProductUl getProductUl() {
        try {
            return ProductUlRepository.getInstance().getById(
                    this.ul.longValue());
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        return null;
    }

    public ProductUom getUom() {
        try {
            return ProductUomRepository.getInstance().getById(
                    this.uomId.longValue());
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        return null;
    }

    public ProductUom getUos() {
        try {
            if ((this.uosId != null) && (this.uosId.longValue() != 0.0)){
                return ProductUomRepository.getInstance().getById(
                        this.uosId.longValue());
            }
            else
                return getUom();

        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        return null;
    }

    // FIXME implement this when we know data of minPrice for product
    // public Float getMinPrice() {
    // return price.floatValue() - (0.1F * price.floatValue());
    // }

    // FIXME implement this when we know data of maxDiscount for product
    public Float getMaxDiscount() {
        return 10F;
    }

    public Number getUosId() {
        return uosId;
    }

    public void setUosId(Number uosId) {
        this.uosId = uosId;
    }

    public Number getBoxUnits() {
        return boxUnits;
    }

    public void setBoxUnits(Number boxUnits) {
        this.boxUnits = boxUnits;
    }

    public Number getBoxesPerPallet() {
        return boxesPerPallet;
    }

    public void setBoxesPerPallet(Number boxesPerPallet) {
        this.boxesPerPallet = boxesPerPallet;
    }

    public Number getPalletGrossWeight() {
        return palletGrossWeight;
    }

    public void setPalletGrossWeight(Number palletGrossWeight) {
        this.palletGrossWeight = palletGrossWeight;
    }

    public Number getPalletTotalHeight() {
        return palletTotalHeight;
    }

    public void setPalletTotalHeight(Number palletTotalHeight) {
        this.palletTotalHeight = palletTotalHeight;
    }

    public Number getTypeOfPallet() {
        return typeOfPallet;
    }

    public void setTypeOfPallet(Number typeOfPallet) {
        this.typeOfPallet = typeOfPallet;
    }
}
