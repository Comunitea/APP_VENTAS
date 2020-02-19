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

   
    chanel = fields.Selection([('erp', 'ERP'),
                                    ('tablet', 'Tablet'),
                                    ('other', 'Other'),
                                    ('ecomerce', 'E-comerce')], 'Chanel',
                                   readonly=True)


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

