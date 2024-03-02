package controllers;

import PDF.invoiceReport;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import helper.DbConnect;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import models.User;
import models.customer;
import models.invoice;
import org.controlsfx.control.textfield.TextFields;

import javafx.application.Platform;

public class NewInvoiceController implements Initializable {

    long millis = System.currentTimeMillis();
    java.sql.Date CDate = new java.sql.Date(millis);

    String currentDate = CDate + "";

    String query = null;
    String query2 = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    PreparedStatement preparedStatement2 = null;
    ResultSet resultSet = null;
    customer custmr = null;
    invoice inv = null;
    ArrayList<String> item = new ArrayList<>();       // customer list
    ArrayList<String> item2 = new ArrayList<>();     // item list
    ArrayList<String> item3 = new ArrayList<>();
    String itemCode;

    ObservableList<invoice> tableList = FXCollections.observableArrayList();

    ArrayList<String[]> amontlist = new ArrayList<>();

    @FXML
    private DatePicker dateField;
//    private ComboBox<String> customerField;
    @FXML
    private TextField itempriceField;
    @FXML
    private TextField amountField;
    @FXML
    private TextField paidField;
    @FXML
    private TextField invTotal;
    @FXML
    private TextField invTotalTax;
//    private ComboBox<String> itemnameField;
    @FXML
    private TableView<invoice> invoiceTable;
    @FXML
    private TableColumn<invoice, String> editCol;
    @FXML
    private TableColumn<invoice, String> totalTAB;
    @FXML
    private TableColumn<invoice, String> priceTAB;
    @FXML
    private TableColumn<invoice, String> amountTAB;
    @FXML
    private TableColumn<invoice, String> itemnameTAB;
    @FXML
    private TableColumn<invoice, String> itemCodeTAB;
//    private ComboBox<String> storeNameField;
//    private TextField codeField;
    @FXML
    private TextField custName;
    @FXML
    private TextField itemName;
//    private Button search_btn;
    @FXML
    private Button addItemToInvoice;
    @FXML
    private Label total_amount;

    STATICDATA pdfPath = new STATICDATA();

    String invoicefileLocation;
    @FXML
    private Label itemPriceCostField;
    @FXML
    private Label total_profit_field;

    DecimalFormat decimal = new DecimalFormat("#.##");
    @FXML
    private TextField barcodeField;
//    private RadioButton searchTypeCheck;

    int USER_ID;
    int ROLE;
    MainController mainController = new MainController();

    String CURRENTBARCODE = "";
    Timeline timeline = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //    codeField.setVisible(false);
        //    search_btn.setVisible(false);
        dateField.setValue(LocalDate.now());

        itemPriceCostField.setVisible(false);
        total_profit_field.setVisible(false);
        invoiceTable.setFocusTraversable(false);
        //    searchTypeCheck.setFocusTraversable(false);
        barcodeField.setFocusTraversable(false);
        dateField.setFocusTraversable(false);
        custName.setFocusTraversable(false);
        itemName.setFocusTraversable(false);
        //    search_btn.setFocusTraversable(false);

        itempriceField.setEditable(false);

        connection = DbConnect.getConnect();

        loadCutomerCombo();
        loadItemCombo();

        itemName.setOnAction(e -> {

            //    String selected =  itemnameField.getSelectionModel().getSelectedItem();
            String selected = itemName.getText().toString();

            getitemDataByName(selected);

        });

        invoicefileLocation = pdfPath.PDF_MAIN_FOLDER + pdfPath.PDF_INV_FOLDER + "//";

        barcodeField.setFocusTraversable(true);
        barcodeField.requestFocus();

    }

    @FXML
    private void save(MouseEvent event) {

        try {
            User user = mainController.loadUserData(USER_ID);
    //    int invNUM = getInvoiceNUM();
            //    int invoicCode = invNUM + 1;

            //    String PDFNAME = "فاتورة رقم" + invoicCode + ".pdf";
            LocalDate date = dateField.getValue();
            String CustomerName = custName.getText().toString();
            String Date = date + "";
            // double paid = Integer.parseInt(paidField.getText());

            if (Date.equals("null")) {
                Date = currentDate;
            }

            if (CustomerName.isEmpty() || Date == null || paidField.getText().isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("الرجاء ادخال البيانات");
                alert.showAndWait();
            } else {

                double paid = 0.0;
                paid = Double.parseDouble(paidField.getText());

                // Add Data To Report   
                String[] header = {"كود الصنف", "أسم الصــنف", "الكمية", "السعر", "الاجمالي"};

                ArrayList<String[]> itemlist = new ArrayList<>();
                ArrayList<Double> TOTAL = new ArrayList<>();

                for (int i = 0; i < tableList.size(); i++) {

                    invoice v = tableList.get(i);
                    String code = v.getCode();
                    String name = v.getName();
                    String amount = String.valueOf(v.getAmount());
                    String price = String.valueOf(v.getPrice());
                    String total = String.valueOf(v.getTotal());

                    String[] data = {code, name, amount, price, total};
                    itemlist.add(data);

                    double totalNUM = Double.parseDouble(total);
                    TOTAL.add(totalNUM);

                    String[] Amount = {code, amount};
                    amontlist.add(Amount);

                }

                double sum = 0;

                for (int i = 0; i < TOTAL.size(); i++) {

                    sum = sum + TOTAL.get(i);
                }

                // double tax = sum * .14;
                double tax = sum;
                double sumWithTAX = (sum);

                // String sumWithTAXString = sumWithTAX + "";
                String sumWithTAXString = new DecimalFormat("##.##").format(sumWithTAX);

                double remain = sumWithTAX - paid;
                remain = Double.parseDouble(new DecimalFormat("##.##").format(remain));

                //  SaveInvoice(Date ,CustomerName,sumWithTAX ,paid , remain);
                total_profit_field.getText();
                String inv_code_str = SaveInvoice(Date, CustomerName, sumWithTAXString, paid + "");
                int inv_code = Integer.parseInt(inv_code_str);

                String PDFNAME = "فاتورة رقم" + inv_code + ".pdf";

                invoiceReport invReport;
                invReport = new invoiceReport();
                invReport.openDocument(PDFNAME, invoicefileLocation, "فاتورة مبيعات");
                invReport.addCompanyInfo(Date, CustomerName, inv_code + "", user.getName());

                invReport.addTable(header, itemlist);

                invReport.addTotalTable(paid, sum, remain, sumWithTAXString);

                invReport.closeDocument();

                UpdateAmount();

                String path = invoicefileLocation + PDFNAME;

                custName.setText("");
                tableList.clear();

                Desktop.getDesktop().open(new File(path));

            }
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "save", ex);
        }

    }

    @FXML
    private void addItemToInvoice(MouseEvent event) {

        addItemInvoice();
    }

    private void getitemDataByName(String d) {

        addItemToInvoice.setDisable(false);
        try {
            connection = DbConnect.getConnect();
            query = "SELECT * FROM `items` where name =?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, d);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                itempriceField.setText(resultSet.getString("priceforcustomer"));
                total_amount.setText(resultSet.getString("amount"));
                itemCode = resultSet.getString("code");
                itemPriceCostField.setText(resultSet.getString("price"));

            }

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "getitemDataByName", ex);
        }
        checkAmount();

    }

    private void getitemDataByCode(String code) {

        addItemToInvoice.setDisable(false);
        barcodeField.clear();
        barcodeField.setText("");
        try {
            if (!code.equals("") && code != "") {
                //  connection = DbConnect.getConnect();
                query = "SELECT * FROM `items` where code =?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, code);
                resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText(null);
                    alert.setContentText("هذا الصنف غير مسجل في المخزن");

                    alert.show();
                } else {

                    //   while (resultSet.next()) {
                    itempriceField.setText(resultSet.getString("priceforcustomer"));
                    total_amount.setText(resultSet.getString("amount"));
                    itemCode = resultSet.getString("code");
                    itemName.setText(resultSet.getString("name"));
                    itemPriceCostField.setText(resultSet.getString("price"));
                    amountField.setText("1");

                    //   checkAmount();
                    //   amountField.requestFocus();
                    //  }
                }
                CURRENTBARCODE = code;

                amountField.requestFocus();
                amountField.setFocusTraversable(true);

            }
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "getitemDataByCode", ex);
        }

    }

    private String SaveInvoice(String date, String CustomerName, String Total, String paid) {

        int cus_id = getCustomerCode(CustomerName);
        BigInteger INV_Id = null;
        String inv_code = "";

        try {
            connection = DbConnect.getConnect();

            // save invoice details 
            DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
            String formattedDate = df.format(Calendar.getInstance().getTime());

            inv_code = USER_ID + "" + formattedDate + "" + getInvoiceDetailsNUM();

            query = "INSERT INTO `sales`(`inv_code`,`date`, `price`, `cus_id`,`user_id` ,`profit`) VALUES (?,?,?,?,?,?)";

            preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, inv_code);
            preparedStatement.setString(2, date);
            preparedStatement.setString(3, Total);
            //    preparedStatement.setString(4, paid);
            //  preparedStatement.setString(4, remain);
            preparedStatement.setInt(4, cus_id);
            preparedStatement.setInt(5, USER_ID);
            preparedStatement.setString(6, total_profit_field.getText());

            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next() && resultSet != null) {
                INV_Id = BigInteger.valueOf(resultSet.getLong(1));
            }

            preparedStatement.close();
            resultSet.close();

            query = "INSERT INTO `sales_pay`(`date`, `paid`, `cus_id`,`user_id`,`paid_type`,`inv_code`) VALUES (?,?,?,?,?,?)";
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, date);
            preparedStatement.setString(2, paid);
            preparedStatement.setInt(3, cus_id);
            preparedStatement.setInt(4, USER_ID);
            preparedStatement.setString(5, "كاش مع الفاتورة");
            preparedStatement.setString(6, INV_Id + "");

            preparedStatement.execute();

            preparedStatement.close();

            for (int i = 0; i < tableList.size(); i++) {
                query = "INSERT INTO `sales_details`(`inv_code`,`item_code`, `amount`, `price`,`cus_id`) VALUES (?,?,?,?,?)";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, inv_code);
                preparedStatement.setString(2, tableList.get(i).getCode());
                preparedStatement.setInt(3, tableList.get(i).getAmount());
                preparedStatement.setDouble(4, tableList.get(i).getPrice());
                preparedStatement.setInt(5, cus_id);
                preparedStatement.execute();
            }
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "SaveInvoice", ex);
        }

        return inv_code;
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

    private int getCustomerCode(String customerName) {

        int cus_code = 0;
        try {
            connection = DbConnect.getConnect();
            query = "SELECT id FROM `customers` where name =?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, customerName);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                cus_code = resultSet.getInt("id");

                return cus_code;

            }
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "getCustomerCode", ex);
        }
        return cus_code;
    }

    private int getInvoiceNUM() {

        int NUM = 0;
        try {

            connection = DbConnect.getConnect();
            //  query = "SELECT id FROM `store` where storeName =?";
            query = "SELECT MAX(id) FROM sales";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                NUM = resultSet.getInt("MAX(id)");

                return NUM;

            }
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "getInvoiceNUM", ex);
        }
        return NUM;

    }

    private int getInvoiceDetailsNUM() {

        int NUM = 0;
        try {

            connection = DbConnect.getConnect();
            //  query = "SELECT id FROM `store` where storeName =?";
            query = "SELECT MAX(id) FROM sales_details";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                NUM = resultSet.getInt("MAX(id)");

                return NUM;

            }
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "getInvoiceDetailsNUM", ex);
        }
        return NUM;

    }

    private void UpdateAmount() {

        int AM = 0;
        try {

            for (int i = 0; i < amontlist.size(); i++) {

                String d[] = amontlist.get(i);

                connection = DbConnect.getConnect();
                String CODE = d[0];

                query = "select amount from items where code = '" + CODE + "'";
                preparedStatement = connection.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String g = resultSet.getString("amount");
                    AM = Integer.parseInt(g);
                }

                int APAID = Integer.parseInt(d[1]);
                int AMOUNT = AM - APAID;

                query = "UPDATE `items` SET "
                        + "`amount`= '" + AMOUNT + "' WHERE code = '" + d[0] + "'";

                preparedStatement = connection.prepareStatement(query);
                preparedStatement.execute();
            }
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "UpdateAmount", ex);
        }
    }

    private void codeSearch(MouseEvent event) throws SQLException {

        String code = barcodeField.getText();
        getitemDataByCode(code);

    }

    @FXML
    private void searchCustAuto(KeyEvent event) {
        TextFields.bindAutoCompletion(custName, item);
    }

    @FXML
    private void searchItemAuto(KeyEvent event) {

        TextFields.bindAutoCompletion(itemName, item2);
    }

    private void codeSearchWithBarcode(KeyEvent event) {

        //    getitemDataByCode(barcodeField.getText());
        try {

            addItemToInvoice.setDisable(false);
            // getitemDataByCode(barcodeField.getText());

            /*
             Timer timer = new Timer();
             timer.scheduleAtFixedRate(new TimerTask() {
             @Override
             public void run() {
             Platform.runLater(() -> {
             try {
             getitemDataByCode(barcodeField.getText());

             } catch (SQLException ex) {
             Logger.getLogger(NewInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
             }

             });
             }
             }, 1000, 1000);

             */
            getitemDataByCode(barcodeField.getText());
            /*
             timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
             try {
             getitemDataByCode(barcodeField.getText());

             } catch (SQLException ex) {
             Logger.getLogger(NewInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
             }

             if (CURRENTBARCODE.equals(barcodeField.getText())) {
             timeline.stop();
             return;
             }
             }));
             timeline.setCycleCount(Animation.INDEFINITE);
             timeline.play();

             */

            /*
             query = "SELECT * FROM `items` where code =?";
             preparedStatement = connection.prepareStatement(query);
             preparedStatement.setString(1, barcodeField.getText());
             resultSet = preparedStatement.executeQuery();

             //     if(!resultSet.next()){
             //        Alert alert = new Alert(Alert.AlertType.WARNING);
             //        alert.setHeaderText(null);
             //       alert.setContentText("هذا الصنف غير مسجل في المخزن");
             //       alert.showAndWait();
             //   }else{
         
             while (resultSet.next()) {

             itempriceField.setText(resultSet.getString("priceforcustomer"));
             total_amount.setText(resultSet.getString("amount"));
             itemCode = resultSet.getString("code");
             itemName.setText(resultSet.getString("name"));
             itemPriceCostField.setText(resultSet.getString("price"));

             checkAmount();

             //  
             }
             
             //   amountField.requestFocus();

             */
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "codeSearchWithBarcode", ex);
        }
    }

    private void checkAmount() {

        int amount = Integer.parseInt(total_amount.getText().toString());
        if (amount <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("هذا الصنف غير متاح في المخزن");
            alert.showAndWait();

            addItemToInvoice.setDisable(true);
            barcodeField.setText("");
            itemName.setText("");
            itempriceField.setText("");
        }

    }

    public void addItemInvoice() {

        /*
         if (!isItemAdded(itemCode)) {
         Alert alert = new Alert(Alert.AlertType.WARNING);
         alert.setHeaderText(null);
         alert.setContentText("تم اضافة هذا الصنف من قبل");
         alert.showAndWait();
         barcodeField.setText("");
         itemName.setText("");
         itempriceField.setText("");
         barcodeField.requestFocus();
         return;
         }
         */
        int Stroreamount = Integer.parseInt(total_amount.getText());

        if (Stroreamount <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("هذا الصنف غير متاح في المخزن");
            alert.showAndWait();

            barcodeField.setText("");
            itemName.setText("");
            itempriceField.setText("");
            barcodeField.requestFocus();
        } else if (Stroreamount < Integer.parseInt(amountField.getText())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("هذه الكمية غير متاحه في المخزن");
            alert.showAndWait();

        } else {

            double price = Double.parseDouble(itempriceField.getText());
            int amount = Integer.parseInt(amountField.getText());
            double total = price * amount;
            String name = itemName.getText();

            double itemPriceCost = Double.parseDouble(itemPriceCostField.getText());
            double totalCost = itemPriceCost * amount;
            double profit = total - totalCost;

            invoice inv = new invoice(itemCode, amount, price, total, name, profit);

            tableList.add(inv);

            itemCodeTAB.setCellValueFactory(new PropertyValueFactory<>("code"));
            amountTAB.setCellValueFactory(new PropertyValueFactory<>("amount"));
            priceTAB.setCellValueFactory(new PropertyValueFactory<>("price"));
            totalTAB.setCellValueFactory(new PropertyValueFactory<>("total"));
            itemnameTAB.setCellValueFactory(new PropertyValueFactory<>("name"));

            itemCodeTAB.setStyle("-fx-alignment: CENTER;");
            amountTAB.setStyle("-fx-alignment: CENTER;-fx-font-size:18px;-fx-font-weight:bold;");
            priceTAB.setStyle("-fx-alignment: CENTER;");
            totalTAB.setStyle("-fx-alignment: CENTER;-fx-font-size:18px;-fx-font-weight:bold;");
            itemnameTAB.setStyle("-fx-alignment: CENTER;-fx-font-size:18px;-fx-font-weight:bold;");

            itemName.setText("");

            itempriceField.setText("");
            amountField.setText("");
            //codeField.setText("");
            total_amount.setText("");

            barcodeField.clear();
            barcodeField.setFocusTraversable(true);
            barcodeField.requestFocus();

            //add cell of button edit 
            Callback<TableColumn<invoice, String>, TableCell<invoice, String>> cellFoctory = (TableColumn<invoice, String> param) -> {
                // make cell containing buttons
                final TableCell<invoice, String> cell = new TableCell<invoice, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        //that cell created only on non-empty rows
                        if (empty) {
                            setGraphic(null);
                            setText(null);

                        } else {

                            FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                            deleteIcon.setStyle(
                                    " -fx-cursor: hand ;"
                                    + "-glyph-size:28px;"
                                    + "-fx-fill:#ff1744;"
                            );

                            deleteIcon.setOnMouseClicked((MouseEvent event) -> {

                                invoice selectedItem = invoiceTable.getSelectionModel().getSelectedItem();

                                invoiceTable.getItems().remove(selectedItem);

                                calcInvTotal();

                            });

                            HBox managebtn = new HBox(deleteIcon);
                            managebtn.setStyle("-fx-alignment:center");
                            HBox.setMargin(deleteIcon, new javafx.geometry.Insets(2, 2, 0, 3));

                            setGraphic(managebtn);

                            setText(null);

                        }
                    }

                };

                return cell;
            };
            editCol.setCellFactory(cellFoctory);

            invoiceTable.setItems(tableList);

            calcInvTotal();

        }

    }

    public boolean isItemAdded(String code) {

        String stringCode = code.toLowerCase();
        boolean result = tableList.filtered(it -> it.getCode().toLowerCase().contains(stringCode)).isEmpty();
        return result;
    }

    private void calcInvTotal() {
        double Total_Profit = 0.00;
        ArrayList<Double> TOT = new ArrayList<>();

        for (int i = 0; i < tableList.size(); i++) {

            invoice v = tableList.get(i);

            String TotaL = String.valueOf(v.getTotal());
            double totalNUM = Double.parseDouble(TotaL);
            TOT.add(totalNUM);

            Total_Profit = Total_Profit + v.getProfit();

        }

        double INVTOtal = 0;
        for (int i = 0; i < TOT.size(); i++) {

            INVTOtal = INVTOtal + TOT.get(i);

        }

        // String INV = String.valueOf(INVTOtal);
        String INV = decimal.format(INVTOtal);

        invTotal.setText(INV);
        double Tax = INVTOtal * .14;
        double totalTax = INVTOtal;

        // String TAX = String.valueOf(totalTax);
        String TAX = decimal.format(totalTax);

        invTotalTax.setText(TAX);

        total_profit_field.setText(decimal.format(Total_Profit));

        invTotal.setEditable(false);

    }

    private void loadCutomerCombo() {

        try {

            connection = DbConnect.getConnect();

            query = "SELECT name FROM `customers`";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                item.add(resultSet.getString("name"));

            }
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "loadCutomerCombo", ex);
        }

    }

    private void loadItemCombo() {

        //  itemnameField.getSelectionModel().clearSelection();
        //  itemnameField.getItems().clear();
        try {

            item2.clear();

            connection = DbConnect.getConnect();

            query = "SELECT name FROM `items`";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                item2.add(resultSet.getString("name"));

            }
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "loadItemCombo", ex);
        }
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
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "loadStoreCombo", ex);
        }

    }

    @FXML
    private void addToInvoice(ActionEvent event) {
        addItemInvoice();
    }

    void setTextField(int id, int role) {
        USER_ID = id;
        ROLE = role;
    }

    @FXML
    private void codeSearchWithBarcode(ActionEvent event) {
        getitemDataByCode(barcodeField.getText());

    }

}
