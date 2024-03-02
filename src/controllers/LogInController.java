/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import helper.DbConnect;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

/**
 * FXML Controller class
 *
 * @author Ahmed Gabr
 */
public class LogInController implements Initializable {

    @FXML
    private TextField user_name;
    @FXML
    private TextField password;
    @FXML
    private ImageView image;

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    String PDF_PARTATION = null;

    String USER_NAME;

    static String company_name;
    static String company_specialty;
    @FXML
    private Label CompanyName;
    @FXML
    private Label CompanySpecialty;

    int result;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            Image img = new Image("/img/company_logo.png");
            image.setImage(img);

            /*
             Gson gson = new Gson();
             try {
             // create a reader
             JsonReader reader = new JsonReader(new FileReader("C://settings.json"));
             Setting setting = gson.fromJson(reader, Setting.class);
             } catch (IOException ex) {
             Logger.getLogger(LogInController.class.getName()).log(Level.SEVERE, null, ex);
             }
             */
            File xmlFile = new File("settings.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            Document doc = (Document) builder.parse(xmlFile);
            getXmlData(doc);
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "initialize",ex);
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
    private void login(MouseEvent event) throws IOException {

        if (loginMethod() != 0) {
            openMainPage(result);
            close(event);
        }
    }

    private int checkLogIn(String userName, String pass) {

        int userId = 0;
        try {
            connection = DbConnect.getConnect();

            //  query = "select user.id , user.name , user.role from user where user.phone =? AND user.password =?";
            query = "select id  from user "
                    + "where phone =? AND password =? ";

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, pass);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                userId = resultSet.getInt("id");
            }

        } catch (SQLException ex) {
            String msg = " ** checkLogIn > - Error checkLogIn with user : " + userName + " AND Pass : " + pass + " # " ;
            STATICDATA.ExceptionHandle(this.getClass().getName(), msg ,ex);
        }

        return userId;
    }

    private void close(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void close(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void openMainPage(int id) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/main.fxml"));
            loader.load();

            MainController mainController = loader.getController();
            mainController.setText(id);
            Parent parent = loader.getRoot();
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setScene(new Scene(parent));
            // stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openMainPage" ,ex);
        }
    }

    @FXML
    private void EnterLogin(ActionEvent event) {
        if (loginMethod() != 0) {
            openMainPage(result);
            close(event);
        }
    }

    private int loginMethod() {
        String userName = user_name.getText();
        String pass = password.getText();

        result = checkLogIn(userName, pass);

        if (result == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("خطأ في اسم المستخدم او كلمه السر");
            alert.showAndWait();
        }
        return result;
    }

}
