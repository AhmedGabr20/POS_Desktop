package controllers;

import PDF.salesReport;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import helper.DbConnect;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import models.User;
import models.invoice;
import models.sales;

public class CustomerProfileController implements Initializable {

    @FXML
    private TextField search;
    @FXML
    private Text customer_id_field;
    @FXML
    private Text customerNameField;
    @FXML
    private TableView<sales> salesTable;
    @FXML
    private TableColumn<sales, String> editCol;
    //private TableColumn<String, String> remainCol;
    @FXML
    private TableColumn<String, String> paidCol;
    @FXML
    private TableColumn<String, String> priceCol;
    @FXML
    private TableColumn<String, String> dateCol;
    //private TableColumn<String, String> userCol;
    @FXML
    private TableColumn<String, String> codeCol;
    @FXML
    private DatePicker to_date;
    @FXML
    private DatePicker from_date;
    @FXML
    private TextField REMAIN;
    @FXML
    private TextField PAID;
    @FXML
    private TextField TOTAL;
    @FXML
    private Text all_remain_text;

    DecimalFormat decimal = new DecimalFormat("#.##");

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    sales sale = null;

    PreparedStatement preparedStatement2 = null;
    ResultSet resultSet2 = null;

    ObservableList<sales> salesList = FXCollections.observableArrayList();
    ObservableList<invoice> detailsList = FXCollections.observableArrayList();
    ObservableList<sales> payList = FXCollections.observableArrayList();
    ObservableList<sales> UserPayList = FXCollections.observableArrayList();

    long millis = System.currentTimeMillis();
    java.sql.Date CDate = new java.sql.Date(millis);
    String currentDate = CDate + "";

    STATICDATA pdfPath = new STATICDATA();

    //String invoicefileLocation;
    String salesfileLocation;

    String FROM_DATE;
    String TO_DATE;
    int USER_ID;
    int ROLE;

    double all_total, all_paid, all_remain;

    ViewCustomerController viewCustomerController = new ViewCustomerController();

//    private TableColumn<String, String> paidTypeCol;
    @FXML
    private TableColumn<String, String> due_dateCol;
    @FXML
    private TableColumn<String, String> remainCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        to_date.setOnAction(e -> {

            FROM_DATE = from_date.getValue() + "";
            TO_DATE = to_date.getValue() + "";

            loadDate();

        });

        from_date.setOnAction(e -> {

            to_date.setValue(null);
        });

    }

    @FXML
    private void refresh() {
        all_total = 0.00;
        all_remain = 0.00;
        all_paid = 0.00;
        //   try {
        salesList.clear();

        getAllRemain();

        query = "select sales.id as PK , sales.inv_code as id , sales.date , sales.price as price , sales.paid_type , sales.due_date ,customers.id as cus_id, customers.name ,user.name as username "
                + "from sales , customers ,user where sales.cus_id = customers.id AND sales.user_id= user.id AND customers.id=? "
                + "and sales.date between ? and ? order by date";

        getData(query);

        query = "select outsales.id as PK , CONCAT(outsales.code , '-OT') as id , outsales.date , outsales.price as price , outsales.paid_type , outsales.due_date ,customers.id as cus_id, customers.name ,user.name as username "
                + "from outsales , customers ,user where outsales.cus_id = customers.id AND outsales.user_id= user.id AND customers.id=? "
                + "and outsales.date between ? and ? order by date";

        getData(query);

        salesTable.setItems(salesList);

        //  all_total += total ;
        //  all_paid += paid ;
        //  all_remain += remain ;
    }

    private void getData(String query) {

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(customer_id_field.getText()));
            preparedStatement.setString(2, FROM_DATE);
            preparedStatement.setString(3, TO_DATE);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String total_string = resultSet.getString("price");
                double total = resultSet.getDouble("price");
                
                String PAID_TYPE = resultSet.getString("paid_type");
                // sum pay from sales_pay 
                String sumPaid = sumPaidM(resultSet.getString("id"), resultSet.getInt("PK"), resultSet.getInt("cus_id"));
                double totalPaid = 0;
                if (sumPaid == null) {
                    totalPaid = 0;
                } else {
                    totalPaid = Double.parseDouble(sumPaid);
                }
                double remain = total - totalPaid;
                String r = decimal.format(remain);
                double REMAIN = Double.parseDouble(r);

                salesList.add(new sales(
                        resultSet.getInt("PK"),
                        resultSet.getString("id"),
                        total_string,
                        totalPaid,
                        REMAIN,
                        0,
                        resultSet.getString("name"),
                        // Integer.parseInt(customer_id_field.getText()),
                        resultSet.getString("date"),
                        resultSet.getString("username"),
                        PAID_TYPE,
                        resultSet.getString("due_date"),
                        resultSet.getInt("cus_id")
                ));

                all_total += total;
                all_paid += totalPaid;
                all_remain += remain;

            }

            TOTAL.setText(decimal.format(all_total));
            PAID.setText(decimal.format(all_paid));
            REMAIN.setText(decimal.format(all_remain));

            preparedStatement.close();
            resultSet.close();

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "getData", ex);
        }
    }

    public void loadDate() {

        connection = DbConnect.getConnect();
        refresh();

        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("total_txt"));
        paidCol.setCellValueFactory(new PropertyValueFactory<>("paid"));
        remainCol.setCellValueFactory(new PropertyValueFactory<>("remain"));
        due_dateCol.setCellValueFactory(new PropertyValueFactory<>("due_date"));

        codeCol.setStyle("-fx-alignment: CENTER;");
        dateCol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");
        priceCol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");
        paidCol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");
        remainCol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");
        due_dateCol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");

        //add cell of button edit 
        Callback<TableColumn<sales, String>, TableCell<sales, String>> cellFoctory = (TableColumn<sales, String> param) -> {
            // make cell containing buttons
            final TableCell<sales, String> cell = new TableCell<sales, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    //that cell created only on non-empty rows
                    if (empty) {
                        setGraphic(null);
                        setText(null);

                    } else {

                        FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                        FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);
                        FontAwesomeIconView open = new FontAwesomeIconView(FontAwesomeIcon.LOCATION_ARROW);

                        if (ROLE == 0) {
                            deleteIcon.setVisible(false);

                        }

                        deleteIcon.setStyle(
                                " -fx-cursor: hand ;"
                                + "-glyph-size:28px;"
                                + "-fx-fill:#ff1744;"
                        );
                        editIcon.setStyle(
                                " -fx-cursor: hand ;"
                                + "-glyph-size:28px;"
                                + "-fx-fill:#00E676;"
                        );
                        open.setStyle(
                                " -fx-cursor: hand ;"
                                + "-glyph-size:28px;"
                                + "-fx-fill:#000000;"
                        );

                        deleteIcon.setOnMouseClicked((MouseEvent event) -> {

                            sale = salesTable.getSelectionModel().getSelectedItem();

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("هل انت متاكد من مسح الفاتوره");
                            // alert.setContentText("تأكيد مسح العميل");
                            alert.getButtonTypes().addAll(ButtonType.CANCEL);

                            Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                            Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
                            okButton.setText("موافق");
                            cancelButton.setText("إلغاء");

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    try {
                                        event.consume();

                                        if (sale.getCode().contains("OT")) {
                                            String cod[] = sale.getCode().split("-", 2);
                                            query = "DELETE FROM `outsales` WHERE id  =" + sale.getId();
                                        } else {
                                            query = "DELETE FROM `sales` WHERE id  =" + sale.getId();
                                        }

                                        connection = DbConnect.getConnect();
                                        preparedStatement = connection.prepareStatement(query);
                                        int result = preparedStatement.executeUpdate();
                                        loadDate();

                                        if (result != 0) {
                                            Alert alert2 = new Alert(Alert.AlertType.WARNING);
                                            alert.setHeaderText(null);
                                            alert.setContentText("تم الحذف");
                                            alert.showAndWait();
                                        } else {
                                            Alert alert2 = new Alert(Alert.AlertType.WARNING);
                                            alert.setHeaderText(null);
                                            alert.setContentText("حدث مشكلة في الحذف");
                                            alert.showAndWait();
                                        }
                                    } catch (SQLException ex) {
                                        Logger.getLogger(CustomerProfileController.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                } else if (response == ButtonType.CANCEL) {

                                }
                            });

                        });
                        editIcon.setOnMouseClicked((MouseEvent event) -> {

                            //    Stage stagemain = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            //    stagemain.close();
                            // close(event);
                            sale = salesTable.getSelectionModel().getSelectedItem();
                            if (sale.getTotal_txt().equals("سداد") || sale.getTotal_txt() == "سداد") {
                                editIcon.setVisible(false);
                                return;
                            }
                            FXMLLoader loader = new FXMLLoader();
                            loader.setLocation(getClass().getResource("/tableView/editSales.fxml"));
                            try {
                                loader.load();
                            } catch (IOException ex) {
                                Logger.getLogger(ViewSalesController.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            EditSalesController editSalesController = loader.getController();
                            editSalesController.setTextField(USER_ID, ROLE, sale, true, customerNameField.getText(), Integer.parseInt(customer_id_field.getText()));
                            Parent parent = loader.getRoot();
                            Stage stage = new Stage();
                            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
                            stage.setScene(new Scene(parent));
                            //    stage.initStyle(StageStyle.UTILITY);
                            stage.show();

                        });

                        open.setOnMouseClicked((MouseEvent event) -> {
                            sale = salesTable.getSelectionModel().getSelectedItem();
                            close(event);
                            openDetailsPage(USER_ID, ROLE, sale);
                        });
                        HBox managebtn = new HBox(deleteIcon, editIcon, open);
                        managebtn.setStyle("-fx-alignment:center");
                        HBox.setMargin(deleteIcon, new javafx.geometry.Insets(2, 2, 0, 3));
                        HBox.setMargin(editIcon, new javafx.geometry.Insets(2, 2, 0, 3));
                        HBox.setMargin(open, new javafx.geometry.Insets(2, 2, 0, 3));

                        setGraphic(managebtn);

                        setText(null);

                    }
                }

            };

            return cell;
        };
        editCol.setCellFactory(cellFoctory);

        if (ROLE == 0) {
            //  editCol.setVisible(false);
        }

        salesTable.setItems(salesList);

    }

    @FXML
    private void save(MouseEvent event){

        salesReport saleReport;
        saleReport = new salesReport();
        try {
        // Add Data To Report   
        String[] header = {"كود الفاتورة", "محرر الفاتورة", "التاريخ", "تاريخ الاستحقاق", "الاجمالي"};

        ArrayList<String[]> salelist = new ArrayList<>();

        String CustomerName = customerNameField.getText();

        for (int i = 0; i < salesList.size(); i++) {

            sales s = salesList.get(i);
            String SaleId = s.getCode();
            String customerName = s.getUserName();
            String InvDATE = s.getDate();
            String Total = s.getTotal_txt();
            String paid = String.valueOf(s.getPaid());
            String remain = String.valueOf(s.getRemain());
            String userName = s.getUserName();
            String dueDate = s.getDue_date();

            String[] data = {SaleId, customerName, InvDATE, dueDate, Total};
            salelist.add(data);

        }

        //  int remain = sum - paid ;
        LocalDate date = LocalDate.now();
        String Date = date + "";

        String userName = customerNameField.getText();
        // String userSalesLocation = salesfileLocation+userName;

        String PDFNAME = "مبيعات " + userName + currentDate + ".pdf";

        saleReport.openDocument(PDFNAME, salesfileLocation);
        saleReport.addTopHeader(" سجل مبيعات ", "في الفترة من :    " + FROM_DATE, "   إلـــي:   " + TO_DATE);
        saleReport.addCompanyInfo(Date, CustomerName, salelist.size());
        saleReport.addTable(header, salelist);

        saleReport.addTotalTable(PAID.getText(), TOTAL.getText(), REMAIN.getText());

        //
        getUserSalesData();
        String[] header2 = {"المستلم", "تاريخ اللإستلام", "المدفوع", "تفاصيل السيداد"};

        ArrayList<String[]> payslist = new ArrayList<>();
        //    ArrayList<Double> TOTAL = new ArrayList<>();

        for (int i = 0; i < UserPayList.size(); i++) {

            sales v = UserPayList.get(i);
            String userN = v.getUserName();
            String invdate = v.getDate();
            String paid = String.valueOf(v.getPaid());
            String paidType = v.getPaid_type();

            String[] data = {userN, invdate, paid, paidType};
            payslist.add(data);

        }

        saleReport.payTable(header2, payslist, "جدول السداد");

        saleReport.closeDocument();

            Desktop.getDesktop().open(new File(salesfileLocation));
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "save", ex);
        }

    }

    @FXML
    private void searchMeal(KeyEvent event) {

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<sales> filteredData = new FilteredList<>(salesList, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(cust -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();
                String code = cust.getCode() + "";

                if (cust.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (code.contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<sales> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(salesTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        salesTable.setItems(sortedData);

    }

    public void setTextField(int userId, int role, int cusId, String name) {

        USER_ID = userId;
        ROLE = role;
        customer_id_field.setText(cusId + "");
        customerNameField.setText(name);
        customer_id_field.setVisible(false);

        salesfileLocation = pdfPath.PDF_MAIN_FOLDER + pdfPath.PDF_CUSTOMER_SALES_FOLDER + "//" + name;

        from_date.setValue(LocalDate.now().minusYears(5));
        to_date.setValue(LocalDate.now());

        FROM_DATE = from_date.getValue() + "";
        TO_DATE = to_date.getValue() + "";

        loadDate();

        TOTAL.setEditable(false);
        PAID.setEditable(false);
        REMAIN.setEditable(false);

    }

    public void getAllRemain() {
        try {
            double remain_text_all = 0.00;
            connection = DbConnect.getConnect();
            query = "select sum(price) as total from sales  where cus_id = ? ";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(customer_id_field.getText()));
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                remain_text_all = resultSet.getDouble("total");
            }

            preparedStatement.close();
            resultSet.close();

            query = "select sum(paid) as paid from sales_pay  where cus_id = ? ";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(customer_id_field.getText()));
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                double paid = resultSet.getDouble("paid");
                if (paid != 0) {
                    remain_text_all = remain_text_all - paid;
                }

            }

            preparedStatement.close();
            resultSet.close();

            query = "select sum(price) as total from outsales  where cus_id = ? ";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(customer_id_field.getText()));
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                remain_text_all += resultSet.getDouble("total");
            }

            preparedStatement.close();
            resultSet.close();

            query = "select sum(paid) as paid from outsales_pay  where cus_id = ? ";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(customer_id_field.getText()));
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                double paid = resultSet.getDouble("paid");
                if (paid != 0) {
                    remain_text_all = remain_text_all - paid;
                }

            }

            all_remain_text.setText(decimal.format(remain_text_all));

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "getAllRemain", ex);
        }
    }

    @FXML
    private void openAddInv(MouseEvent event) {

        close(event);
        openAddOutInv(Integer.parseInt(customer_id_field.getText()), customerNameField.getText(), USER_ID, ROLE);
    }

    public void openAddOutInv(int cusId, String customerName, int userId, int role) {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/addOutInv.fxml"));
            loader.load();

            AddOutSalesController addOutSalesController = loader.getController();
            addOutSalesController.setTextField(cusId, customerName, userId, role);
            Parent parent = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.setTitle("العميل:  " + customerName);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    viewCustomerController.openCustomerProfile(userId, role, cusId, customerName);
                }
            });
            stage.show();
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openAddOutInv", ex);
        }

    }


    private void close(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public String sumPaidM(String code, int id, int cus_id) {
        String total_paid = "0";
        try {
            if (code.contains("OT")) {
                String cod[] = code.split("-", 2);
                code = cod[0];
                query = "select sum(paid) as total from outSales_pay where inv_code =? AND cus_id=? ";
            } else {
                query = "select sum(paid) as total from sales_pay where inv_code =? AND cus_id=? ";
            }
            //    connection = DbConnect.getConnect();
            preparedStatement2 = connection.prepareStatement(query);
            preparedStatement2.setInt(1, id);
            preparedStatement2.setInt(2, cus_id);
            resultSet2 = preparedStatement2.executeQuery();

            while (resultSet2.next()) {
                total_paid = resultSet2.getString("total");
            }
            preparedStatement2.close();
            resultSet2.close();

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "sumPaidM", ex);
        }

        return total_paid;
    }

    public void openDetailsPage(int user_id, int role, sales sale) {

        //  sale.getId(), sale.getCus_id(), sale.getCode(), sale.getTotal_txt(), sale.getPaid()
        loadInvoiceData(sale.getId(), sale.getCus_id(), sale.getCode());
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/editInvoice.fxml"));
            loader.load();

            editInvoiceController editInvoiceCon = loader.getController();
            editInvoiceCon.setTextField(user_id, role);
            editInvoiceCon.loadInvoiceDataT(detailsList, payList, sale);
            Scene scene = new Scene(loader.getRoot());
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    ViewCustomerController v = new ViewCustomerController();
                    v.openCustomerProfile(user_id, role, sale.getCus_id(), sale.getCustomerName());
                }
            });
            stage.show();
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openDetailsPage", ex);
        }
    }

    public void loadInvoiceData(int salesId, int cus_id, String code) {
        String INV_CODE = "0";
        detailsList.clear();
        payList.clear();
        try {
            if (code.contains("OT")) {
                String cod[] = code.split("-", 2);
                INV_CODE = cod[0];
            } else {
                INV_CODE = code;
            }

            // GET INV DETAILS 
            if (!code.contains("OT")) {
                query = "SELECT sales_details.id , sales_details.item_code ,sales_details.amount ,sales_details.price, "
                        + "items.name FROM `sales_details` , `items` where sales_details.inv_code =? AND sales_details.cus_id=? "
                        + "AND sales_details.item_code = items.code ";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, INV_CODE);
                preparedStatement.setInt(2, cus_id);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String in_id = resultSet.getString("id");
                    String item_code = resultSet.getString("item_code");
                    String item_name = resultSet.getString("name");
                    int item_amount = resultSet.getInt("amount");
                    double item_price = resultSet.getDouble("price");
                    double item_total = item_amount * item_price;
                    detailsList.add(new invoice(item_code, item_amount, item_price, item_total, item_name));
                }

                preparedStatement.close();
                resultSet.close();

                // get sales pay
                query = "select sales_pay.id, sales_pay.date , sales_pay.paid_type , sales_pay.paid ,customers.id as cus_id, customers.name ,user.name as username "
                        + "from sales_pay , customers ,user where sales_pay.cus_id = customers.id AND sales_pay.user_id= user.id AND customers.id=? AND sales_pay.inv_code=? ";
                getPaySalesData(query, cus_id, salesId, false);
            } else {
                query = "select outsales_pay.id, outsales_pay.date , outsales_pay.paid_type , outsales_pay.paid ,customers.id as cus_id, customers.name ,user.name as username "
                        + "from outsales_pay , customers ,user where outsales_pay.cus_id = customers.id AND outsales_pay.user_id= user.id AND customers.id=? AND outsales_pay.inv_code=? ";
                getPaySalesData(query, cus_id, salesId, true);
            }

        } catch (SQLException ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "loadInvoiceData", ex);
        }

    }

    private void getPaySalesData(String query, int cus_id, int INV_ID, boolean out) {

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, cus_id);
            preparedStatement.setInt(2, INV_ID);
            //    preparedStatement.setString(3, TO_DATE);
            resultSet = preparedStatement.executeQuery();

            if (sale.getFirstPaid() != 0) {

                payList.add(new sales(0, INV_ID + "", "0", sale.getFirstPaid(), 0, sale.getFirstPaid(), sale.getCustomerName(), sale.getDate(), sale.getUserName(), sale.getPaid_type(), "", cus_id));
            }
            while (resultSet.next()) {
                double paid = resultSet.getDouble("paid");
                // sum pay from sales_pay 
                payList.add(new sales(
                        resultSet.getInt("id"),
                        INV_ID + "",
                        "0",
                        paid,
                        0,
                        0,
                        resultSet.getString("name"),
                        resultSet.getString("date"),
                        resultSet.getString("username"),
                        resultSet.getString("paid_type"),
                        "",
                        cus_id
                ));
            }
            preparedStatement.close();
            resultSet.close();
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "getPaySalesData", ex);
        }
    }

    private void getUserSalesData() {
        UserPayList.clear();

       
        // get sales pay
                query = "select sales_pay.id, sales_pay.date , sales_pay.paid_type , sales_pay.paid ,customers.id as cus_id, customers.name ,user.name as username "
                        + "from sales_pay , customers ,user where sales_pay.cus_id = customers.id AND sales_pay.user_id= user.id AND customers.id=?  "
                        + "and sales_pay.date between ? and ? order by sales_pay.date";
                getUserPaySalesData(query);

                query = "select outsales_pay.id, outsales_pay.date , outsales_pay.paid_type , outsales_pay.paid ,customers.id as cus_id, customers.name ,user.name as username "
                        + "from outsales_pay , customers ,user where outsales_pay.cus_id = customers.id AND outsales_pay.user_id= user.id AND customers.id=?  "
                        + "and outsales_pay.date between ? and ? order by outsales_pay.date";
                getUserPaySalesData(query);
        
    }
    
    private void getUserPaySalesData(String query) {

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, customer_id_field.getText());
            preparedStatement.setString(2, FROM_DATE);
            preparedStatement.setString(3, TO_DATE);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                double paid = resultSet.getDouble("paid");
                // sum pay from sales_pay 
                UserPayList.add(new sales(
                        resultSet.getInt("id"),
                        "",
                        "0",
                        paid,
                        0,
                        0,
                        resultSet.getString("name"),
                        resultSet.getString("date"),
                        resultSet.getString("username"),
                        resultSet.getString("paid_type"),
                        "",
                        0
                ));
            }
            preparedStatement.close();
            resultSet.close();

            

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "getUserPaySalesData", ex);
        }
    }

}
