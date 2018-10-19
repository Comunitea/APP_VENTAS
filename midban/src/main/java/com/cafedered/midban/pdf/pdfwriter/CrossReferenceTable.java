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

public class CrossReferenceTable extends List {

    private int mObjectNumberStart;

    public CrossReferenceTable() {
        super();
        clear();
    }

    public void setObjectNumberStart(int Value) {
        this.mObjectNumberStart = Value;
    }

    public int getObjectNumberStart() {
        return this.mObjectNumberStart;
    }

    private String getObjectsXRefInfo() {
        return renderList();
    }

    public void addObjectXRefInfo(int ByteOffset, int Generation, boolean InUse) {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("%010d", ByteOffset));
        sb.append(" ");
        sb.append(String.format("%05d", Generation));
        if (InUse) {
            sb.append(" n ");
        } else {
            sb.append(" f ");
        }
        sb.append("\r\n");
        this.mList.add(sb.toString());
    }

    private String render() {
        final StringBuilder sb = new StringBuilder();
        sb.append("xref");
        sb.append("\r\n");
        sb.append(this.mObjectNumberStart);
        sb.append(" ");
        sb.append(this.mList.size());
        sb.append("\r\n");
        sb.append(getObjectsXRefInfo());
        return sb.toString();
    }

    @Override
    public String toPDFString() {
        return render();
    }

    @Override
    public void clear() {
        super.clear();
        addObjectXRefInfo(0, 65536, false); // free objects linked list head
        this.mObjectNumberStart = 0;
    }

}
