# -*- coding: utf-8 -*-
##############################################################################
#
#    Copyright (C) 2015-2018 Coimunitea Servicios Tecnológicos All Rights Reserved
#    $Javier Colmenero Fernández$ <javier@comunitea.com>
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

from openerp import api, models, fields
import openerp.addons.decimal_precision as dp

import time

class ProductTemplate(models.Model):

    _inherit ="product.template"

    sale_app = fields.Boolean("Sale in app")

class ProductPricelist(models.Model):

    _inherit ="product.pricelist"

    in_app = fields.Boolean('Pricelist in APP', default=False)

    def _price_rule_get_multi(self, cr, uid, pricelist, products_by_qty_by_partner, context=None):

        context = context or {}
        date = context.get('date') or time.strftime('%Y-%m-%d')
        date = date[0:10]

        products = map(lambda x: x[0], products_by_qty_by_partner)
        if not products:
            return {}
        version = False
        for v in pricelist.version_id:
            if ((v.date_start is False) or (v.date_start <= date)) and ((v.date_end is False) or (v.date_end >= date)):
                version = v
                break
        if not version:
            return {}

        return super(ProductPricelist, self)._price_rule_get_multi(cr, uid, pricelist, products_by_qty_by_partner, context)

class TablePricelistPrices(models.Model):

    _name = "table.pricelist.prices"
    _rec_name = 'pricelist_id'

    pricelist_id = fields.Many2one('product.pricelist', 'Pricelist',
                                        readonly=True,)
    product_id = fields.Many2one('product.product', 'Product',
                                      readonly=True)
    price = fields.Float('Price', digits_compute= dp.get_precision('Product Price'),
                              readonly=True)

    @api.one
    def recalculate_table_btn(self):
        return self.recalculate_table()

    @api.model
    def recalculate_table(self):
        t_product = self.env["product.product"]
        t_pricelist = self.env["product.pricelist"]
        domain = [('sale_ok', '=', True)]
        prod_objs = t_product.search(domain)
        domain = [('type', '=', 'sale'), ('in_app', '=', True)]
        pricelist_objs = t_pricelist.search(domain, order="id")

        for product in prod_objs:
            #print product.display_name
            table = pricelist_objs.price_get_multi(products_by_qty_by_partner=
                                                   [(product, 1.0, False)])
            product_table = table[product.id]
            for pricelist in pricelist_objs:
                #print pricelist.name
                if pricelist.id in product_table.keys():
                    price = product_table[pricelist.id]

                    if not price or price < -1 or price == 'warn':
                        price = 0.0
                    domain = [
                        ('product_id', '=', product.id),
                        ('pricelist_id', '=', pricelist.id)
                    ]
                    rec_table = self.search(domain, limit=1)
                    if not rec_table:
                        vals = {
                            'product_id': product.id,
                            'pricelist_id': pricelist.id,
                            'price': price
                        }
                        self.create(vals)
                    else:
                        if price != rec_table.price:
                            rec_table.price = price

        # Borro todos los precios a 0 para no sincronizarlos
        sql1 = "delete from table_pricelist_prices where price <= 0.00"
        self._cr.execute(sql1)
        return True
