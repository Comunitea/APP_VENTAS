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

public class IndirectObject extends Base {

    private EnclosedContent mContent;
    private Dictionary mDictionaryContent;
    private Stream mStreamContent;
    private IndirectIdentifier mID;
    private int mByteOffset;
    private boolean mInUse;

    public IndirectObject() {
        clear();
    }

    public void setNumberID(int Value) {
        this.mID.setNumber(Value);
    }

    public int getNumberID() {
        return this.mID.getNumber();
    }

    public void setGeneration(int Value) {
        this.mID.setGeneration(Value);
    }

    public int getGeneration() {
        return this.mID.getGeneration();
    }

    public String getIndirectReference() {
        return this.mID.toPDFString() + " R";
    }

    public void setByteOffset(int Value) {
        this.mByteOffset = Value;
    }

    public int getByteOffset() {
        return this.mByteOffset;
    }

    public void setInUse(boolean Value) {
        this.mInUse = Value;
    }

    public boolean getInUse() {
        return this.mInUse;
    }

    public void addContent(String Value) {
        this.mContent.addContent(Value);
    }

    public void setContent(String Value) {
        this.mContent.setContent(Value);
    }

    public String getContent() {
        return this.mContent.getContent();
    }

    public void addDictionaryContent(String Value) {
        this.mDictionaryContent.addContent(Value);
    }

    public void setDictionaryContent(String Value) {
        this.mDictionaryContent.setContent(Value);
    }

    public String getDictionaryContent() {
        return this.mDictionaryContent.getContent();
    }

    public void addStreamContent(String Value) {
        this.mStreamContent.addContent(Value);
    }

    public void setStreamContent(String Value) {
        this.mStreamContent.setContent(Value);
    }

    public String getStreamContent() {
        return this.mStreamContent.getContent();
    }

    protected String render() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.mID.toPDFString());
        sb.append(" ");
        // j-a-s-d: this can be performed in inherited classes DictionaryObject and StreamObject
        if (this.mDictionaryContent.hasContent()) {
            this.mContent.setContent(this.mDictionaryContent.toPDFString());
            if (this.mStreamContent.hasContent()) {
                this.mContent.addContent(this.mStreamContent.toPDFString());
            }
        }
        sb.append(this.mContent.toPDFString());
        return sb.toString();
    }

    @Override
    public void clear() {
        this.mID = new IndirectIdentifier();
        this.mByteOffset = 0;
        this.mInUse = false;
        this.mContent = new EnclosedContent();
        this.mContent.setBeginKeyword("obj", false, true);
        this.mContent.setEndKeyword("endobj", false, true);
        this.mDictionaryContent = new Dictionary();
        this.mStreamContent = new Stream();
    }

    @Override
    public String toPDFString() {
        return render();
    }

}
