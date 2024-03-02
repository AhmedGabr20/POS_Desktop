package helper;

import controllers.STATICDATA;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;
import javafx.scene.control.Alert;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DbConnect {

    // static final String jdbcUrl = "jdbc:mysql://192.168.1.31:3306/elnoor_v2?useSSL=false";
    static String user = "hbstudent";
    static String pass = "hbstudent";
    static String db = "elnoor_v2";
    static String ip = "localhost";

    private static Connection connection;

    private static void getXmlData(Document doc) {
        NodeList nodes = doc.getElementsByTagName("config");

        org.w3c.dom.Node itemNode = nodes.item(0);
        if (itemNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            Element element = (Element) itemNode;
            db = element.getElementsByTagName("database").item(0).getTextContent();
            user = element.getElementsByTagName("databaseUser").item(0).getTextContent();
            pass = element.getElementsByTagName("databasePass").item(0).getTextContent();
            ip = element.getElementsByTagName("databaseIp").item(0).getTextContent();
        }
    }

    public static Connection getConnect(){
        try {
            // connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/%s?characterEncoding=UTF-8", HOST,PORT,DB_NAME),USERNAME,PASSWORD);
            File xmlFile = new File("settings.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            Document doc = (Document) builder.parse(xmlFile);
            getXmlData(doc);

            connection = DriverManager.getConnection("jdbc:mysql://" + ip + "/" + db + "?useSSL=false&allowPublicKeyRetrieval=True", user, pass);

        } catch (Exception ex) {
            //   STATICDATA.ExceptionHandle("DbConnect", "getConnect", ex);
            String param = "#ip : " + ip + "  #db : " + db + " #user : " + user + "  #pass : " + pass;
            STATICDATA.ExceptionHandleParam("DbConnect", "getConnect", ex, param);

            // create db if not exist 
            try {

                if (ex.getMessage().contains("Unknown database")) {
                    File file = new File("posdb.sql");

                    if (file.exists()) {
                        Scanner s;
                        s = new Scanner(file);
                        String ip = s.next();
                        s.close();
                        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost" + "/sys" + "?useSSL=false&allowPublicKeyRetrieval=True", user, pass);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setHeaderText(null);
                        alert.setContentText("مشكلة مكان ملف قاعدة البيانات");
                        alert.showAndWait();
                        connection = null;
                    }
                }
            } catch (Exception e) {
                STATICDATA.ExceptionHandle("DbConnect", "getConnect", e);
            }
        }

        return connection;
    }

    public static String getUser() {
        return user;
    }

    public static String getPass() {
        return pass;
    }

    /*
       
     static final String jdbcUrl = "jdbc:mysql://localhost:3306/elnoor?useSSL=false";
     static final String user = "hbstudent";
     static final String pass = "hbstudent";
    
     private static final String HOST = "127.0.0.1";
     private static final int PORT = 3306;
     private static final String DB_NAME = "elnoor";
     private static final String USERNAME = "root";
     private static final String PASSWORD = "";
     private static Connection connection ;
        
        
     public static Connection getConnect (){
     try {
     //    connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/%s?characterEncoding=UTF-8", HOST,PORT,DB_NAME),USERNAME,PASSWORD);
            
            
     connection = DriverManager.getConnection(jdbcUrl , user , pass);
            
     System.out.println("connection : "+connection);
            
            
     } catch (SQLException ex) {
     Logger.getLogger(DbConnect.class.getName()).log(Level.SEVERE, null, ex);
     }
            
     return  connection;
     }
        
     */
}
