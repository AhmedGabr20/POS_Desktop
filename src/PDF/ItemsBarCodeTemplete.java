package PDF;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import controllers.STATICDATA;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import javafx.collections.ObservableList;
import javax.naming.Context;
import models.items;

public class ItemsBarCodeTemplete {

    private Context context;
    private File pdfFile;
    private Document document;
    public PdfWriter pdfWriter;
    BaseFont ArialBase;

// Get Arabic Font from Assets
    {
        try {
            ArialBase = BaseFont.createFont("c:/windows/fonts/Arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Font ArialFont_14 = new Font(ArialBase, 14, Font.BOLD);
    Font ArialFont_12 = new Font(ArialBase, 12, Font.BOLD);
    Font ArialFontSmall_11 = new Font(ArialBase, 11, Font.NORMAL);
    Font ArialFontSmall_8 = new Font(ArialBase, 8, Font.NORMAL);

    public void openDocument(String docName, String fileLocation) {
        try {
            Random rand = new Random();
            int n = rand.nextInt(50);
            String PDFNAME = docName + ".pdf";

            File folder = new File(fileLocation);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            pdfFile = new File(folder, PDFNAME);

            document = new Document(PageSize.A4);
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

           // document.setMargins(36, 72, 108, 180);
            // document.setMarginMirroring(false);
            document.open();

         //   document.setMargins(36, 36, 36, 36);
            //   document.setMarginMirroring(false);
           // addTopRectangle();
          //  addImage(157,574);
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openDocument", ex);
        }
    }

    public void closeDocument() {
        document.close();
    }

    public void addTopHeader(ObservableList<items> items) {

        // Table
        PdfPTable table = new PdfPTable(3); // 3 columns.

        table.setWidthPercentage(100);

        try {
            //table.setWidths(new int[]{100});
            table.setWidths(new int[]{100, 100, 100});
           // table.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);

            for (int i = 0; i < items.size(); i++) {

                PdfContentByte canvas = pdfWriter.getDirectContent();

                Barcode128 barcode128 = new Barcode128();
                barcode128.setCode(items.get(i).getCode() + "");
                //barcode128.setAltText("Ahmed Gabr");
                barcode128.setCodeType(Barcode128.CODE128);
                Image code128Image = barcode128.createImageWithBarcode(canvas, null, null);
                code128Image.setAbsolutePosition(10, 700);
                code128Image.scalePercent(100);

                String txt = items.get(i).getName() + " - " + items.get(i).getStoreName() + "\n" + "السعر: " + items.get(i).getPriceforcustomer();
                Phrase text = new Phrase(txt, ArialFontSmall_8);

                PdfPCell cell1 = new PdfPCell();
                cell1.setBorder(1);
                cell1.addElement(new Phrase(items.get(i).getName(), ArialFontSmall_8));
                cell1.addElement(code128Image);
                cell1.addElement(new Phrase("السعر: " + items.get(i).getPriceforcustomer(), ArialFontSmall_8));
                cell1.setPadding(10);
                //cell1.setBorder(Rectangle.NO_BORDER);
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                //cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell1);

            }

            //   table.setSpacingBefore(20F);
            table.setSpacingBefore(10F);
            document.add(table);

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "addTopHeader", ex);
        }

    }

}
