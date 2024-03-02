package controllers;

import helper.DbConnect;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.customer;

public class AddCustomerController implements Initializable {

    @FXML
    private TextField nameFild;
    @FXML
    private TextField telFild;
    @FXML
    private TextField emailfild;
    @FXML
    private TextField addressFild;

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    customer custmr = null;
    private boolean update;

    int CUST_ID;
    int USER_ID;
    int ROLE;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void save(MouseEvent event) {
        try {

            connection = DbConnect.getConnect();
            final String name = nameFild.getText();
            String phone = telFild.getText();
            String email = emailfild.getText();
            String address = addressFild.getText();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("الرجاء ادخال البيانات");
                alert.showAndWait();
            } else {
                getQuery();
                insert();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                alert.setContentText("تم التسجيل");
                alert.showAndWait();

                nameFild.setText("");
                telFild.setText("");
                emailfild.setText("");
                addressFild.setText("");
            }
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "save", ex);
            STATICDATA.AlertMessage(ex.getMessage());
        }
    }

    @FXML
    private void close(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void getQuery() {

        if (update == false) {

            query = "INSERT INTO `customers`(`name`, `phone`, `email`, `address`) VALUES (?,?,?,?)";

        } else {
            query = "UPDATE `customers` SET "
                    + "`name`=?,"
                    + "`phone`=?,"
                    + "`email`=?,"
                    + "`address`= ? WHERE id = '" + CUST_ID + "'";
        }

    }

    private void insert() {

        try {

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, nameFild.getText());
            preparedStatement.setString(2, telFild.getText());
            preparedStatement.setString(3, emailfild.getText());
            preparedStatement.setString(4, addressFild.getText());
            preparedStatement.execute();

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "insert", ex);
        }

    }

    void setTextField(customer customer) {

        CUST_ID = customer.getId();
        nameFild.setText(customer.getName());
        telFild.setText(customer.getPhone());
        emailfild.setText(customer.getEmail());
        addressFild.setText(customer.getAddress());

    }

    void setUpdate(boolean b) {
        this.update = b;

    }

    void setTextField(int id, int role) {
        USER_ID = id;
        ROLE = role;
    }

}
