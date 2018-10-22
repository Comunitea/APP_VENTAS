# -*- coding: utf-8 -*-
##############################################################################
#
#    Copyright (C) 2018 Comunitea
#
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU Affero General Public License as published
#    by the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU Affero General Public License for more details.
#
#    You should have received a copy of the GNU Affero General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
##############################################################################

from openerp import models, fields, api, _
from datetime import datetime
from dateutil import tz
import time
import logging
_logger = logging.getLogger(__name__)


class SaleOrder(models.Model):
    _inherit = 'sale.order'

    supplier_id = fields.Many2one(
        string="Supplier",
        comodel_name='res.partner',
        readonly=True, select=True,
        domain = [('supplier','=',True)],
        states={'draft': [('readonly', False)]})
    order_policy = fields.Selection(selection_add=[('prepaid', 'Pay before delivery'),
            ('manual', 'Deliver & invoice on demand'),
            ('picking', 'Invoice based on deliveries'),
            ('postpaid', 'Invoice on order after delivery'),
            ('no_bill', 'No bill')])
    supplier_cip = fields.Char(
        string="CIP", size=32, readonly=True,
        states={'draft': [('readonly', False)],'waiting_date': [('readonly', False)],'manual': [('readonly', False)],'progress': [('readonly', False)]},
        help="Código interno del proveedor.")
    shop_id = fields.Many2one(
        string="Sale type",
        comodel_name='sale.shop',
        required=True)
    commercial_partner_id = fields.Many2one(
        comodel_name='res.partner',
        invisible=True)
    effective_date = fields.Date(
        string="Effective Date",
        compute="_get_effective_date", store=True,
        help="Date on which the first Delivery Order was delivered.")
    chanel = fields.Selection([('erp', 'ERP'), ('telesale', 'telesale'),
                                    ('tablet', 'Tablet'),
                                    ('other', 'Other'),
                                    ('ecomerce', 'E-comerce')], 'Chanel',
                                   readonly=True)

    @api.multi
    @api.depends('state')
    def _get_effective_date(self):
        for order in self:
            if order.state in ('cancel', 'draft'):
                order.effective_date = False
            else:
                order.update_effective_date()

    @api.onchange('shop_id')
    def onchange_shop_id(self):
        if self.shop_id:
            self.company_id = self.shop_id.company_id
            self.pricelist_id = self.shop_id.pricelist_id
            self.supplier_id = self.shop_id.supplier_id
            self.order_policy = self.shop_id.order_policy
            self.warehouse_id = self.shop_id.warehouse_id
            if self.shop_id.project_id and not self.project_id:
                self.project_id = self.shop_id.project_id
            if self.partner_id:
                partner_id = self.partner_id.commercial_partner_id
                if self.shop_id.indirect_invoicing:
                    if partner_id.property_product_pricelist_indirect_invoicing:
                        self.pricelist_id = partner_id.property_product_pricelist_indirect_invoicing
                else:
                    if partner_id.property_product_pricelist:
                        self.pricelist_id = partner_id.property_product_pricelist
        else:
            self.pricelist_id = False

    @api.multi
    def action_ship_create(self):
        res = super(SaleOrder, self).action_ship_create()
        user_tz = self.env['res.users'].browse(self._uid).tz
        from_zone = tz.gettz('UTC')
        to_zone = tz.gettz(user_tz)
        for order in self:
            for picking in order.picking_ids:
                if order.requested_date:
                    datetime_requested = \
                        datetime.strptime(order.requested_date,
                                          '%Y-%m-%d %H:%M:%S').\
                        replace(tzinfo=from_zone).astimezone(to_zone)
                    date_requested = datetime.strftime(datetime_requested,
                                                       '%Y-%m-%d')
                    date_effective = date_requested
                else:
                    date_requested = False
                    datetime_effective = \
                        datetime.strptime(order.commitment_date,
                                          '%Y-%m-%d %H:%M:%S').\
                        replace(tzinfo=from_zone).astimezone(to_zone)
                    date_effective = datetime.strftime(datetime_effective,
                                                       '%Y-%m-%d')
                vals = {'note': order.note,
                        'requested_date': date_requested,
                        'effective_date': date_effective,
                        }
                if order.supplier_id and picking.state != 'cancel' \
                        and not picking.supplier_id:
                    vals.update({'supplier_id': order.supplier_id.id})
                picking.write(vals)
        return res

    @api.multi
    def onchange_partner_id(self, part):
        res = super(SaleOrder, self).onchange_partner_id(part)
        if not part:
            res['value']['project_id'] = False
            res['value']['partner_invoice_id'] = False
            res['value']['user_id'] = False
            return res
        company_id = self.env['res.users'].browse(self._uid).company_id.id
        partner = self.env['res.partner'].browse(part)
        rec = self.env['account.analytic.default'].account_get(
                    product_id=False, partner_id=partner.commercial_partner_id.id,
                    user_id=self._uid, date=time.strftime('%Y-%m-%d'), company_id=company_id)
        res['value']['project_id'] = rec and rec.analytic_id.id or False
        # Modificamos para que la dirección de factura sea la que tenga la empresa padre
        addr = partner.commercial_partner_id.address_get(['invoice'])
        res['value']['partner_invoice_id'] = addr['invoice']
        dedicated_salesman = False
        if res['value'].get('partner_shipping_id', False):
            part_ship_id = res['value']['partner_shipping_id']
            partner_ship = self.env['res.partner'].browse(part_ship_id)
            dedicated_salesman = partner_ship.user_id and \
                partner_ship.user_id.id or False
        if dedicated_salesman:
            res['value']['user_id'] = dedicated_salesman
        return res

    @api.multi
    def onchange_delivery_id(self, company_id, partner_id, delivery_id, fiscal_position):
        res = super(SaleOrder, self).onchange_delivery_id(
                company_id, partner_id, delivery_id, fiscal_position)
        if delivery_id:
            partner_ship = self.env['res.partner'].browse(delivery_id)
            res['value']['user_id'] = partner_ship.user_id and \
                partner_ship.user_id.id or \
                (partner_ship.commercial_partner_id.user_id and
                    partner_ship.commercial_partner_id.user_id.id or False)
        return res

    @api.multi
    def onchange_partner_id3(self, part, early_payment_discount=False, payment_term=False, shop=False):
        """extend this event for change the pricelist when the shop is to indirect invoice"""
        res = self.onchange_partner_id2(part, early_payment_discount, payment_term)
        partner_obj = self.env['res.partner'].browse(part)
        res['value']['commercial_partner_id'] = \
            partner_obj.commercial_partner_id.id
        if not part:
            res['value']['pricelist_id'] = False
            return res
        if shop:
            shop_obj = self.env['sale.shop'].browse(shop)
            if shop_obj.pricelist_id:
                res['value']['pricelist_id'] = shop_obj.pricelist_id.id
            if shop_obj.indirect_invoicing:
                if partner_obj.commercial_partner_id.property_product_pricelist_indirect_invoicing:
                    res['value']['pricelist_id'] = \
                        partner_obj.commercial_partner_id.property_product_pricelist_indirect_invoicing.id
            else:
                if partner_obj.commercial_partner_id.property_product_pricelist:
                    res['value']['pricelist_id'] = \
                        partner_obj.commercial_partner_id.property_product_pricelist.id
        else:
            res['value']['pricelist_id'] = \
                partner_obj.commercial_partner_id.property_product_pricelist.id
        return res

    @api.model
    def create_and_confirm(self, vals):
        res = self.create(vals)
        if res:
            #res.check_route()
            res.action_button_confirm()
            _logger.info("APP. Respuesta a create_and_confirm <%s> "
                         %(res))
            return res.id
        _logger.info("APP. Respuesta ERROR!! create_and_confirm <%s> "
                         %(res))
        return False

    @api.multi
    def update_effective_date(self):
        for order in self:
            pickings = order.picking_ids.filtered(lambda r: r.state != 'cancel' and r.effective_date)
            dates_list = pickings.mapped('effective_date')
            min_date = dates_list and min(dates_list) or False
            if order.effective_date != min_date:
                order.effective_date = min_date

class SaleOrderLine(models.Model):
    _inherit = 'sale.order.line'

    @api.multi
    def product_id_change(self, pricelist, product, qty=0,
            uom=False, qty_uos=0, uos=False, name='',
            partner_id=False,
            lang=False, update_tax=True, date_order=False,
            packaging=False,
            fiscal_position=False, flag=False):
        """
        Heredamos para poner por defecto una unidad de venta y convertir a unidad principal
        """
        if not product:
            return {'value': {'th_weight': 0,
                    'product_uos_qty': qty}, 'domain': {'product_uom': [],
                    'product_uos': []}}
        prod_obj = self.env['product.product'].browse(product)
        set_uos = False
        if not uos and prod_obj.uos_id:
            uos = prod_obj.uos_id.id
            qty_uos = 1.0
            uom = False  # Hará que haga la conversión de uos a uom
            set_uos = True
        res = super(SaleOrderLine, self).product_id_change(
                pricelist, product, qty=qty,
                uom=uom, qty_uos=qty_uos, uos=uos, name=name,
                partner_id=partner_id,
                lang=lang, update_tax=update_tax, date_order=date_order,
                packaging=packaging,
                fiscal_position=fiscal_position, flag=flag)
        if set_uos:
            res['value']['product_uos_qty'] = 1.0
            res['value']['product_uos'] = uos
        return res

    @api.multi
    def product_uom_change(self, pricelist, product, qty=0,
            uom=False, qty_uos=0, uos=False, name='', partner_id=False,
            lang=False, update_tax=True, date_order=False):
        """
        Modificamos para que solo permita seleccionar una unidad de medida de la misma categoría y si
        se selecciona una de diferente categoría pone la que tiene por defecto el producto.
        Lo usamos también para la unidad de venta. En este caso si cambia ponemos siempre la asignada en el producto.
        Última modificación: se comenta la parte de la categoría y se añade uom = product_obj.uom_id.id or False para que no se pueda
        cambiar la unidad de medida por defecto tampoco en la venta. (no se pone readonly=True en la vista porque sino no se guarda el valor)
        Con todo esto evitamos sobre todo problemas en precios en facturas (_get_price_unit_invoice)
        """
        if product:
            product_obj = self.env['product.product'].browse(product)
            uom = product_obj.uom_id and product_obj.uom_id.id or False
            uos = product_obj.uos_id and product_obj.uos_id.id or False

        res = super(SaleOrderLine, self).product_uom_change(
                pricelist, product, qty, uom, qty_uos, uos, name,
                partner_id, lang, update_tax, date_order)

        res['value']['product_uom'] = uom
        res['value']['product_uos'] = uos

        if product:
            res['domain'] = {'product_uom': [('category_id', '=', product_obj.uom_id.category_id.id)]} #Esto sobra porque tenemos fijada la uom y no se permite cambiar
        return res
