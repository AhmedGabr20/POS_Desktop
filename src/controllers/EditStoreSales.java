/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 * @author Ahmed Gabr
 */
public class EditStoreSales implements Initializable {

    @FXML
    private ComboBox<String> storeNameField;
    @FXML
    private Spinner<Double> persent;

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    int USER_ID;
    int ROLE;
    int STORE_CODE;

    ViewItemsController viewItemsController = new ViewItemsController();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Spinner<Integer> spinner = new Spinner<>(1, 10, 2);
        //    persent.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100,1));
        persent.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(-100, 100, 1));
        
        storeNameField.getSelectionModel().select(0);

        storeNameField.setOnAction(e -> {

            String StoreName = storeNameField.getSelectionModel().getSelectedItem();

            try {

                STORE_CODE = viewItemsController.getStoreCode(StoreName);

            } catch (Exception ex) {
                STATICDATA.ExceptionHandle(this.getClass().getName(), "initialize", ex);
            }

        });

    }

    @FXML
    private void close(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void updateAll(MouseEvent event) {

        double PERSENT = persent.getValueFactory().getValue();

        //   if (PERSENT > 0) {
        try {
            PERSENT = PERSENT / 100;

            connection = DbConnect.getConnect();
            query = "UPDATE `items` SET "
                    + "`price`= price+?*price,"
                    + "`priceforcustomer`= priceforcustomer+?*priceforcustomer "
                    + "WHERE storeCode = '" + STORE_CODE + "'";

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, PERSENT);
            preparedStatement.setDouble(2, PERSENT);
            int result = preparedStatement.executeUpdate();

            if (result != 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                alert.setContentText("تم تحديث الأسعار");
                alert.showAndWait();

            //    loadItemByStore(STORE_CODE);
                //    persent.setText("0");
                PERSENT = 0;
                close(event);

            }

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "updateAll", ex);
        }
        /*
         } else {
         Alert alert = new Alert(Alert.AlertType.WARNING);
         alert.setHeaderText(null);
         alert.setContentText("الرجاء ادخال نسبة صحيحة");
         alert.showAndWait();
         }
         */
    }

    void setTextField(int id, int role, ArrayList<String> stores) {
        USER_ID = id;
        ROLE = role;
        storeNameField.getItems().addAll(stores);
        storeNameField.getSelectionModel().select(0);
    }

}
