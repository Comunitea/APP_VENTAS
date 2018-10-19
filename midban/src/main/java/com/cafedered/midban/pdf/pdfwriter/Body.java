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

public class Body extends List {

    private int mByteOffsetStart;
    private int mObjectNumberStart;
    private int mGeneratedObjectsCount;
    private ArrayList<IndirectObject> mObjectsList;

    public Body() {
        super();
        clear();
    }

    public int getObjectNumberStart() {
        return this.mObjectNumberStart;
    }

    public void setObjectNumberStart(int Value) {
        this.mObjectNumberStart = Value;
    }

    public int getByteOffsetStart() {
        return this.mByteOffsetStart;
    }

    public void setByteOffsetStart(int Value) {
        this.mByteOffsetStart = Value;
    }

    public int getObjectsCount() {
        return this.mObjectsList.size();
    }

    private int getNextAvailableObjectNumber() {
        return ++this.mGeneratedObjectsCount + this.mObjectNumberStart;
    }

    public IndirectObject getNewIndirectObject() {
        return getNewIndirectObject(getNextAvailableObjectNumber(), 0, true);
    }

    public IndirectObject getNewIndirectObject(int Number, int Generation, boolean InUse) {
        final IndirectObject iobj = new IndirectObject();
        iobj.setNumberID(Number);
        iobj.setGeneration(Generation);
        iobj.setInUse(InUse);
        return iobj;
    }

    public IndirectObject getObjectByNumberID(int Number) {
        IndirectObject iobj;
        int x = 0;
        while (x < this.mObjectsList.size()) {
            iobj = this.mObjectsList.get(x);
            if (iobj.getNumberID() == Number) {
                return iobj;
            }
            x++;
        }
        return null;
    }

    public void includeIndirectObject(IndirectObject iobj) {
        this.mObjectsList.add(iobj);
    }

    private String render() {
        int x = 0;
        int offset = this.mByteOffsetStart;
        while (x < this.mObjectsList.size()) {
            final IndirectObject iobj = getObjectByNumberID(++x);
            String s = "";
            if (iobj != null) {
                s = iobj.toPDFString() + "\n";
            }
            this.mList.add(s);
            iobj.setByteOffset(offset);
            offset += s.length();
        }
        return renderList();
    }

    @Override
    public String toPDFString() {
        return render();
    }

    @Override
    public void clear() {
        super.clear();
        this.mByteOffsetStart = 0;
        this.mObjectNumberStart = 0;
        this.mGeneratedObjectsCount = 0;
        this.mObjectsList = new ArrayList<IndirectObject>();
    }

}
