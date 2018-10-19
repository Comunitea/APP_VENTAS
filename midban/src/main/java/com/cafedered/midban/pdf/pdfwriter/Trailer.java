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

public class Trailer extends Base {

    private int mXRefByteOffset;
    private int mObjectsCount;
    private String mId;
    private Dictionary mTrailerDictionary;

    public Trailer() {
        clear();
    }

    public void setId(String Value) {
        this.mId = Value;
    }

    public void setCrossReferenceTableByteOffset(int Value) {
        this.mXRefByteOffset = Value;
    }

    public void setObjectsCount(int Value) {
        this.mObjectsCount = Value;
    }

    private void renderDictionary() {
        this.mTrailerDictionary.setContent("  /Size " + Integer.toString(this.mObjectsCount));
        this.mTrailerDictionary.addNewLine();
        this.mTrailerDictionary.addContent("  /Root 1 0 R");
        this.mTrailerDictionary.addNewLine();
        this.mTrailerDictionary.addContent("  /ID [<" + this.mId + "> <" + this.mId + ">]");
        this.mTrailerDictionary.addNewLine();
    }

    private String render() {
        renderDictionary();
        final StringBuilder sb = new StringBuilder();
        sb.append("trailer");
        sb.append("\n");
        sb.append(this.mTrailerDictionary.toPDFString());
        sb.append("startxref");
        sb.append("\n");
        sb.append(this.mXRefByteOffset);
        sb.append("\n");
        sb.append("%%EOF");
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public String toPDFString() {
        return render();
    }

    @Override
    public void clear() {
        this.mXRefByteOffset = 0;
        this.mTrailerDictionary = new Dictionary();
    }
}
