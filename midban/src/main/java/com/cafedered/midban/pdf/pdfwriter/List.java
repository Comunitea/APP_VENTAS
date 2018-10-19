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
//
//  Android PDF Writer
//  http://coderesearchlabs.com/androidpdfwriter
//
//  by Javier Santo Domingo (j-a-s-d@coderesearchlabs.com)
//

package com.cafedered.midban.pdf.pdfwriter;

import java.util.ArrayList;

public abstract class List extends Base {

    protected ArrayList<String> mList;

    public List() {
        this.mList = new ArrayList<String>();
    }

    protected String renderList() {
        final StringBuilder sb = new StringBuilder();
        int x = 0;
        while (x < this.mList.size()) {
            sb.append(this.mList.get(x).toString());
            x++;
        }
        return sb.toString();
    }

    @Override
    public void clear() {
        this.mList.clear();
    }
}
