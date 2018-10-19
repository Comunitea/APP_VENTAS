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

public class Page {

    private final PDFDocument mDocument;
    private final IndirectObject mIndirectObject;
    private final ArrayList<IndirectObject> mPageFonts;
    private final ArrayList<XObjectImage> mXObjects;
    private final IndirectObject mPageContents;

    public Page(PDFDocument document) {
        this.mDocument = document;
        this.mIndirectObject = this.mDocument.newIndirectObject();
        this.mPageFonts = new ArrayList<IndirectObject>();
        this.mXObjects = new ArrayList<XObjectImage>();
        setFont(StandardFonts.SUBTYPE, StandardFonts.TIMES_ROMAN, StandardFonts.WIN_ANSI_ENCODING);
        this.mPageContents = this.mDocument.newIndirectObject();
        this.mDocument.includeIndirectObject(this.mPageContents);
    }

    public IndirectObject getIndirectObject() {
        return this.mIndirectObject;
    }

    private String getFontReferences() {
        String result = "";
        if (!this.mPageFonts.isEmpty()) {
            result = "    /Font <<\n";
            int x = 0;
            for (final IndirectObject lFont : this.mPageFonts) {
                result += "      /F" + Integer.toString(++x) + " " + lFont.getIndirectReference() + "\n";
            }
            result += "    >>\n";
        }
        return result;
    }

    private String getXObjectReferences() {
        String result = "";
        if (!this.mXObjects.isEmpty()) {
            result = "    /XObject <<\n";
            for (final XObjectImage xObj : this.mXObjects) {
                result += "      " + xObj.asXObjectReference() + "\n";
            }
            result += "    >>\n";
        }
        return result;
    }

    public void render(String pagesIndirectReference) {
        this.mIndirectObject.setDictionaryContent(
                "  /Type /Page\n  /Parent " + pagesIndirectReference + "\n" +
                        "  /Resources <<\n" + getFontReferences() + getXObjectReferences() + "  >>\n" +
                        "  /Contents " + this.mPageContents.getIndirectReference() + "\n"
                );
    }

    public void setFont(String subType, String baseFont) {
        final IndirectObject lFont = this.mDocument.newIndirectObject();
        this.mDocument.includeIndirectObject(lFont);
        lFont.setDictionaryContent("  /Type /Font\n  /Subtype /" + subType + "\n  /BaseFont /" + baseFont + "\n");
        this.mPageFonts.add(lFont);
    }

    public void setFont(String subType, String baseFont, String encoding) {
        final IndirectObject lFont = this.mDocument.newIndirectObject();
        this.mDocument.includeIndirectObject(lFont);
        lFont.setDictionaryContent("  /Type /Font\n  /Subtype /" + subType + "\n  /BaseFont /" + baseFont + "\n  /Encoding /" + encoding + "\n");
        this.mPageFonts.add(lFont);
    }

    private void addContent(String content) {
        this.mPageContents.addStreamContent(content);
        final String streamContent = this.mPageContents.getStreamContent();
        this.mPageContents.setDictionaryContent("  /Length " + Integer.toString(streamContent.length()) + "\n");
        this.mPageContents.setStreamContent(streamContent);
    }

    public void addRawContent(String rawContent) {
        addContent(rawContent);
    }

    public void addText(int leftPosition, int topPositionFromBottom, int fontSize, String text) {
        addText(leftPosition, topPositionFromBottom, fontSize, text, Transformation.DEGREES_0_ROTATION);
    }

    public void addText(int leftPosition, int topPositionFromBottom, int fontSize, String text, String transformation) {
        addContent(
                "BT\n" +
                        transformation + " " + Integer.toString(leftPosition) + " " + Integer.toString(topPositionFromBottom) + " Tm\n" +
                        "/F" + Integer.toString(this.mPageFonts.size()) + " " + Integer.toString(fontSize) + " Tf\n" +
                        "(" + text + ") Tj\n" +
                        "ET\n"
                );
    }

    public void addTextAsHex(int leftPosition, int topPositionFromBottom, int fontSize, String hex) {
        addTextAsHex(leftPosition, topPositionFromBottom, fontSize, hex, Transformation.DEGREES_0_ROTATION);
    }

    public void addTextAsHex(int leftPosition, int topPositionFromBottom, int fontSize, String hex, String transformation) {
        addContent(
                "BT\n" +
                        transformation + " " + Integer.toString(leftPosition) + " " + Integer.toString(topPositionFromBottom) + " Tm\n" +
                        "/F" + Integer.toString(this.mPageFonts.size()) + " " + Integer.toString(fontSize) + " Tf\n" +
                        "<" + hex + "> Tj\n" +
                        "ET\n"
                );
    }

    public void addLine(int fromLeft, int fromBottom, int toLeft, int toBottom) {
        addContent(
                Integer.toString(fromLeft) + " " + Integer.toString(fromBottom) + " m\n" +
                        Integer.toString(toLeft) + " " + Integer.toString(toBottom) + " l\nS\n"
                );
    }

    public void addRectangle(int fromLeft, int fromBottom, int toLeft, int toBottom) {
        addContent(
                Integer.toString(fromLeft) + " " + Integer.toString(fromBottom) + " " +
                        Integer.toString(toLeft) + " " + Integer.toString(toBottom) + " re\nS\n"
                );
    }

    private String ensureXObjectImage(XObjectImage xObject) {
        for (final XObjectImage x : this.mXObjects) {
            if (x.getId().equals(xObject.getId())) {
                return x.getName();
            }
        }
        this.mXObjects.add(xObject);
        xObject.appendToDocument();
        return xObject.getName();
    }

    public void addImage(int fromLeft, int fromBottom, int width, int height, XObjectImage xImage, String transformation){
        final String name = ensureXObjectImage(xImage);
        final String translate = "1 0 0 1 " + fromLeft + " " + fromBottom;
        final String scale = "" + width + " 0 0 " + height + " 0 0";
        final String rotate = transformation + " 0 0";
        addContent(
                "q\n" +
                        translate + " cm\n" +
                        rotate + " cm\n" +
                        scale + " cm\n" +
                        name + " Do\n" +
                        "Q\n"
                );
    }
}
