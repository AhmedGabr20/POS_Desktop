package controllers;

import static controllers.LogInController.company_name;
import helper.DbConnect;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import models.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MainController implements Initializable {

    @FXML
    private ImageView image;
    @FXML
    private Label user_name;
    @FXML
    private Button btn_users;
    @FXML
    private Label user_date;

    User user = null;
    @FXML
    private Button btn_company;
    @FXML
    private Button btn_sales;
    @FXML
    private Button btn_store;

    String USER_NAME;
    int USER_ID;
    int ROle;

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    LogInController logInController = new LogInController();
    @FXML
    private Text CompanyName;
    @FXML
    private Text CompanySpecialty;
    static String company_name;
    static String company_specialty;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            // src/img/elnoor.jpg
            Image img = new Image("/img/company_logo.png");
            image.setImage(img);

            String date = LocalDate.now() + "";

            user_date.setText("تاريخ اليوم : " + date);

            File xmlFile = new File("settings.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            Document doc = (Document) builder.parse(xmlFile);
            getXmlData(doc);
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "initialize", ex);
        }
        CompanyName.setText(company_name);
        CompanySpecialty.setText(company_specialty);

    }

    private static void getXmlData(Document doc) {
        NodeList studentNodes = doc.getElementsByTagName("config");

        org.w3c.dom.Node studentNode = studentNodes.item(0);
        if (studentNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            Element studentElement = (Element) studentNode;
            company_name = studentElement.getElementsByTagName("companyName").item(0).getTextContent();
            company_specialty = studentElement.getElementsByTagName("companySpecialty").item(0).getTextContent();
        }
    }

    @FXML
    public void customerPage(MouseEvent event) {

        close(event);
        openCustomerPage(USER_ID, ROle);

        /*
         try {
                       
         List<items> listItems = new ArrayList<items>();
                       
         items i1 = new items("123", "item1", 5, 0, 120, 120);
         items i2 = new items("145", "item2", 4, 0, 100, 100);
         items i3 = new items("156", "item3", 3, 0, 150, 150);
         items i4 = new items("167", "item4", 2, 0, 1200, 1200);
                       
         listItems.add(i1);
         listItems.add(i2);
         listItems.add(i3);
         listItems.add(i4);
                       
                       
         Connection connection = null ;
         connection = DbConnect.getConnect();   
                       
         JasperReport r = JasperCompileManager.compileReport("book.jrxml");
         JasperPrint jp = JasperFillManager.fillReport(r, null,connection);
            
         JasperViewer.viewReport(jp,false);   
            
         } catch (JRException ex) {
         Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
         }
            
            
         /*
         try {
                       
         List<items> listItems = new ArrayList<items>();
                       
         items i1 = new items("123", "item1", 5, 0, 120, 120);
         items i2 = new items("145", "item2", 4, 0, 100, 100);
         items i3 = new items("156", "item3", 3, 0, 150, 150);
         items i4 = new items("167", "item4", 2, 0, 1200, 1200);
                       
         listItems.add(i1);
         listItems.add(i2);
         listItems.add(i3);
         listItems.add(i4);
                       
                       
         Connection connection = null ;
         connection = DbConnect.getConnect();   
                       
         JasperReport r = JasperCompileManager.compileReport("book.jrxml");
         JasperPrint jp = JasperFillManager.fillReport(r, null,connection);
            
         JasperViewer.viewReport(jp,false);   
            
         } catch (JRException ex) {
         Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
         }
            
            
         /*
         try {
                       
         List<items> listItems = new ArrayList<items>();
                       
         items i1 = new items("123", "item1", 5, 0, 120, 120);
         items i2 = new items("145", "item2", 4, 0, 100, 100);
         items i3 = new items("156", "item3", 3, 0, 150, 150);
         items i4 = new items("167", "item4", 2, 0, 1200, 1200);
                       
         listItems.add(i1);
         listItems.add(i2);
         listItems.add(i3);
         listItems.add(i4);
                       
                       
         Connection connection = null ;
         connection = DbConnect.getConnect();   
                       
         JasperReport r = JasperCompileManager.compileReport("book.jrxml");
         JasperPrint jp = JasperFillManager.fillReport(r, null,connection);
            
         JasperViewer.viewReport(jp,false);   
            
         } catch (JRException ex) {
         Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
         }
            
            
         /*
         try {
                       
         List<items> listItems = new ArrayList<items>();
                       
         items i1 = new items("123", "item1", 5, 0, 120, 120);
         items i2 = new items("145", "item2", 4, 0, 100, 100);
         items i3 = new items("156", "item3", 3, 0, 150, 150);
         items i4 = new items("167", "item4", 2, 0, 1200, 1200);
                       
         listItems.add(i1);
         listItems.add(i2);
         listItems.add(i3);
         listItems.add(i4);
                       
                       
         Connection connection = null ;
         connection = DbConnect.getConnect();   
                       
         JasperReport r = JasperCompileManager.compileReport("book.jrxml");
         JasperPrint jp = JasperFillManager.fillReport(r, null,connection);
            
         JasperViewer.viewReport(jp,false);   
            
         } catch (JRException ex) {
         Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
         }
         */
    }

    @FXML
    public void itemsPage(MouseEvent event) {
        close(event);
        openItemsPage(USER_ID, ROle);
    }

    @FXML
    private void storesPage(MouseEvent event) {
        close(event);
        openStorePage(USER_ID, ROle);
    }

    @FXML
    private void salesPage(MouseEvent event) {

        close(event);
        openSalesPage(USER_ID, ROle, LocalDate.now(), LocalDate.now());
    }

    @FXML
    private void invoicesPage(MouseEvent event) {
        close(event);
        openNewInvoicePage(USER_ID, ROle);
    }

    @FXML
    private void reportsPage(MouseEvent event) {
        close(event);
        openOfferPricePage(USER_ID, ROle);
    }

    private void close(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void companes(MouseEvent event) {
        close(event);
        openCompanesPage(USER_ID, ROle);
    }

    @FXML
    public void users(MouseEvent event) {
        close(event);
        openDashBoardPage(USER_ID, ROle);
    }

    @FXML
    private void logout(MouseEvent event) {
        close(event);
        openLogInPage(USER_ID, ROle);
    }

    /*
     public User getUserDateFromFile() throws FileNotFoundException {

     User user = null;
     Scanner s = new Scanner(new FileInputStream("./user.txt"));
     while (s.hasNext()) {

     String id = s.nextLine();
     String name = s.nextLine();
     String role = s.nextLine();

     user = new User(Integer.parseInt(id), name, Integer.parseInt(role));
     }

     return user;

     }
     */
    public void setText(int id) {

        USER_ID = id;

        User currectUser = loadUserData(USER_ID);

        USER_NAME = currectUser.getName();
        ROle = currectUser.getRole();
        String msg = "مرحبا : " + USER_NAME;
        user_name.setText(msg);

        if (ROle == 0) {

            btn_users.setVisible(false);
            btn_sales.setVisible(false);
            btn_company.setVisible(false);
            btn_store.setVisible(false);

        }

    }

    public User loadUserData(int id) {

        User user = null;
        try {
            connection = DbConnect.getConnect();

            query = "select user.name , user.role from user where user.id =? ";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int role = resultSet.getInt("role");

                user = new User(id, name, role);

            }

            preparedStatement.close();
            resultSet.close();
            connection.close();

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "loadUserData", ex);
        }
        return user;
    }

    public void openItemsPage(int id, int role) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/viewItems.fxml"));
            loader.load();

            ViewItemsController viewItemsController = loader.getController();
            viewItemsController.setTextField(id, role);
            Scene scene = new Scene(loader.getRoot());
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    logInController.openMainPage(id);
                }
            });
            stage.show();
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openItemsPage", ex);
        }
    }

    public void openStorePage(int id, int role) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/viewStore.fxml"));
            loader.load();

            ViewStoreController viewStoreController = loader.getController();
            viewStoreController.setTextField(id, role);
            Scene scene = new Scene(loader.getRoot());
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    logInController.openMainPage(id);
                }
            });
            stage.show();
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openStorePage", ex);
        }
    }

    public void openSalesPage(int id, int role, LocalDate from, LocalDate to) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/viewSales.fxml"));
            loader.load();

            ViewSalesController viewSalesController = loader.getController();
            viewSalesController.setTextField(id, role, from, to);
            Scene scene = new Scene(loader.getRoot());
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    logInController.openMainPage(id);
                }
            });
            stage.show();
        } catch (IOException ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openSalesPage", ex);
        }
    }

    public void openNewInvoicePage(int id, int role) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/NewInvoice.fxml"));
            loader.load();

            NewInvoiceController newInvoiceController = loader.getController();
            newInvoiceController.setTextField(id, role);
            Scene scene = new Scene(loader.getRoot());
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    logInController.openMainPage(id);
                }
            });
            stage.show();
        } catch (IOException ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openNewInvoicePage", ex);
        }
    }

    public void openOfferPricePage(int id, int role) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/viewOfferPrice.fxml"));
            loader.load();

            ViewOfferPriceController viewOfferPriceController = loader.getController();
            viewOfferPriceController.setTextField(id, role);
            Scene scene = new Scene(loader.getRoot());
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    logInController.openMainPage(id);
                }
            });
            stage.show();
        } catch (IOException ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openOfferPricePage", ex);
        }
    }

    public void openCompanesPage(int id, int role) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/viewCompanes.fxml"));
            loader.load();

            ViewCompanesController viewCompanesController = loader.getController();
            viewCompanesController.setTextField(id, role);
            Scene scene = new Scene(loader.getRoot());
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    logInController.openMainPage(id);
                }
            });
            stage.show();
        } catch (IOException ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openCompanesPage", ex);
        }
    }

    public void openDashBoardPage(int id, int role) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/dashBoard.fxml"));
            loader.load();

            DashBoardController dashBoardController = loader.getController();
            dashBoardController.setTextField(id, role);
            Scene scene = new Scene(loader.getRoot());
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    logInController.openMainPage(id);
                }
            });
            stage.show();
        } catch (IOException ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openDashBoardPage", ex);
        }
    }

    public void openLogInPage(int id, int role) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/logIn.fxml"));
            loader.load();

            LogInController logInController = loader.getController();
            //  logInController.setTextField(id);
            Scene scene = new Scene(loader.getRoot());
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.show();
        } catch (IOException ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openLogInPage", ex);
        }
    }

    public void openCustomerPage(int id, int role) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/viewCustomer.fxml"));
            loader.load();

            ViewCustomerController viewCustomerController = loader.getController();
            viewCustomerController.setTextField(id, role);
            Scene scene = new Scene(loader.getRoot());
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    logInController.openMainPage(id);
                }
            });
            stage.show();
        } catch (IOException ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openCustomerPage", ex);
        }
    }

}
