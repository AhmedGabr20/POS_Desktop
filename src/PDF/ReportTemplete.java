/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PDF;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author Ahmed Gabr
 */
public class ReportTemplete {

    long millis = System.currentTimeMillis();
    java.sql.Date date = new java.sql.Date(millis);

    DecimalFormat decimal = new DecimalFormat("#.##");

    DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss aa");
    Date resultdate = new Date(millis);
    String dateFormat = sdf.format(resultdate);

    public PdfPTable tableTop = new PdfPTable(1); // 3 columns.

    public PdfWriter pdfWriter;
    private Document document;
    File pdfFile;

    BaseFont ArialBase;

    {
        try {
            ArialBase = BaseFont.createFont(
                    "c:/windows/fonts/Arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Font font = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.RED);

    Font ArialFont = new Font(ArialBase, 14);
    Font ArialFontSmall = new Font(ArialBase, 10);
    Font ArialFontRed = new Font(ArialBase, 20, 0, BaseColor.DARK_GRAY);

    public void closeDocument() {
        document.close();
    }

    public void openDocument(String docName, String fileLocation) throws DocumentException, FileNotFoundException, BadElementException, IOException {

        Random rand = new Random();
        int n = rand.nextInt(50);
        String PDFNAME = docName + ".pdf";

        File folder = new File(fileLocation);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        pdfFile = new File(folder, PDFNAME);

        document = new Document();
        pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

        document.setMargins(36, 36, 160, 50);
        document.setMarginMirroring(false);

        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
        pdfWriter.setPageEvent(event);

        document.open();

    }

    public void addTopHeader(String title, String dateFrom, String dateTo) {

        // Table
        PdfPTable table = new PdfPTable(3); // 3 columns.

        table.setWidthPercentage(100);

        try {
            table.setWidths(new int[]{180, 0, 180});

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell7 = new PdfPCell(new Phrase(title, ArialFontRed));
        cell7.setBorder(Rectangle.NO_BORDER);
        cell7.setPadding(5);
        cell7.setColspan(3);
        cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell7);

        PdfPCell cell6 = new PdfPCell(new Phrase(dateFrom + dateTo, ArialFontSmall));
        cell6.setBorder(Rectangle.NO_BORDER);
        cell6.setPadding(5);
        cell6.setColspan(3);
        cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell6);

        try {

            //   table.setSpacingBefore(20F);
            table.setSpacingBefore(10F);
            document.add(table);

        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    public void addInfo(String revenue, String expenses) {

        double profit = Double.parseDouble(revenue) - Double.parseDouble(expenses);
        String profit_txt = decimal.format(profit);
        String label = "أرباح";
        
        if(profit_txt.contains("-")){
            label = "خسارة";
        }
        // Table
        PdfPTable table = new PdfPTable(2); // 2 columns.
        // table.setWidthPercentage(100);
        try {
            table.setWidths(new int[]{200, 120});
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

        PdfPCell cell0 = new PdfPCell(new Phrase("إيرادات", ArialFont));
        PdfPCell cello = new PdfPCell(new Paragraph(revenue, ArialFontSmall));
        cello.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell0.setPadding(5);
        cello.setPadding(5);
        cell0.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell0);
        table.addCell(cello);

        PdfPCell cell1 = new PdfPCell(new Phrase("مصروفات", ArialFont));
        PdfPCell cell2 = new PdfPCell(new Paragraph(expenses, ArialFontSmall));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell1.setPadding(5);
        cell2.setPadding(5);
        cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell1);
        table.addCell(cell2);

        PdfPCell cell3 = new PdfPCell(new Paragraph(label, ArialFont));
        PdfPCell cell4 = new PdfPCell(new Paragraph(profit_txt, ArialFontSmall));
        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell3.setPadding(5);
        cell4.setPadding(5);
        cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell3);
        table.addCell(cell4);

        try {

            // table.setSpacingAfter(30f);
            table.setSpacingBefore(10F);
            document.add(table);

        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    public void revenueTable(String[] header, ArrayList<String[]> items) {

        // Table
        PdfPTable table = new PdfPTable(header.length); // # of columns.
        table.setWidthPercentage(100);

        table.setHeaderRows(1);

        try {
            table.setWidths(new int[]{30, 30, 50, 50, 50});
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

        PdfPCell cell3 = new PdfPCell(new Paragraph("جــــــــدول الإيـــــــرادات", ArialFont));
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell3.setPadding(5);
        cell3.setBackgroundColor(BaseColor.GRAY);
        cell3.setColspan(6);
        table.addCell(cell3);

        PdfPCell pdfPCell;
        int indexC = 0;
        while (indexC < header.length) {
            pdfPCell = new PdfPCell(new Phrase(header[indexC++], ArialFont));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pdfPCell.setFixedHeight(30);
            table.addCell(pdfPCell);
        }

        for (int indexR = 0; indexR < items.size(); indexR++) {
            String[] row = items.get(indexR);
            for (indexC = 0; indexC < header.length; indexC++) {

                Phrase phrase = new Phrase(row[indexC], ArialFontSmall);

                Paragraph paragraph1 = new Paragraph(row[indexC], ArialFontSmall);

                paragraph1.setSpacingBefore(5);

                pdfPCell = new PdfPCell(paragraph1);
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setPadding(5);
                // pdfPCell.setFixedHeight(20);
                table.addCell(pdfPCell);
            }
        }

        try {
            table.setSpacingBefore(30F);
            document.add(table);

        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    public void expensesTable(String[] header, ArrayList<String[]> items, String tableTitle) {

        // Table
        PdfPTable table = new PdfPTable(header.length); // # of columns.
        table.setWidthPercentage(100);

        table.setHeaderRows(1);

        try {
            table.setWidths(new int[]{30, 30, 40, 50, 50, 50});
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

        PdfPCell cell3 = new PdfPCell(new Paragraph(tableTitle, ArialFont));
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell3.setPadding(5);
        cell3.setBackgroundColor(BaseColor.GRAY);
        cell3.setColspan(7);
        table.addCell(cell3);

        PdfPCell pdfPCell;
        int indexC = 0;
        while (indexC < header.length) {
            pdfPCell = new PdfPCell(new Phrase(header[indexC++], ArialFont));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pdfPCell.setFixedHeight(30);
            table.addCell(pdfPCell);
        }

        for (int indexR = 0; indexR < items.size(); indexR++) {
            String[] row = items.get(indexR);
            for (indexC = 0; indexC < header.length; indexC++) {

                Phrase phrase = new Phrase(row[indexC], ArialFontSmall);

                Paragraph paragraph1 = new Paragraph(row[indexC], ArialFontSmall);

                paragraph1.setSpacingBefore(5);

                pdfPCell = new PdfPCell(paragraph1);
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setPadding(5);
                // pdfPCell.setFixedHeight(20);
                table.addCell(pdfPCell);
            }
        }

        try {
            table.setSpacingBefore(30F);
            document.add(table);

        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

}
