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
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class ReceiptTemplete {

    long millis = System.currentTimeMillis();
    java.sql.Date date = new java.sql.Date(millis);

    DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss aa");
    Date resultdate = new Date(millis);
    String dateFormat = sdf.format(resultdate);

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

    Font ArialFont = new Font(ArialBase, 18);
    Font ArialFontRed = new Font(ArialBase, 20, 0, BaseColor.RED);
    Font ArialFontBlue = new Font(ArialBase, 20, 0, BaseColor.BLUE);
    Font ArialFontSmall = new Font(ArialBase, 10);

    public void closeDocument() {
        document.close();
    }

    public void openDocument(String pdfName, String location) throws DocumentException, FileNotFoundException, BadElementException, IOException {

        File folder = new File(location);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        pdfFile = new File(folder, pdfName);
        document = new Document(PageSize.A5, 0, 0, 0, 0);

            // left , right , top , bottom
        //  document.setMargins(36, 36, 160, 50);
        //  document.setMarginMirroring(false);
        pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

        document.open();

        PdfContentByte cb = pdfWriter.getDirectContent();

        Rectangle frame2 = new Rectangle(405, 290, 20, 5); // you can resize rectangle
        frame2.enableBorderSide(1);
        frame2.enableBorderSide(2);
        frame2.enableBorderSide(4);
        frame2.enableBorderSide(8);
        frame2.setBorderColor(BaseColor.BLACK);
        frame2.setBorderWidth(1);
        frame2.setBorder(Rectangle.BOX);
        frame2.setBorderWidth(2);

            // 2 
        Rectangle frame1 = new Rectangle(405, 580, 20, 300); // you can resize rectangle
        frame1.enableBorderSide(1);
        frame1.enableBorderSide(2);
        frame1.enableBorderSide(4);
        frame1.enableBorderSide(8);
        frame1.setBorderColor(BaseColor.BLACK);
        frame1.setBorderWidth(1);
        frame1.setBorder(Rectangle.BOX);
        frame1.setBorderWidth(2);

        cb.rectangle(frame1);
        cb.rectangle(frame2);

    }

    public void addTopHeader() {

        // Table
        PdfPTable table = new PdfPTable(3); // 3 columns.

        table.setWidthPercentage(100);

        try {
            table.setWidths(new int[]{180, 0, 180});

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        //table.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPCell cell7 = new PdfPCell(new Phrase(" إيــــصال اســـــــتلام نقـــــدية ", ArialFontRed));
        cell7.setBorder(Rectangle.NO_BORDER);
        cell7.setPadding(5);
        cell7.setColspan(3);
        cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell7);

        try {

            table.setSpacingBefore(10F);
            document.add(table);

        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    public void addCompanyInfo(String customerName, String userName, double paidAmount) throws BadElementException, IOException {

        // Table
        PdfPTable table = new PdfPTable(2); // 2 columns.
        // table.setWidthPercentage(100);
        try {
            table.setWidths(new int[]{400, 200});
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);

        Image image = Image.getInstance("c:\\company_logo.png");
        image.setAlignment(Element.ALIGN_RIGHT);
        image.scaleAbsolute(30f, 730f);
        image.scaleToFit(300, 50);

        PdfPCell cellImg = new PdfPCell(image);
        String header = "شـــركة الــنور للتوريدات العمومية" + "\n\n" + "  إيــــصال اســـــــتلام نقـــــدية";
        PdfPCell cellLabel = new PdfPCell(new Phrase(header, ArialFontRed));
        cellLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellImg.setHorizontalAlignment(Element.ALIGN_LEFT);
        cellImg.setBorder(Rectangle.NO_BORDER);
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellImg.setPaddingTop(20);
        cellImg.setPaddingRight(20);
        cellLabel.setPaddingTop(20);
        table.addCell(cellImg);
        table.addCell(cellLabel);

        PdfPCell cell0 = new PdfPCell(new Phrase("التاريخ : ", ArialFont));
        PdfPCell cello = new PdfPCell(new Paragraph(dateFormat, ArialFont));
        cell0.setHorizontalAlignment(Element.ALIGN_LEFT);
        cello.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell0.setBorder(Rectangle.NO_BORDER);
        cello.setBorder(Rectangle.NO_BORDER);
        cell0.setPaddingRight(30);
        cell0.setPaddingBottom(10);
        cello.setPaddingBottom(10);
        cell0.setPaddingTop(10);
        cello.setPaddingTop(10);
        table.addCell(cell0);
        table.addCell(cello);

        PdfPCell cell1 = new PdfPCell(new Phrase("استلمت أنا : ", ArialFont));
        PdfPCell cell2 = new PdfPCell(new Paragraph(customerName, ArialFont));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell1.setPaddingRight(30);
        cell1.setPaddingBottom(10);
        cell2.setPaddingBottom(10);
        table.addCell(cell1);
        table.addCell(cell2);

        PdfPCell cell3 = new PdfPCell(new Paragraph("من السيد : ", ArialFont));
        PdfPCell cell4 = new PdfPCell(new Paragraph(userName, ArialFont));
        cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell4.setBorder(Rectangle.NO_BORDER);
        cell3.setPaddingRight(30);
        cell3.setPaddingBottom(10);
        cell4.setPaddingBottom(10);
        table.addCell(cell3);
        table.addCell(cell4);

        PdfPCell cell5 = new PdfPCell(new Paragraph("مبلغ وقدرة : ", ArialFont));
        PdfPCell cell6 = new PdfPCell(new Paragraph(paidAmount + "  فقط : .........................", ArialFont));
        cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell5.setBorder(Rectangle.NO_BORDER);
        cell6.setBorder(Rectangle.NO_BORDER);
        cell5.setPaddingRight(30);
        cell5.setPaddingBottom(10);
        cell6.setPaddingBottom(10);
        table.addCell(cell5);
        table.addCell(cell6);

        PdfPCell cell7 = new PdfPCell(new Paragraph("وهذا ايصال بالاستلام     ", ArialFont));
        cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell7.setBorder(Rectangle.NO_BORDER);
        cell7.setColspan(2);
        table.addCell(cell7);

        PdfPCell cell8 = new PdfPCell(new Paragraph("   المستلم" + "\n" + "..............", ArialFontRed));
        PdfPCell cell9 = new PdfPCell(new Paragraph("ختم الشركة" + "\n" + "...............", ArialFontRed));
        cell8.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell9.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell8.setBorder(Rectangle.NO_BORDER);
        cell9.setBorder(Rectangle.NO_BORDER);
        cell8.setPaddingRight(30);
        cell8.setPaddingBottom(10);
        cell9.setPaddingBottom(10);
        table.addCell(cell8);
        table.addCell(cell9);

        try {

            // table.setSpacingAfter(30f);
            table.setSpacingBefore(3F);
            document.add(table);
            document.add(table);

        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

}
