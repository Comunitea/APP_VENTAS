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

import android.graphics.Bitmap;

public class PDFWriter {

    private PDFDocument mDocument;
    private IndirectObject mCatalog;
    private Pages mPages;
    private Page mCurrentPage;

    public PDFWriter() {
        newDocument(PaperSize.A4_WIDTH, PaperSize.A4_HEIGHT);
    }

    public PDFWriter(int pageWidth, int pageHeight) {
        newDocument(pageWidth, pageHeight);
    }

    private void newDocument(int pageWidth, int pageHeight) {
        this.mDocument = new PDFDocument();
        this.mCatalog = this.mDocument.newIndirectObject();
        this.mDocument.includeIndirectObject(this.mCatalog);
        this.mPages = new Pages(this.mDocument, pageWidth, pageHeight);
        this.mDocument.includeIndirectObject(this.mPages.getIndirectObject());
        renderCatalog();
        newPage();
    }

    private void renderCatalog() {
        this.mCatalog.setDictionaryContent("  /Type /Catalog\n  /Pages " + this.mPages.getIndirectObject().getIndirectReference() + "\n");
    }

    public void newPage() {
        this.mCurrentPage = this.mPages.newPage();
        this.mDocument.includeIndirectObject(this.mCurrentPage.getIndirectObject());
        this.mPages.render();
    }

    public void setCurrentPage(int pageNumber) {
        this.mCurrentPage = this.mPages.getPageAt(pageNumber);
    }

    public int getPageCount() {
        return this.mPages.getCount();
    }

    public void setFont(String subType, String baseFont) {
        this.mCurrentPage.setFont(subType, baseFont);
    }

    public void setFont(String subType, String baseFont, String encoding) {
        this.mCurrentPage.setFont(subType, baseFont, encoding);
    }

    public void addRawContent(String rawContent) {
        this.mCurrentPage.addRawContent(rawContent);
    }

    public void addText(int leftPosition, int topPositionFromBottom, int fontSize, String text) {
        addText(leftPosition, topPositionFromBottom, fontSize, text, Transformation.DEGREES_0_ROTATION);
    }

    public void addText(int leftPosition, int topPositionFromBottom, int fontSize, String text, String transformation) {
        this.mCurrentPage.addText(leftPosition, topPositionFromBottom, fontSize, text, transformation);
    }

    public void addTextAsHex(int leftPosition, int topPositionFromBottom, int fontSize, String hex) {
        addTextAsHex(leftPosition, topPositionFromBottom, fontSize, hex, Transformation.DEGREES_0_ROTATION);
    }

    public void addTextAsHex(int leftPosition, int topPositionFromBottom, int fontSize, String hex, String transformation) {
        this.mCurrentPage.addTextAsHex(leftPosition, topPositionFromBottom, fontSize, hex, transformation);
    }

    public void addLine(int fromLeft, int fromBottom, int toLeft, int toBottom) {
        this.mCurrentPage.addLine(fromLeft, fromBottom, toLeft, toBottom);
    }

    public void addRectangle(int fromLeft, int fromBottom, int toLeft, int toBottom) {
        this.mCurrentPage.addRectangle(fromLeft, fromBottom, toLeft, toBottom);
    }

    public void addImage(int fromLeft, int fromBottom, Bitmap bitmap) {
        addImage(fromLeft, fromBottom, bitmap, Transformation.DEGREES_0_ROTATION);
    }

    public void addImage(int fromLeft, int fromBottom, Bitmap bitmap, String transformation) {
        final XObjectImage xImage = new XObjectImage(this.mDocument, bitmap);
        this.mCurrentPage.addImage(fromLeft, fromBottom, xImage.getWidth(), xImage.getHeight(), xImage, transformation);
    }

    public void addImage(int fromLeft, int fromBottom, int toLeft, int toBottom, Bitmap bitmap) {
        addImage(fromLeft, fromBottom, toLeft, toBottom, bitmap, Transformation.DEGREES_0_ROTATION);
    }

    public void addImage(int fromLeft, int fromBottom, int toLeft, int toBottom, Bitmap bitmap, String transformation) {
        this.mCurrentPage.addImage(fromLeft, fromBottom, toLeft, toBottom, new XObjectImage(this.mDocument, bitmap), transformation);
    }

    public void addImageKeepRatio(int fromLeft, int fromBottom, int width, int height, Bitmap bitmap) {
        addImageKeepRatio(fromLeft, fromBottom, width, height, bitmap, Transformation.DEGREES_0_ROTATION);
    }

    public void addImageKeepRatio(int fromLeft, int fromBottom, int width, int height, Bitmap bitmap, String transformation) {
        final XObjectImage xImage = new XObjectImage(this.mDocument, bitmap);
        final float imgRatio = (float) xImage.getWidth() / (float) xImage.getHeight();
        final float boxRatio = (float) width / (float) height;
        float ratio;
        if (imgRatio < boxRatio) {
            ratio = (float) width / (float) xImage.getWidth();
        } else {
            ratio = (float) height / (float) xImage.getHeight();
        }
        width = (int) (xImage.getWidth() * ratio);
        height = (int) (xImage.getHeight() * ratio);
        this.mCurrentPage.addImage(fromLeft, fromBottom, width, height, xImage, transformation);
    }

    public String asString() {
        this.mPages.render();
        return this.mDocument.toPDFString();
    }
}
