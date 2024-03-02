package controllers;

import helper.DbConnect;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.items;

public class AddItemController implements Initializable {

    @FXML
    private TextField nameFild;
    @FXML
    private TextField codeFild;
    @FXML
    private TextField quentityfild;
    @FXML
    private TextField priceFild;
    @FXML
    private ComboBox<String> storeFild;
    @FXML
    private TextField pricecustomerFild;

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    private boolean update;
    int itemId;
    int Storecode = 0;
    ArrayList<String> item3 = new ArrayList<>();

    int USER_ID;
    int ROLE;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            loadStoreCombo();

            storeFild.getSelectionModel().select(0);
            Storecode = getStoreCode(storeFild.getSelectionModel().getSelectedItem());

            storeFild.setOnAction(e -> {

                try {
                    String StoreName = storeFild.getSelectionModel().getSelectedItem();
                    Storecode = getStoreCode(StoreName);
                } catch (Exception ex) {
                    STATICDATA.ExceptionHandle(this.getClass().getName(), "initialize", ex);
                }

            });
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "initialize", ex);
        }

    }

    @FXML
    private void close(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void save(MouseEvent event) {

        try {

            storeFild.setOnAction(e -> {

                String StoreName = storeFild.getSelectionModel().getSelectedItem();
                try {
                    Storecode = getStoreCode(StoreName);
                } catch (Exception ex) {
                    STATICDATA.ExceptionHandle(this.getClass().getName(), "save", ex);
                }
            });

            connection = DbConnect.getConnect();
            final String name = nameFild.getText();
            String code = codeFild.getText();
            String price = priceFild.getText();
            String priceForCustomer = pricecustomerFild.getText();
            String amount = quentityfild.getText();

            if (name.isEmpty() || price.isEmpty() || code.isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("الرجاء ادخال البيانات");
                alert.showAndWait();
            } else {

                int res = checkCode(code);

                if (res == 1 && update == false) {

                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText(null);
                    alert.setContentText("هذا الكود مكرر");
                    alert.showAndWait();
                } else {

                    getQuery();
                    insert();

                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText(null);
                    alert.setContentText("تم التسجيل");
                    alert.showAndWait();
                    nameFild.setText("");
                    codeFild.setText("");
                    priceFild.setText("");
                    pricecustomerFild.setText("");
                    quentityfild.setText("");
                    codeFild.setText(getNextCode() + 1 + "");
                }

            }
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "save", ex);
            STATICDATA.AlertMessage(ex.getMessage());
        }
    }

    private void getQuery() {

        if (update == false) {

            query = "INSERT INTO `items`(`name`, `code`, `amount`, `price`, `priceforcustomer` ,`storeCode`) VALUES (?,?,?,?,?,?)";

        } else {
            query = "UPDATE `items` SET "
                    + "`name`=?,"
                    + "`code`=?,"
                    + "`amount`=?,"
                    + "`price`= ?,"
                    + "`priceforcustomer`= ?,"
                    + "`storeCode`= ? WHERE id = '" + itemId + "'";
        }

    }

    private void insert() {

        try {

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, nameFild.getText());
            preparedStatement.setString(2, codeFild.getText());
            preparedStatement.setString(3, quentityfild.getText());
            preparedStatement.setString(4, priceFild.getText());
            preparedStatement.setString(5, pricecustomerFild.getText());
            preparedStatement.setInt(6, Storecode);
            preparedStatement.execute();

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "insert", ex);
        }

    }

    void setTextField(items item) {

        itemId = item.getId();
        nameFild.setText(item.getName());
        codeFild.setText(item.getCode());
        quentityfild.setText("" + item.getQuantity());
        priceFild.setText("" + item.getPrice());
        pricecustomerFild.setText("" + item.getPriceforcustomer());
        storeFild.getSelectionModel().select(item.getStoreName());
        try {
            Storecode = getStoreCode(item.getStoreName());
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "insert", ex);
        }
    }

    void setUpdate(boolean b) {
        this.update = b;

    }

    private void loadStoreCombo() {

        try {
            connection = DbConnect.getConnect();

            query = "SELECT storeName FROM `store`";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                item3.add(resultSet.getString("storeName"));

            }
            storeFild.getItems().addAll(item3);
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "loadStoreCombo", ex);
        }
    }

    private int getStoreCode(String StoreName) {

        int code = 0;
        try {

            connection = DbConnect.getConnect();
            query = "SELECT id FROM `store` where storeName =?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, StoreName);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                code = resultSet.getInt("id");

                return code;

            }
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "getStoreCode", ex);
        }
        return code;
    }

    private int checkCode(String code) {

        int numberOfRows = 0;

        try {
            query = "select count(*) from items where code=?";
            preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setString(1, code);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                numberOfRows = resultSet.getInt(1);

            } else {

                numberOfRows = 0;
            }
            preparedStatement.close();
            resultSet.close();

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "checkCode", ex);
        }
        return numberOfRows;

    }

    private int getNextCode() {

        int next = 0;

        try {
            query = "SELECT max(code) FROM pos_v1.items;";
            preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                next = resultSet.getInt(1);

            } else {

                next = 0;
            }
            preparedStatement.close();
            resultSet.close();

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "getNextCode", ex);
        }
        return next;

    }

    void setTextField(int id, int role) {
        ROLE = role;
        USER_ID = id;
        codeFild.setText(getNextCode() + 1 + "");
    }

}
