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

public class PDFDocument extends Base {

    private final Header mHeader;
    private final Body mBody;
    private final CrossReferenceTable mCRT;
    private final Trailer mTrailer;

    public PDFDocument() {
        this.mHeader = new Header();
        this.mBody = new Body();
        this.mBody.setByteOffsetStart(this.mHeader.getPDFStringSize());
        this.mBody.setObjectNumberStart(0);
        this.mCRT = new CrossReferenceTable();
        this.mTrailer = new Trailer();
    }

    public IndirectObject newIndirectObject() {
        return this.mBody.getNewIndirectObject();
    }

    public IndirectObject newRawObject(String content) {
        final IndirectObject iobj = this.mBody.getNewIndirectObject();
        iobj.setContent(content);
        return iobj;
    }

    public IndirectObject newDictionaryObject(String dictionaryContent) {
        final IndirectObject iobj = this.mBody.getNewIndirectObject();
        iobj.setDictionaryContent(dictionaryContent);
        return iobj;
    }

    public IndirectObject newStreamObject(String streamContent) {
        final IndirectObject iobj = this.mBody.getNewIndirectObject();
        iobj.setDictionaryContent("  /Length " + Integer.toString(streamContent.length()) + "\n");
        iobj.setStreamContent(streamContent);
        return iobj;
    }

    public void includeIndirectObject(IndirectObject iobj) {
        this.mBody.includeIndirectObject(iobj);
    }

    @Override
    public String toPDFString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.mHeader.toPDFString());
        sb.append(this.mBody.toPDFString());
        this.mCRT.setObjectNumberStart(this.mBody.getObjectNumberStart());
        int x = 0;
        while (x < this.mBody.getObjectsCount()) {
            final IndirectObject iobj = this.mBody.getObjectByNumberID(++x);
            if (iobj != null) {
                this.mCRT.addObjectXRefInfo(iobj.getByteOffset(), iobj.getGeneration(), iobj.getInUse());
            }
        }
        this.mTrailer.setObjectsCount(this.mBody.getObjectsCount());
        this.mTrailer.setCrossReferenceTableByteOffset(sb.length());
        this.mTrailer.setId(Indentifiers.generateId());
        return sb.toString() + this.mCRT.toPDFString() + this.mTrailer.toPDFString();
    }

    @Override
    public void clear() {
        this.mHeader.clear();
        this.mBody.clear();
        this.mCRT.clear();
        this.mTrailer.clear();
    }
}
