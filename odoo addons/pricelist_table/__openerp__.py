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
{
    "name": "Price list tables",
    "version": "1.0",
    "author": "Comunitea",
    "category": "custom",
    "website": "www.comunitea.com",
    "description": """
    * Price list table

    """,
    "images": [],
    "depends": ["sale"],
    "data": [

        'security/ir.model.access.csv',
        'cron_table_data.xml',
        'table_pricelist_prices_view.xml',
    ],
    "installable": True,
    "application": False,
}
