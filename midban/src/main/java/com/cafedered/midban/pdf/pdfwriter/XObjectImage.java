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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class XObjectImage {

    public static final int BITSPERCOMPONENT_8 = 8;
    public static final String DEVICE_RGB = "/DeviceRGB";

    public static boolean INTERPOLATION = false;
    public static int BITSPERCOMPONENT = BITSPERCOMPONENT_8;
    public static String COLORSPACE = DEVICE_RGB;

    public static int COMPRESSION_LEVEL = Deflater.NO_COMPRESSION;
    public static String ENCODING = "ISO-8859-1";

    private static int mImageCount = 0;

    private final PDFDocument mDocument;
    private IndirectObject mIndirectObject;
    private int mDataSize = 0;
    private int mWidth = -1;
    private int mHeight = -1;
    private String mName = "";
    private String mId = "";
    private String mProcessedImage = "";

    public XObjectImage(PDFDocument document, Bitmap bitmap) {
        this.mDocument = document;
        this.mProcessedImage = processImage(configureBitmap(bitmap));
        this.mId = Indentifiers.generateId(this.mProcessedImage);
        this.mName = "/img" + (++mImageCount);
    }

    public void appendToDocument() {
        this.mIndirectObject = this.mDocument.newIndirectObject();
        this.mDocument.includeIndirectObject(this.mIndirectObject);
        this.mIndirectObject.addDictionaryContent(
                " /Type /XObject\n" +
                        " /Subtype /Image\n" +
                        " /Filter [/ASCII85Decode /FlateDecode]\n" +
                        " /Width " + this.mWidth + "\n" +
                        " /Height " + this.mHeight + "\n" +
                        " /BitsPerComponent " + Integer.toString(BITSPERCOMPONENT) + "\n" +
                        " /Interpolate " + Boolean.toString(INTERPOLATION) + "\n" +
                        " /ColorSpace " + DEVICE_RGB + "\n" +
                        " /Length " + this.mProcessedImage.length() + "\n"
                );
        this.mIndirectObject.addStreamContent(this.mProcessedImage);
    }

    private Bitmap configureBitmap(Bitmap bitmap) {
        final Bitmap img = bitmap.copy(Config.ARGB_8888, false);
        if (img != null) {
            this.mWidth = img.getWidth();
            this.mHeight = img.getHeight();
            this.mDataSize = this.mWidth * this.mHeight * 3;
        }
        return img;
    }

    private byte[] getBitmapData(Bitmap bitmap) {
        byte[] data = null;
        if (bitmap != null) {
            data = new byte[this.mDataSize];
            int intColor;
            int offset = 0;
            for (int y = 0; y < this.mHeight; y++) {
                for (int x = 0; x < this.mWidth; x++) {
                    intColor = bitmap.getPixel(x, y);
                    data[offset++] = (byte) ((intColor>>16) & 0xFF);
                    data[offset++] = (byte) ((intColor>>8) & 0xFF);
                    data[offset++] = (byte) ((intColor>>0) & 0xFF);
                }
            }
        }
        return data;
    }

    private boolean deflateImageData(ByteArrayOutputStream baos, byte[] data) {
        if (data != null) {
            final Deflater deflater = new Deflater(COMPRESSION_LEVEL);
            final DeflaterOutputStream dos = new DeflaterOutputStream(baos, deflater);
            try {
                dos.write(data);
                dos.close();
                deflater.end();
                return true;
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private String encodeImageData(ByteArrayOutputStream baos) {
        final ByteArrayOutputStream sob = new ByteArrayOutputStream();
        final ASCII85Encoder enc85 = new ASCII85Encoder(sob);
        try {
            int i = 0;
            for (final byte b : baos.toByteArray()) {
                enc85.write(b);
                if (i++ == 255) {
                    sob.write('\n');
                    i = 0;
                }
            }
            return sob.toString(ENCODING);
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            try {
                enc85.close();
            } catch (final IOException e) {
            }
        }
        return "";
    }

    private String processImage(Bitmap bitmap) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (deflateImageData(baos, getBitmapData(bitmap))) {
            return encodeImageData(baos);
        }
        return null;
    }

    public String asXObjectReference() {
        return this.mName + " " + this.mIndirectObject.getIndirectReference();
    }

    public String getName() {
        return this.mName;
    }

    public String getId() {
        return this.mId;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }
}
