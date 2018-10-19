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

public class EnclosedContent extends Base {

    private String mBegin;
    private String mEnd;
    protected StringBuilder mContent;

    public EnclosedContent() {
        clear();
    }

    public void setBeginKeyword(String Value, boolean NewLineBefore, boolean NewLineAfter) {
        if (NewLineBefore) {
            this.mBegin = "\n" + Value;
        } else {
            this.mBegin = Value;
        }
        if (NewLineAfter) {
            this.mBegin += "\n";
        }
    }

    public void setEndKeyword(String Value, boolean NewLineBefore, boolean NewLineAfter) {
        if (NewLineBefore) {
            this.mEnd = "\n" + Value;
        } else {
            this.mEnd = Value;
        }
        if (NewLineAfter) {
            this.mEnd += "\n";
        }
    }

    public boolean hasContent() {
        return this.mContent.length() > 0;
    }

    public void setContent(String Value) {
        clear();
        this.mContent.append(Value);
    }

    public String getContent() {
        return this.mContent.toString();
    }

    public void addContent(String Value) {
        this.mContent.append(Value);
    }

    public void addNewLine() {
        this.mContent.append("\n");
    }

    public void addSpace() {
        this.mContent.append(" ");
    }

    @Override
    public void clear() {
        this.mContent = new StringBuilder();
    }

    @Override
    public String toPDFString() {
        return this.mBegin + this.mContent.toString() + this.mEnd;
    }

}
