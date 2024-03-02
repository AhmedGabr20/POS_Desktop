package controllers;

import PDF.ReceiptTemplete;
import com.itextpdf.text.DocumentException;
import helper.DbConnect;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import models.User;
import models.sales;

public class EditSalesController implements Initializable {

    @FXML
    private TextField totalCol;
    @FXML
    private TextField remainCol;
    @FXML
    private TextField newPaidCol;
    @FXML
    private Button save_btn;
    @FXML
    private Text customername_feild;
    @FXML
    private Label customer_id_feild;

    int hour = Calendar.getInstance().get(Calendar.HOUR);
    int min = Calendar.getInstance().get(Calendar.MINUTE);
    int sec = Calendar.getInstance().get(Calendar.SECOND);

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    private boolean updateSales;

    double REMAIN;

    double NEWPAID;

    DecimalFormat decimal = new DecimalFormat("#.##");

    //   User user = null;
    STATICDATA pdfPath = new STATICDATA();
    String receiptLocation;

    String customerName, userName;

    int customerId;
    int USER_ID;
    int ROLE;

    @FXML
    private HBox total_box;
    @FXML
    private HBox remain_box;
    @FXML
    private HBox newPaid_box;
    @FXML
    private HBox newPaid_box1;
    @FXML
    private TextField paidType;
    @FXML
    private DatePicker from_date;
    @FXML
    private DatePicker due_date;
    @FXML
    private Text date_text;
    @FXML
    private Text due_text;
    @FXML
    private Text newpaid_text;

    String CODE;
    int SALES_ID;

    MainController MainController = new MainController();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        totalCol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");
        remainCol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");
        newPaidCol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");

        totalCol.setEditable(false);
        remainCol.setEditable(false);

        totalCol.setFocusTraversable(false);
        remainCol.setFocusTraversable(false);
        save_btn.setFocusTraversable(false);
        customer_id_feild.setVisible(false);

        newPaidCol.requestFocus();

        receiptLocation = pdfPath.PDF_MAIN_FOLDER + pdfPath.PDF_CUSTOMER_SALES_FOLDER;

    }

    @FXML
    private void save(MouseEvent event) {

        double TOTAL = 0;
        String newPaid = newPaidCol.getText();

        //  NEWPAID = Double.parseDouble(newPaidCol.getText());
        connection = DbConnect.getConnect();
        final String total = totalCol.getText();

        if (newPaid.isEmpty() && !updateSales) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("الرجاء ادخال البيانات");
            alert.showAndWait();
        } else {
            int result = getQuery();

            if (result != 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("تم التعديل");
                alert.showAndWait();

                Stage stagemain = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stagemain.close();

                User user = MainController.loadUserData(USER_ID);

                printReceit(user.getName());

            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("حدث مشكلة في التعديل");
                alert.showAndWait();
            }

        }

    }

    private int getQuery() {

        // update == fale  >> سداد 
        // check code  outsales  or  sales
        if (CODE.contains("OT")) {
           // String cod[] = CODE.split("-", 2);
        //    query = "UPDATE `outsales` SET "
        //            + "`price`=?,"
        //            + "`date`=?,"
        //            + "`due_date`=?,"
        //            + "`paid_type`= ? WHERE code  =" + cod[0] + " ";
            
            query = "INSERT INTO `outSales_pay`(`date`, `paid`, `cus_id`,`user_id`,`paid_type`,`inv_code`) VALUES (?,?,?,?,?,?)";

            return insert();

        } else {
            //    query = "UPDATE `sales` SET "
            //            + "`price`=?,"
            //            + "`date`=?,"
            //            + "`due_date`=?,"
            //            + "`paid_type`= ? WHERE id  =" + CODE + " ";
            

            query = "INSERT INTO `sales_pay`(`date`, `paid`, `cus_id`,`user_id`,`paid_type`,`inv_code`) VALUES (?,?,?,?,?,?)";
            return insert();
        }

        //   return updateSales();
    }

    private int insert() {

       
        int result = 0;
        try {

            if (CODE.contains("OT")) {
                String cod[] = CODE.split("-", 2);
                CODE = cod[0];
            } 
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, from_date.getValue()+"");
            preparedStatement.setString(2, newPaidCol.getText());
            preparedStatement.setInt(3, customerId);
            preparedStatement.setInt(4, USER_ID);
            preparedStatement.setString(5, paidType.getText());
            preparedStatement.setInt(6, SALES_ID);
            result = preparedStatement.executeUpdate();

            preparedStatement.close();

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "insert", ex);
        }
        return result;
    }

    private int insertOutSales() {

       
        int result = 0;
        try {
           if (CODE.contains("OT")) {
                String cod[] = CODE.split("-", 2);
                CODE = cod[0];
            } 
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, from_date.getValue()+"");
            preparedStatement.setInt(2, 0);
            preparedStatement.setString(3, newPaidCol.getText());
            preparedStatement.setInt(4, customerId);
            preparedStatement.setInt(5, USER_ID);
            preparedStatement.setString(6, paidType.getText());
            preparedStatement.setInt(7, SALES_ID);
            result = preparedStatement.executeUpdate();

            preparedStatement.close();

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "insertOutSales", ex);
        }
        return result;
    }

    void setTextField(int user_id, int role, sales sale, boolean edit, String customerName, int customerId) {

        // if edit = true >>
        // edit sales or out sales 
        // else >>
        // add new paid
        
        // sale.getCode(), sale.getTotal_txt(), sale.getPaid(), sale.getDate(), sale.getDue_date(), sale.getPaid_type()
        // String code, String total, double paid, String date, String DUE_DATE, String PAIDTYPE
        USER_ID = user_id;
        ROLE = role;
        CODE = sale.getCode();
        SALES_ID = sale.getId();
        totalCol.setVisible(edit);
        newPaidCol.setVisible(edit);
        newpaid_text.setVisible(edit);
        remain_box.setVisible(edit);
        from_date.setVisible(edit);
        due_date.setVisible(edit);
        date_text.setVisible(edit);
        due_text.setVisible(edit);

        updateSales = edit;

        if (edit) {
            from_date.setVisible(true);
            from_date.setValue(LocalDate.now());
            due_date.setVisible(false);
            date_text.setVisible(false);
            due_text.setVisible(false);
            totalCol.setText(sale.getTotal_txt() + "");
        //    from_date.setValue(LocalDate.parse(date, DateTimeFormatter.ISO_DATE));
            paidType.setText(sale.getPaid_type());
            if (sale.getDue_date() != null) {
        //        due_date.setValue(LocalDate.parse(DUE_DATE, DateTimeFormatter.ISO_DATE));
            }

        //    String totalPaid = sumPaid(code,customerId);
        //    if (totalPaid == null) {
                double rem = Double.parseDouble(sale.getTotal_txt()) - sale.getPaid();
                remainCol.setText(rem + "");
       //     } else {
       //         double rem = Double.parseDouble(total) - (Double.parseDouble(totalPaid)+paid);
       //         remainCol.setText(rem + "");
       //     }

        }

        this.customerName = customerName;
        customername_feild.setText("العميل / " + customerName);
        customer_id_feild.setText(customerId + "");
        this.customerId = customerId;

        customer_id_feild.setVisible(false);
    }

    public void printReceit(String userName) {

        if (updateSales) {
            try {
                String pdfName = customerName + LocalDate.now() + "_" + hour + "_" + min + "_" + sec;
                ReceiptTemplete receiptTemplete = new ReceiptTemplete();
                receiptTemplete.openDocument(pdfName + ".pdf", receiptLocation + "//" + customerName + pdfPath.PDF_receipt_FOLDER);
                System.out.println("newPaid :: " + NEWPAID);
                receiptTemplete.addCompanyInfo(userName, customerName, Double.parseDouble(newPaidCol.getText()));

                receiptTemplete.closeDocument();

                try {
                    Desktop.getDesktop().open(new File(receiptLocation + "//" + customerName + pdfPath.PDF_receipt_FOLDER));
                } catch (IOException ex) {
                    Logger.getLogger(AddCustomerController.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (Exception ex) {
                STATICDATA.ExceptionHandle(this.getClass().getName(), "printReceit", ex);
            }
        }

    //    ViewCustomerController viewCustomerController = new ViewCustomerController();
    //    viewCustomerController.openCustomerProfile(USER_ID, ROLE, customerId, customername_feild.getText());
    }


    /*
     public void openCompanyProfile() {

     ViewCompanesController viewCompanesController = new ViewCompanesController();
     viewCompanesController.openCompanyProfile(customerId, customername_feild.getText());

     }
    
     */
    public String sumPaid(String code,int cus_id) {
        String total_paid = "0";
        try {
            if (CODE.contains("OT")) {
                String cod[] = CODE.split("-", 2);
                code = cod[0];
                query = "select sum(paid) as total from outsales where code =?";
            } else {
                System.out.println("else !!!!!!!!!!!!!");
                query = "select sum(paid) as total from sales_pay where inv_code =? AND cus_id=? ";
            }
            System.out.println("&&&&&&&&&&&   code " + code);
            connection = DbConnect.getConnect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, code);
            preparedStatement.setInt(2, cus_id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                total_paid = resultSet.getString("total");
            }
            preparedStatement.close();

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "sumPaid", ex);
        }

        return total_paid;
    }

}
