package PDF;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import controllers.STATICDATA;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.text.DecimalFormat;


public class invoiceReport {

    public PdfWriter pdfWriter;
    private Document document;
    File pdfFile;

    long millis = System.currentTimeMillis();
    java.sql.Date date = new java.sql.Date(millis);

    DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss aa");
    Date resultdate = new Date(millis);
    String dateFormat = sdf.format(resultdate);

    BaseFont ArialBase;

    {
        try {
            ArialBase = BaseFont.createFont(
                    "c:/windows/fonts/Arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "invoiceReport", ex);
        }
    }

    Font font = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.RED);

    Font ArialFont = new Font(ArialBase, 14);
    Font ArialFontSmall = new Font(ArialBase, 10);
    Font ArialFontMine = new Font(ArialBase, 8);
    Font ArialFontSmallRed = new Font(ArialBase, 10, 0, BaseColor.RED);
    Font ArialFontRed = new Font(ArialBase, 20, 0, BaseColor.DARK_GRAY);

    public void closeDocument() {
        document.close();
    }

    public void openDocument(String pdfName, String location, String title) {

        try {
            File folder = new File(location);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            pdfFile = new File(folder, pdfName);
            document = new Document(PageSize.A5);
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

            document.setMargins(36, 36, 110, 36);
            document.setMarginMirroring(false);

            HeaderFooterPageEventA5 event = new HeaderFooterPageEventA5();
            pdfWriter.setPageEvent(event);

            document.open();

            //  addTopHeaderIMG();
            addTopHeader(title);
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openDocument", ex);
        }

    }

    public void addTopHeader(String title) {

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

        PdfPCell cell4 = new PdfPCell(new Phrase("تاريخ اليوم: " + dateFormat, ArialFontMine));
        cell4.setBorder(Rectangle.NO_BORDER);
        cell4.setPadding(3);
        cell4.setColspan(3);
        table.addCell(cell4);

        PdfPCell cell7 = new PdfPCell(new Phrase(title, ArialFontRed));
        cell7.setBorder(Rectangle.NO_BORDER);
        cell7.setPadding(3);
        cell7.setColspan(3);
        cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell7);

        try {

            //   table.setSpacingBefore(20F);
            table.setSpacingBefore(5F);
            document.add(table);

        } catch (DocumentException ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "addTopHeader", ex);
        }

    }

    public void addTopHeaderIMG() {

        try {
            PdfPTable tableTop = new PdfPTable(1); // 3 columns.

            tableTop.setWidthPercentage(100);

            tableTop.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            tableTop.setHorizontalAlignment(Element.ALIGN_CENTER);

            Image image = Image.getInstance("c:\\header.png");
            image.setAlignment(Element.ALIGN_CENTER);
            // image.scaleAbsolute(350f, 840f);
            document.getPageSize();
            image.scaleToFit(document.getPageSize().getWidth(), 78);

            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin()) / image.getWidth()) * 100;

            image.scalePercent(scaler);

            PdfPCell cell1 = new PdfPCell();
            cell1.addElement(image);
            cell1.setBorder(Rectangle.NO_BORDER);
            tableTop.addCell(cell1);

         //   table.setSpacingBefore(20F);
            //   table.setSpacingBefore(30F);
            document.add(tableTop);

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "addTopHeaderIMG", ex);
        }

    }

    public void addCompanyInfo(String Date, String CustomerName, String invCode, String UserName) {

        // Table
        PdfPTable table = new PdfPTable(2); // 2 columns.
        // table.setWidthPercentage(100);
        try {
            table.setWidths(new int[]{200, 120});
        
        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

        PdfPCell cell0 = new PdfPCell(new Phrase("تاريخ  الفاتورة", ArialFontSmall));
        PdfPCell cello = new PdfPCell(new Paragraph(Date, ArialFontSmall));
        cello.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell0.setPadding(5);
        cello.setPadding(5);
        cell0.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell0);
        table.addCell(cello);

        PdfPCell cell1 = new PdfPCell(new Phrase("رقـــم الفـــــاتورة", ArialFontSmall));
        PdfPCell cell2 = new PdfPCell(new Paragraph(invCode + "", ArialFontSmall));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell1.setPadding(5);
        cell2.setPadding(5);
        cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell1);
        table.addCell(cell2);

        PdfPCell cell3 = new PdfPCell(new Paragraph("عناية السيد الاستاذ", ArialFontSmall));
        PdfPCell cell4 = new PdfPCell(new Paragraph(CustomerName, ArialFontSmall));
        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell3.setPadding(5);
        cell4.setPadding(5);
        cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell3);
        table.addCell(cell4);

        PdfPCell cell5 = new PdfPCell(new Paragraph("محرر الفـــــاتورة", ArialFontSmall));
        PdfPCell cell6 = new PdfPCell(new Paragraph(UserName, ArialFontSmall));
        cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell5.setPadding(5);
        cell6.setPadding(5);
        cell5.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell5);
        table.addCell(cell6);


            // table.setSpacingAfter(30f);
            table.setSpacingBefore(5F);
            document.add(table);

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "addCompanyInfo", ex);
        }

    }

    public void addTable(String[] header, ArrayList<String[]> items) {

        Paragraph paragraph = new Paragraph();

        // Table
        PdfPTable table = new PdfPTable(header.length); // # of columns.
        table.setWidthPercentage(100);

        table.setHeaderRows(1);

        try {
            table.setWidths(new int[]{50, 50, 50, 150, 50});
       
        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

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

            //  table.setSpacingAfter(70f);
            table.setSpacingBefore(10F);
            document.add(table);

         } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "addTable", ex);
        }

    }

    public void addTotalTable(double paid, double total, double remain, String NewTotal) {

        // Table
        PdfPTable table = new PdfPTable(2); // 2 columns.
        table.setWidthPercentage(50);

        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setKeepTogether(true);

        PdfPCell cell0 = new PdfPCell(new Phrase("إجــــمالي  الفاتورة", ArialFontSmall));
        PdfPCell cello = new PdfPCell(new Paragraph(new DecimalFormat("##.##").format(total) + "", ArialFontSmall));
        cello.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell0.setPadding(5);
        cello.setPadding(5);
        table.addCell(cell0);
        table.addCell(cello);

        PdfPCell cell5 = new PdfPCell(new Phrase("القيمة المضافة14%", ArialFontSmall));
        PdfPCell cell6 = new PdfPCell(new Paragraph(NewTotal + "", ArialFontSmallRed));
        cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell5.setPadding(5);
        cell6.setPadding(5);
        cell5.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell6.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell5);
        table.addCell(cell6);

        PdfPCell cell1 = new PdfPCell(new Phrase("المبلغ المدفوع", ArialFontSmall));
        PdfPCell cell2 = new PdfPCell(new Paragraph(paid + "", ArialFontSmall));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell1.setPadding(5);
        cell2.setPadding(5);
        table.addCell(cell1);
        table.addCell(cell2);

        PdfPCell cell3 = new PdfPCell(new Paragraph("المبلغ المتبقي", ArialFontSmall));
        PdfPCell cell4 = new PdfPCell(new Paragraph(remain + "", ArialFontSmallRed));
        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell3.setPadding(5);
        cell4.setPadding(5);
        cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell4.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell3);
        table.addCell(cell4);

        try {

            // table.setSpacingAfter(30f);
            table.setSpacingBefore(10F);
            document.add(table);

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "addTotalTable", ex);
        }

    }

    public void payTable(String[] header, ArrayList<String[]> items, String tableTitle) {

        // Table
        PdfPTable table = new PdfPTable(header.length); // # of columns.
        table.setWidthPercentage(100);

        table.setHeaderRows(1);

        try {
            table.setWidths(new int[]{100, 70, 70, 100});

            table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

            PdfPCell cell3 = new PdfPCell(new Paragraph(tableTitle, ArialFont));
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell3.setPadding(5);
            cell3.setBackgroundColor(BaseColor.GRAY);
            cell3.setColspan(7);
            //    table.addCell(cell3);

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

            table.setSpacingBefore(30F);
            document.add(table);

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "payTable", ex);
        }

    }

}
