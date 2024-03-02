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
import controllers.STATICDATA;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class offerPriceReport {

    public PdfWriter pdfWriter;
    private Document document;
    File pdfFile;

    BaseFont ArialBase;

    {
        try {
            ArialBase = BaseFont.createFont(
                    "c:/windows/fonts/Arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException | IOException e) {
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

    public void openDocument(String pdfName, String location) {

        File folder = new File(location);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        try {
            pdfFile = new File(folder, pdfName);
            document = new Document();
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

            document.setMargins(36, 36, 160, 50);
            document.setMarginMirroring(false);

            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            pdfWriter.setPageEvent(event);

            document.open();

            //   addTopHeaderIMG();
            addTopHeader();
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openDocument", ex);
        }

    }

    //          PdfPCell cell7 = new PdfPCell(new Phrase("عـــــــــرض ســـــــــــــعــــر",ArialFontRed));
    public void addTopHeader() {

        // Table
        PdfPTable table = new PdfPTable(3); // 3 columns.

        table.setWidthPercentage(100);

        try {
            table.setWidths(new int[]{180, 0, 180});

            table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell cell7 = new PdfPCell(new Phrase("عـــــــــرض ســـــــــــــعــــر", ArialFontRed));
            cell7.setBorder(Rectangle.NO_BORDER);
            cell7.setPadding(5);
            cell7.setColspan(3);
            cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell7);

            //   table.setSpacingBefore(20F);
            table.setSpacingBefore(10F);
            document.add(table);

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "addTopHeader", ex);
        }

    }

    public void addCompanyInfo(String Date, String CustomerName, int invCode) {

        // Table
        PdfPTable table = new PdfPTable(2); // 2 columns.
        // table.setWidthPercentage(100);
        try {
            table.setWidths(new int[]{200, 120});

            table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

            PdfPCell cell0 = new PdfPCell(new Phrase("  تاريخ  الفاتورة:  ", ArialFont));
            PdfPCell cello = new PdfPCell(new Paragraph(Date, ArialFontSmall));
            cello.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell0.setPadding(5);
            cello.setPadding(5);
            cell0.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell0);
            table.addCell(cello);

            PdfPCell cell1 = new PdfPCell(new Phrase("  رقـــم الفـــــاتورة : ", ArialFont));
            PdfPCell cell2 = new PdfPCell(new Paragraph(invCode + "", ArialFontSmall));
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setPadding(5);
            cell2.setPadding(5);
            cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell1);
            table.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Paragraph("  عناية السيد الاستاذ :", ArialFont));
            PdfPCell cell4 = new PdfPCell(new Paragraph(CustomerName, ArialFontSmall));
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell3.setPadding(5);
            cell4.setPadding(5);
            cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell3);
            table.addCell(cell4);
            // table.setSpacingAfter(30f);
            table.setSpacingBefore(10F);
            document.add(table);

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "addCompanyInfo", ex);
        }

    }

    public void addTable(String[] header, ArrayList<String[]> items) {

        Paragraph paragraph = new Paragraph();
        try {
            // Table
            PdfPTable table = new PdfPTable(header.length); // # of columns.
            table.setWidthPercentage(100);

            table.setHeaderRows(1);

            try {
                table.setWidths(new int[]{50, 50, 50, 150, 50});
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

            PdfPCell pdfPCell;
            int indexC = 0;
            while (indexC < header.length) {
                pdfPCell = new PdfPCell(new Phrase(header[indexC++], ArialFont));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setBackgroundColor(BaseColor.GRAY);
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
            table.setSpacingBefore(30F);
            document.add(table);

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "addTable", ex);
        }

    }

    public void addTotalTable(double paid, double total, double remain) {

        try {
            // Table
            PdfPTable table = new PdfPTable(2); // 2 columns.
            table.setWidthPercentage(50);

            table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            table.setHorizontalAlignment(Element.ALIGN_RIGHT);

            PdfPCell cell0 = new PdfPCell(new Phrase("  إجــــمالي  الفاتورة:  ", ArialFont));
            PdfPCell cello = new PdfPCell(new Paragraph(total + "", ArialFontSmall));
            cello.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell0.setPadding(5);
            cello.setPadding(5);
            cell0.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cello.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell0);
            table.addCell(cello);

            PdfPCell cell1 = new PdfPCell(new Phrase("  المبلغ المدفوع : ", ArialFont));
            PdfPCell cell2 = new PdfPCell(new Paragraph(paid + "", ArialFontSmall));
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setPadding(5);
            cell2.setPadding(5);
            table.addCell(cell1);
            table.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Paragraph("  المبلغ المتبقي :", ArialFont));
            PdfPCell cell4 = new PdfPCell(new Paragraph(remain + "", ArialFontSmall));
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell3.setPadding(5);
            cell4.setPadding(5);
            cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell4.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell3);
            table.addCell(cell4);

            // table.setSpacingAfter(30f);
            table.setSpacingBefore(30F);
            document.add(table);

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "addTotalTable", ex);
        }

    }

}
