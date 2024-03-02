
package PDF;

import com.itextpdf.text.BaseColor;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import javax.naming.Context;
import models.items;

public class ItemsBarCodeTempleteOne {
    
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

    Font ArialFont_14 = new Font(ArialBase, 14,Font.BOLD);
    Font ArialFont_12 = new Font(ArialBase, 12,Font.BOLD);
    Font ArialFontSmall_11 = new Font(ArialBase, 11,Font.NORMAL);
    Font ArialFontSmall_10 = new Font(ArialBase, 10,Font.NORMAL);

    
    
     public void openDocument(String docName , String fileLocation){
        try {
             Random rand = new Random();
        int n = rand.nextInt(50);
        String PDFNAME = docName+".pdf";
        
         File folder = new File(fileLocation);
        if (!folder.exists())
            folder.mkdirs();
        pdfFile = new File(folder,PDFNAME);
            
            document = new Document(PageSize.A9.rotate(),5,5,5,5);
            pdfWriter = PdfWriter.getInstance(document,new FileOutputStream(pdfFile));


            document.open();
            


        } catch (Exception e) {
            
        }
    }

  

    public void closeDocument(){
        document.close();
    }
    
   
     
      public void addTopHeader(items items){

        // Table
        PdfPTable table = new PdfPTable(1); // 3 columns.

        table.setWidthPercentage(100);

        try {
            //table.setWidths(new int[]{100});
            table.setWidths(new int[]{100});
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        
       
           
           PdfContentByte canvas = pdfWriter.getDirectContent();
                                 
                        Barcode128 barcode128 = new Barcode128();
                        barcode128.setCode(items.getCode()+"");
                        //barcode128.setAltText("Ahmed Gabr");
                        barcode128.setCodeType(Barcode128.CODE128);
                        Image code128Image = barcode128.createImageWithBarcode(canvas, null, null);
                       // code128Image.setAbsolutePosition(50, 700);
                        code128Image.scalePercent(100);
                        
       
           //String txt = items.getName()+" - "+items.getStoreName();
           //Phrase text = new Phrase(txt,ArialFontSmall_8);
           
          String upToNCharacters = items.getName().substring(0, Math.min(items.getName().length(), 27));
        PdfPCell cell1 = new PdfPCell(new Phrase(upToNCharacters,ArialFontSmall_10));
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell1.setPadding(5);
      //  cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell1);
        
         PdfPCell cell2 = new PdfPCell(code128Image);
         cell2.setPadding(5);
         cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
       
         table.addCell(cell2);
         
         PdfPCell cell3 = new PdfPCell(new Phrase(items.getStoreName(),ArialFontSmall_11));
         cell3.setPadding(5);
         cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
       
         table.addCell(cell3);

        try {

         //   table.setSpacingBefore(20F);
            table.setSpacingBefore(10F);
            
            document.add(table);

        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }
      
    
    
}
