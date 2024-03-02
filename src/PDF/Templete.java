package PDF;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
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
import java.util.ArrayList;

public class Templete {

    long millis = System.currentTimeMillis();
    java.sql.Date date = new java.sql.Date(millis);

    public PdfPTable tableTop = new PdfPTable(1); // 3 columns.

    public PdfWriter pdfWriter;
    private Document document;
    File pdfFile;

    BaseFont ArialBase;

    {
        try {
            ArialBase = BaseFont.createFont(
                    "c:/windows/fonts/Arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "Templete", ex);
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
        try {
            File folder = new File(location);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            pdfFile = new File(folder, pdfName);

            document = new Document();

            document.setMargins(36, 36, 160, 50);
            document.setMarginMirroring(false);

            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            pdfWriter.setPageEvent(event);

            document.open();

            // addTopHeaderIMG();
            addTopHeader();
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openDocument", ex);
        }

    }

    public void addTopHeader() {

        // Table
        PdfPTable table = new PdfPTable(3); // 3 columns.

        table.setWidthPercentage(100);

        try {
            table.setWidths(new int[]{180, 0, 180});

            table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell cell7 = new PdfPCell(new Phrase(" تقرير عملاء شركة النور ", ArialFont));
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

    public void addTopHeaderIMG() {

        // Table
        //  PdfPTable tableTop = new PdfPTable(1); // 3 columns.
        try {

            tableTop.setWidthPercentage(100);

            tableTop.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            tableTop.setHorizontalAlignment(Element.ALIGN_CENTER);

            Image image = Image.getInstance("C:\\test.png");
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

    public void addTable(String[] header, ArrayList<String[]> items) {

        Paragraph paragraph = new Paragraph();
      //  paragraph.setFont(ArialFontSmall);

        // Table
        PdfPTable table = new PdfPTable(header.length); // # of columns.
        table.setWidthPercentage(100);

        table.setHeaderRows(1);

        try {
            table.setWidths(new int[]{60, 70, 50, 80, 15});

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

            table.setSpacingBefore(30F);
            document.add(table);

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "addTable", ex);
        }

    }

}
