/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PDF;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import controllers.STATICDATA;

/**
 *
 * @author Ahmed Gabr
 */
public class HeaderFooterPageEvent extends PdfPageEventHelper {

    private PdfTemplate t;
    private Image total;

    public void onOpenDocument(PdfWriter writer, Document document) {
        t = writer.getDirectContent().createTemplate(30, 16);
        try {
            total = Image.getInstance(t);
            total.setRole(PdfName.ARTIFACT);
        } catch (DocumentException ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "onOpenDocument", ex);
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        try {
            addHeader(writer);
            addFooter(writer);
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "onEndPage", ex);
        }
    }

    private void addHeader(PdfWriter writer) {
        PdfPTable header = new PdfPTable(1);
        try {

            PdfContentByte cb = writer.getDirectContent();

            Rectangle frame2 = new Rectangle(577, 825, 18, 15); // you can resize rectangle
            frame2.enableBorderSide(1);
            frame2.enableBorderSide(2);
            frame2.enableBorderSide(4);
            frame2.enableBorderSide(8);
            frame2.setBorderColor(BaseColor.BLACK);
            frame2.setBorderWidth(1);
            frame2.setBorder(Rectangle.BOX);
            frame2.setBorderWidth(2);

            cb.rectangle(frame2);

            // set defaults
//            header.setWidths(new int[]{400});
            header.setWidthPercentage(120);
            header.setTotalWidth(527);
            header.setLockedWidth(true);
            header.getDefaultCell().setFixedHeight(100);
            header.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);

            Image image = Image.getInstance("C:\\test.png");
            image.setAlignment(Element.ALIGN_CENTER);

            PdfPCell cell1 = new PdfPCell();
            cell1.addElement(image);
            header.addCell(cell1);

            // write content
            header.writeSelectedRows(0, -1, 35, 820, writer.getDirectContent());
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "addHeader", ex);
        }
    }

    private void addFooter(PdfWriter writer) {
        PdfPTable footer = new PdfPTable(3);
        try {
            // set defaults
            footer.setWidths(new int[]{24, 2, 1});
            footer.setTotalWidth(527);
            footer.setLockedWidth(true);
            footer.getDefaultCell().setFixedHeight(40);
            footer.getDefaultCell().setBorder(Rectangle.TOP);
            footer.getDefaultCell().setBorderColor(BaseColor.BLACK);

            // add copyright
            footer.addCell(new Phrase("\u00A9 Ahmed Gabr - 01013884717", new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD)));

            // add current page count
            footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            footer.addCell(new Phrase(String.format("Page %d of", writer.getPageNumber()), new Font(Font.FontFamily.HELVETICA, 8)));

            // add placeholder for total page count
            PdfPCell totalPageCount = new PdfPCell(total);
            totalPageCount.setBorder(Rectangle.TOP);
            totalPageCount.setBorderColor(BaseColor.BLACK);
            footer.addCell(totalPageCount);

            // write page
            PdfContentByte canvas = writer.getDirectContent();
            canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
            footer.writeSelectedRows(0, -1, 34, 35, canvas);
            canvas.endMarkedContentSequence();
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "addFooter", ex);
        }
    }

    public void onCloseDocument(PdfWriter writer, Document document) {
        int totalLength = String.valueOf(writer.getPageNumber()).length();
        int totalWidth = totalLength * 5;
        ColumnText.showTextAligned(t, Element.ALIGN_RIGHT,
                new Phrase(String.valueOf(writer.getPageNumber()), new Font(Font.FontFamily.HELVETICA, 8)),
                totalWidth, 6, 0);
    }

}
