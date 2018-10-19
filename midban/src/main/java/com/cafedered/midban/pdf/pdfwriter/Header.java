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

public class Header extends Base {

    private String mVersion;
    private String mRenderedHeader;

    public Header() {
        clear();
    }

    public void setVersion(int Major, int Minor) {
        this.mVersion = Integer.toString(Major) + "." + Integer.toString(Minor);
        render();
    }

    public int getPDFStringSize() {
        return this.mRenderedHeader.length();
    }

    private void render() {
        this.mRenderedHeader = "%PDF-" + this.mVersion + "\n%����\n";
    }

    @Override
    public String toPDFString() {
        return this.mRenderedHeader;
    }

    @Override
    public void clear() {
        setVersion(1, 4);
    }

}
