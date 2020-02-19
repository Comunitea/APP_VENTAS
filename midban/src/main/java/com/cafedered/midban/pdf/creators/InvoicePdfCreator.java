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
package com.cafedered.midban.pdf.creators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;

import android.graphics.BitmapFactory;
import android.os.Environment;

import com.cafedered.midban.R;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Invoice;
import com.cafedered.midban.entities.InvoiceLine;
import com.cafedered.midban.pdf.pdfwriter.PDFWriter;
import com.cafedered.midban.pdf.pdfwriter.StandardFonts;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;

public class InvoicePdfCreator {
    private static final int MARGIN_LEFT = 30;
    private static final int TITLE_TEXT_SIZE = 28;
    private static final int TOTAL_TEXT_SIZE = 16;
    private static final int TEXT_SIZE = 14;

    public static File generateFile(Invoice invoice) {
        return outputToFile("invoice_"
                + invoice.getNumber().replace(" ", "_").replace("/", "_")
                + ".pdf", composeInvoicePdf(invoice), "ISO-8859-1");
    }

    private static File outputToFile(String fileName, String pdfContent,
            String encoding) {
        final String filePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/" + fileName;
        final File file = new File(filePath);
        try {
            file.createNewFile();
            try {
                final FileOutputStream os = new FileOutputStream(file);
                os.write(pdfContent.getBytes(encoding));
                os.close();
            } catch (final FileNotFoundException e) {
                if (LoggerUtil.isDebugEnabled())
                    e.printStackTrace();
            }
        } catch (final IOException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        return file;
    }

    private static String composeInvoicePdf(Invoice invoice) {
        final PDFWriter writer = new PDFWriter();
        addData(writer, invoice);
        return writer.asString();
    }

    private static void addData(PDFWriter writer, Invoice invoice) {
        writer.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA_BOLD,
                StandardFonts.WIN_ANSI_ENCODING);
        writer.addImage(MARGIN_LEFT, 780, BitmapFactory.decodeResource(
                MidbanApplication
                        .getContext().getResources(), R.drawable.valquin));
//                .getContext().getResources(), R.drawable.logo_midban));
        writer.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA_BOLD,
                StandardFonts.WIN_ANSI_ENCODING);
        writer.addText(600, 780, TEXT_SIZE, "Foodservice business");
        writer.addText(
                70,
                740,
                TITLE_TEXT_SIZE,
                MidbanApplication.getContext().getResources()
                        .getString(R.string.activity_invoice_detail_invoice)
                        + ": " + invoice.getNumber());
        writer.addText(MARGIN_LEFT, 740, TEXT_SIZE, invoice.getPartner()
                .getName());
        writer.addText(MARGIN_LEFT, 720, TEXT_SIZE, invoice.getPartner()
                .getCompleteAddress());
        try {
            String dateInvoice = "";
            if (invoice.getDateInvoice() != null)
                dateInvoice = DateUtil.toFormattedString(DateUtil.parseDate(
                        invoice.getDateInvoice(), "yyyy-MM-dd"), "dd.MM.yyyy");
            writer.addText(
                    400,
                    740,
                    TEXT_SIZE,
                    MidbanApplication.getContext().getResources()
                            .getString(R.string.activity_invoice_detail_date)
                            + " " + dateInvoice);
        } catch (ParseException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        writer.addText(
                MARGIN_LEFT,
                690,
                TEXT_SIZE,
                MidbanApplication.getContext().getResources()
                        .getString(R.string.activity_invoice_detail_quantity));
        writer.addText(
                100,
                690,
                TEXT_SIZE,
                MidbanApplication.getContext().getResources()
                        .getString(R.string.activity_invoice_detail_product));
        writer.addText(
                470,
                690,
                TEXT_SIZE,
                MidbanApplication.getContext().getResources()
                        .getString(R.string.activity_invoice_detail_unit_price));
        int yPosition = 670;
        for (InvoiceLine line : invoice.getLines()) {
            writer.addText(MARGIN_LEFT, yPosition, TEXT_SIZE,
                    "" + line.getQuantity());
            writer.addText(100, yPosition, TEXT_SIZE, line.getName());
            writer.addText(470, yPosition, TEXT_SIZE,
                    ""
                            + line.getPriceUnit()
                            + MidbanApplication.getContext().getResources()
                                    .getString(R.string.pdf_currency_symbol));
            yPosition = yPosition - 20;
            if (yPosition < 30) {
                writer.newPage();
                yPosition = 780;
            }
        }
        if (yPosition < 780)
            yPosition = yPosition - 20;
        if (yPosition < 50) {
            yPosition = 780;
            writer.newPage();
        }
        writer.addText(
                300,
                yPosition,
                TEXT_SIZE,
                MidbanApplication
                        .getContext()
                        .getResources()
                        .getString(
                                R.string.activity_invoice_detail_unit_amount_without_taxes)
                        + " "
                        + invoice.getAmountUntaxed()
                        + MidbanApplication.getContext().getResources()
                                .getString(R.string.pdf_currency_symbol));
        yPosition = yPosition - 20;
        writer.addText(
                300,
                yPosition,
                TEXT_SIZE,
                MidbanApplication
                        .getContext()
                        .getResources()
                        .getString(
                                R.string.activity_invoice_detail_unit_amount_taxes)
                        + " "
                        + new BigDecimal(invoice.getAmountTax().doubleValue())
                                .setScale(2, RoundingMode.HALF_UP)
                        + MidbanApplication.getContext().getResources()
                                .getString(R.string.pdf_currency_symbol));
        yPosition = yPosition - 30;
        writer.addText(
                300,
                yPosition,
                TOTAL_TEXT_SIZE,
                MidbanApplication
                        .getContext()
                        .getResources()
                        .getString(
                                R.string.activity_invoice_detail_unit_amount_total)
                        + " "
                        + invoice.getAmountTotal()
                        + MidbanApplication.getContext().getResources()
                                .getString(R.string.pdf_currency_symbol));
    }

}
