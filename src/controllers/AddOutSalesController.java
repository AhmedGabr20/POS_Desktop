/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import helper.DbConnect;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Ahmed Gabr
 */
public class AddOutSalesController implements Initializable {

    @FXML
    private TextField total_input;
    @FXML
    private TextField paid_input;
    @FXML
    private Button save_btn;
    @FXML
    private Text customername_feild;
    @FXML
    private Label customer_id_feild;
    @FXML
    private DatePicker from_date;
    @FXML
    private TextField code_input;

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    String INVDATE;
    String DUEDATE;
    String INVCODE;
    double TOTAL;
    double paid;
    BigInteger INV_Id;

    int CUSTOMER_ID;
    int USER_ID;
    int ROLE;
    String CUSTOMER_NAME;
    @FXML
    private HBox newPaid_box1;
    @FXML
    private TextField paidType;
    @FXML
    private DatePicker due_date;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        

    }

    public void setTextField(int cusId, String cusName, int userId, int role) {
        customername_feild.setText(cusName);
        customer_id_feild.setText(cusId + "");
        customer_id_feild.setVisible(false);

        CUSTOMER_ID = cusId;
        USER_ID = userId;
        ROLE = role;
        CUSTOMER_NAME = cusName;

    }

    @FXML
    private void save(MouseEvent event) {
        try {

            INVDATE = from_date.getValue() +"";
            DUEDATE = due_date.getValue()+"";

            INVCODE = code_input.getText();
            TOTAL = Double.parseDouble(total_input.getText());
            paid = Double.parseDouble(paid_input.getText());

            if (INVDATE.isEmpty() || DUEDATE.isEmpty() || INVCODE.isEmpty() || total_input.getText().isEmpty() || paid_input.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("الرجاء ادخال البيانات");
                alert.showAndWait();
                System.out.println("الرجاء ادخال البيانات");
            } else {

                //    getQuery();
                int result = insert();
                if (result != 0) {
                    System.out.println("تم اضافة الفاتورة");
                }

                result = insertPay();
                if (result != 0) {
                    System.out.println("تم اضافة الدفع");
                }

                if (result == 1) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText(null);
                    alert.setContentText("تم اضافة الفاتورة");
                    alert.showAndWait();

                    Stage stagemain = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stagemain.close();
                    ViewCustomerController viewCustomerController = new ViewCustomerController();
                    viewCustomerController.openCustomerProfile(USER_ID, ROLE, CUSTOMER_ID, CUSTOMER_NAME);

                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText(null);
                    alert.setContentText("خطأ في اضافة الفاتوره تاكد من الكود");
                    alert.showAndWait();

                }

            }
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "save", ex);
        }
    }

    private void getQuery() {
        //  query = "INSERT INTO `outsales`(`date`, `code`,`price`, `paid`, `cus_id`,`user_id`,`paid_type` , `due_date` ) VALUES (?,?,?,?,?,?,?,?)";
    }

    private int insert() {
        int result = 0;
        try {
            query = "INSERT INTO `outsales`(`date`, `code`,`price`, `cus_id`,`user_id`,`paid_type` , `due_date` ) VALUES (?,?,?,?,?,?,?)";
            Connection connection = DbConnect.getConnect();
            preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, INVDATE);
            preparedStatement.setString(2, INVCODE);
            preparedStatement.setDouble(3, TOTAL);
            //    preparedStatement.setDouble(4, paid);
            preparedStatement.setInt(4, CUSTOMER_ID);
            preparedStatement.setInt(5, USER_ID);
            preparedStatement.setString(6, paidType.getText());
            preparedStatement.setString(7, DUEDATE);
            result = preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next() && resultSet != null) {
                INV_Id = BigInteger.valueOf(resultSet.getLong(1));
            }

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "insert", ex);
        }

        return result;

    }

    private int insertPay() {
        int result = 0;

        try {
            preparedStatement.close();
            resultSet.close();

            query = "INSERT INTO `outSales_pay`(`date`, `paid`, `cus_id`,`user_id`,`paid_type`,`inv_code`) VALUES (?,?,?,?,?,?)";
            Connection connection = DbConnect.getConnect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, INVDATE);
            preparedStatement.setDouble(2, paid);
            preparedStatement.setInt(3, CUSTOMER_ID);
            preparedStatement.setInt(4, USER_ID);
            preparedStatement.setString(5, paidType.getText());
            preparedStatement.setString(6, INV_Id + "");
            result = preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "insertPay", ex);
        }

        return result;

    }

}
