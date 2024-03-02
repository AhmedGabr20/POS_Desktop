package controllers;

import java.io.File;
import java.time.LocalDateTime;
import javafx.scene.control.Alert;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;
import java.util.List;

public class STATICDATA {

    //   String PDF_PARTATION ;
    // static String PARTATION ;
    public String getPartation() {
        String PARTATION = "F://";
        try {
            File xmlFile = new File("settings.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            Document doc = (Document) builder.parse(xmlFile);

            PARTATION = getXmlData(doc);

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(STATICDATA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(STATICDATA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(STATICDATA.class.getName()).log(Level.SEVERE, null, ex);
        }
        return PARTATION;
    }

    private String getXmlData(Document doc) {
        String PARTATION = "F://";
        NodeList studentNodes = doc.getElementsByTagName("config");

        org.w3c.dom.Node studentNode = studentNodes.item(0);
        if (studentNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            Element studentElement = (Element) studentNode;
            PARTATION = studentElement.getElementsByTagName("partation").item(0).getTextContent();

        }
        return PARTATION;
    }

    String PDF_MAIN_FOLDER = "../../" + "التقارير";
    static String PDF_BackUp_FOLDER = "G://BACKUP";
    String PDF_ITEMS_FOLDER = "//الأصناف";
    String PDF_ITEMS_BARCODE_FOLDER = "//الباركود";
    String PDF_CUSTOMERS_FOLDER = "//العملاء";
    String PDF_INV_FOLDER = "//الفواتير";
    String PDF_SALES_FOLDER = "//المبيعات";
    String PDF_COMPANY_SALES_FOLDER = "//الموردين";
    String PDF_REPORT_FOLDER = "//الايرادات والمصروفات";
    String PDF_USER_SALES_FOLDER = PDF_SALES_FOLDER + "//مبيعات المستخدمين";
    String PDF_CUSTOMER_SALES_FOLDER = PDF_SALES_FOLDER + "//مبيعات العملاء";
    String PDF_OFFER_PRICE_FOLDER = "//عروض اسعار";
    String PDF_receipt_FOLDER = "//ايصالات استلام نقدية";

    public String getPDF_MAIN_FOLDER() {
        return PDF_MAIN_FOLDER;
    }

    public String getPDF_ITEMS_FOLDER() {
        return PDF_ITEMS_FOLDER;
    }

    public String getPDF_CUSTOMERS_FOLDER() {
        return PDF_CUSTOMERS_FOLDER;
    }

    public String getPDF_INV_FOLDER() {
        return PDF_INV_FOLDER;
    }

    public String getPDF_SALES_FOLDER() {
        return PDF_SALES_FOLDER;
    }

    public static void ExceptionHandle(String className, String method, Exception ex) {
        System.out.println(LocalDateTime.now() + " #Class : " + className);
        System.out.println("                        #Method : " + method);
        System.out.println("                        #Message: " + ex.getMessage());
        System.out.println("                        #Cause: " + ex.getCause());
        System.out.println("                        #Trace: ");
        StackTraceElement[] trace = ex.getStackTrace();
        for (int i = 0; i < trace.length; i++) {
            System.out.println("                      - " + trace[0]);
        }
        System.out.println("          ************************************************************                 \n\n");
    }

    public static void ExceptionHandle(String className, String method, String msg) {
        System.out.println(LocalDateTime.now() + " #Class : " + className);
        System.out.println("                        #Method : " + method);
        System.out.println("                        #Message: " + msg);
        System.out.println("          ************************************************************                 \n\n");
        System.out.println("");
    }

    public static void ExceptionHandleParam(String className, String method, Exception ex,String param) {
        System.out.println(LocalDateTime.now() + " #Class : " + className);
        System.out.println("                        #Method : " + method);
        System.out.println("                        #Message: " + ex.getMessage());
        System.out.println("                        #Cause: " + ex.getCause());
        System.out.println("                        #param: " + param);
        System.out.println("                        #Trace: ");
        StackTraceElement[] trace = ex.getStackTrace();
        for (int i = 0; i < trace.length; i++) {
            System.out.println("                      - " + trace[0]);
        }
        System.out.println("          ************************************************************                 \n\n");
    }

    public static void AlertMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
