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

public class Pages {

    private final PDFDocument mDocument;
    private final ArrayList<Page> mPageList;
    private final IndirectObject mIndirectObject;
    private final Array mMediaBox;
    private final Array mKids;

    public Pages(PDFDocument document, int pageWidth, int pageHeight) {
        this.mDocument = document;
        this.mIndirectObject = this.mDocument.newIndirectObject();
        this.mPageList = new ArrayList<Page>();
        this.mMediaBox = new Array();
        final String content[] = {"0", "0", Integer.toString(pageWidth), Integer.toString(pageHeight)};
        this.mMediaBox.addItemsFromStringArray(content);
        this.mKids = new Array();
    }

    public IndirectObject getIndirectObject() {
        return this.mIndirectObject;
    }

    public Page newPage() {
        final Page lPage = new Page(this.mDocument);
        this.mPageList.add(lPage);
        this.mKids.addItem(lPage.getIndirectObject().getIndirectReference());
        return lPage;
    }

    public Page getPageAt(int position) {
        return this.mPageList.get(position);
    }

    public int getCount() {
        return this.mPageList.size();
    }

    public void render() {
        this.mIndirectObject.setDictionaryContent(
                "  /Type /Pages\n" +
                        "  /MediaBox " + this.mMediaBox.toPDFString() + "\n" +
                        "  /Count " + Integer.toString(this.mPageList.size()) + "\n" +
                        "  /Kids " + this.mKids.toPDFString() + "\n"
                );
        for (final Page lPage: this.mPageList) {
            lPage.render(this.mIndirectObject.getIndirectReference());
        }
    }
}
