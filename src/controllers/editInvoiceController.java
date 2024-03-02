package controllers;

import PDF.invoiceReport;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import helper.DbConnect;
import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import models.invoice;
import models.sales;

public class editInvoiceController implements Initializable {

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
    @FXML
    private Text invoce_date;
    @FXML
    private Text custName;
    @FXML
    private TextField invTotal;
    @FXML
    private TextField paidField;
    @FXML
    private Button addItemToInvoice1;
    @FXML
    private TextField amountField1;
    @FXML
    private Text userName;
    @FXML
    private VBox editBox;
    @FXML
    private TableView<sales> payTable;
    @FXML
    private TableColumn<String, String> paidTypeCol;
    @FXML
    private TableColumn<String, String> paidCol;
    @FXML
    private TableColumn<String, String> dateCol;
    @FXML
    private TableColumn<String, String> userCol;
    @FXML
    private TableColumn<sales, String> payEditCol;
    @FXML
    private Text total_txt;
    @FXML
    private Text paid_txt;
    @FXML
    private Text remian_txt;
    @FXML
    private Label inv_code_txt;

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    ResultSet resultSet2 = null;

    ObservableList<invoice> tableList = FXCollections.observableArrayList();
    ObservableList<sales> payList = FXCollections.observableArrayList();
    sales pay = null;

    invoiceReport invReport;

    STATICDATA pdfPath = new STATICDATA();

    String invoicefileLocation;

    DecimalFormat decimal = new DecimalFormat("#.##");

    int USER_ID, ROLE;
    @FXML
    private HBox inv_box;
    @FXML
    private VBox edit_totalBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        invoicefileLocation = pdfPath.PDF_MAIN_FOLDER + pdfPath.PDF_INV_FOLDER + "//";

        editBox.setVisible(false);
        edit_totalBox.setVisible(false);
    }

    @FXML
    private void save(MouseEvent event) {

        printInvoice();
    }

    private void printInvoice() {
        try {

            String CustomerName = custName.getText();
            String UserName = userName.getText();
            String Date = invoce_date.getText();

            String inv_total = total_txt.getText();
            String inv_paid = paid_txt.getText();
            String inv_remain = remian_txt.getText();
            String inv_code = inv_code_txt.getText();

            // Add Data To Report
            String[] header = {"كود الصنف", "أسم الصــنف", "الكمية", "السعر", "الاجمالي"};

            ArrayList<String[]> itemlist = new ArrayList<>();
            //    ArrayList<Double> TOTAL = new ArrayList<>();

            for (int i = 0; i < tableList.size(); i++) {

                invoice v = tableList.get(i);
                String code = v.getCode();
                String name = v.getName();
                String amount = String.valueOf(v.getAmount());
                String price = String.valueOf(v.getPrice());
                String total = String.valueOf(v.getTotal());

                String[] data = {code, name, amount, price, total};
                itemlist.add(data);

                //    double totalNUM = Double.parseDouble(total);
                //    TOTAL.add(totalNUM);
                //    String[] Amount = {code, amount};
            }

            //    double sum = 0;
            //    for (int i = 0; i < TOTAL.size(); i++) {
            //        sum = sum + TOTAL.get(i);
            //    }
            // double tax = sum * .14;
            //    double tax = sum;
            //    double sumWithTAX = (sum);
            // String sumWithTAXString = sumWithTAX + "";
            //    String sumWithTAXString = new DecimalFormat("##.##").format(sumWithTAX);
            //    double remain = sumWithTAX - paid;
            //    remain = Double.parseDouble(new DecimalFormat("##.##").format(remain));
            //  SaveInvoice(Date ,CustomerName,sumWithTAX ,paid , remain);
            //    total_profit_field.getText();
            String PDFNAME = "نسخة " + "فاتورة رقم" + inv_code + ".pdf";

            invReport = new invoiceReport();
            invReport.openDocument(PDFNAME, invoicefileLocation, "فاتورة مبيعات (نسخة)");
            invReport.addCompanyInfo(Date, CustomerName, inv_code + "", UserName);

            invReport.addTable(header, itemlist);

            invReport.addTotalTable(Double.parseDouble(inv_paid), Double.parseDouble(inv_total), Double.parseDouble(inv_remain), inv_total);

            invReport.closeDocument();

            String path = invoicefileLocation + PDFNAME;

            Desktop.getDesktop().open(new File(path));

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "printInvoice", ex);
        }

    }

    private void printInvoiceAndPay() {
        try {

            String CustomerName = custName.getText();
            String UserName = userName.getText();
            String Date = invoce_date.getText();

            String inv_total = total_txt.getText();
            String inv_paid = paid_txt.getText();
            String inv_remain = remian_txt.getText();
            String inv_code = inv_code_txt.getText();

            String PDFNAME = "";

            invReport = new invoiceReport();
            PDFNAME = "تفاصيبل سداد  " + "فاتورة رقم" + inv_code + ".pdf";

            invReport.openDocument(PDFNAME, invoicefileLocation, "فاتورة مبيعات بالسداد (نسخة)");
            invReport.addCompanyInfo(Date, CustomerName, inv_code, UserName);

            if (tableList.size() != 0) {
                // Add Data To Report
                String[] header = {"كود الصنف", "أسم الصــنف", "الكمية", "السعر", "الاجمالي"};

                ArrayList<String[]> itemlist = new ArrayList<>();
                //    ArrayList<Double> TOTAL = new ArrayList<>();

                for (int i = 0; i < tableList.size(); i++) {

                    invoice v = tableList.get(i);
                    String code = v.getCode();
                    String name = v.getName();
                    String amount = String.valueOf(v.getAmount());
                    String price = String.valueOf(v.getPrice());
                    String total = String.valueOf(v.getTotal());

                    String[] data = {code, name, amount, price, total};
                    itemlist.add(data);

                }

                invReport.addTable(header, itemlist);

            }

            invReport.addTotalTable(Double.parseDouble(inv_paid), Double.parseDouble(inv_total), Double.parseDouble(inv_remain), inv_total);
            String path = invoicefileLocation + PDFNAME;

            // Add Data To Report
            String[] header2 = {"المستلم", "تاريخ اللإستلام", "المدفوع", "تفاصيل السيداد"};

            ArrayList<String[]> payslist = new ArrayList<>();
            //    ArrayList<Double> TOTAL = new ArrayList<>();

            for (int i = 0; i < payList.size(); i++) {

                sales v = payList.get(i);
                String userName = v.getUserName();
                String invdate = v.getDate();
                String paid = String.valueOf(v.getPaid());
                String paidType = v.getPaid_type();

                String[] data = {userName, invdate, paid, paidType};
                payslist.add(data);

            }

            invReport.payTable(header2, payslist, "جدول السداد");

            invReport.closeDocument();

            Desktop.getDesktop().open(new File(path));

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "printInvoiceAndPay", ex);
        }
    }

    @FXML
    private void addItemToInvoice(MouseEvent event) {
    }

    @FXML
    private void addToInvoice(ActionEvent event) {
    }

    public void loadInvoiceDataT(ObservableList<invoice> detailsList, ObservableList<sales> payList, sales sale) {

        // sale.getId(), sale.getCus_id(), sale.getCode(), sale.getTotal_txt(), sale.getPaid()
        total_txt.setText(sale.getTotal_txt());
        paid_txt.setText(sale.getPaid() + "");
        double rem = Double.parseDouble(sale.getTotal_txt()) - sale.getPaid();
        remian_txt.setText(decimal.format(rem));

        invoce_date.setText(sale.getDate());
        custName.setText(sale.getCustomerName());
        userName.setText(sale.getUserName());
        inv_code_txt.setText(sale.getCode());

        tableList.clear();
        this.payList.clear();
        tableList = detailsList;
        this.payList = payList;

        if (tableList.size() == 0) {
            invoiceTable.setVisible(false);
            editBox.setVisible(false);
            inv_box.setVisible(false);

        } else {
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

            editCol.setVisible(false);

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

                            if (ROLE == 0) {
                                deleteIcon.setVisible(false);

                            }

                            deleteIcon.setStyle(
                                    " -fx-cursor: hand ;"
                                    + "-glyph-size:28px;"
                                    + "-fx-fill:#ff1744;"
                            );

                            deleteIcon.setOnMouseClicked((MouseEvent event) -> {

                                invoice selectedItem = invoiceTable.getSelectionModel().getSelectedItem();

                                invoiceTable.getItems().remove(selectedItem);

                                //  calcInvTotal();
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
        }

        // get sales pay
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        paidCol.setCellValueFactory(new PropertyValueFactory<>("paid"));
        paidTypeCol.setCellValueFactory(new PropertyValueFactory<>("paid_type"));

        dateCol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");
        userCol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");
        paidCol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");
        paidTypeCol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");

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

                        //    if (ROLE == 0) {
                        //        deleteIcon.setVisible(false);
                        //    }
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

                        deleteIcon.setOnMouseClicked((MouseEvent event) -> {

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("هل انت متاكد من حذف السداد ");
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

                                        pay = payTable.getSelectionModel().getSelectedItem();

                                        if (pay.getId() == 0) {
                                            Alert alert2 = new Alert(Alert.AlertType.WARNING);
                                            alert.setHeaderText(null);
                                            alert.setContentText("لا يمكن حذف الدفعة الاولي");
                                            alert.showAndWait();
                                        }

                                        if (inv_code_txt.getText().contains("-OT")) {
                                            System.out.println("OT : PAY :: " + pay);
                                            query = "DELETE FROM `outsales_pay` WHERE id  =" + pay.getId();
                                        } else {
                                            System.out.println("PAY :: " + pay);
                                            query = "DELETE FROM `sales_pay` WHERE id  =" + pay.getId();
                                        }
                                        connection = DbConnect.getConnect();
                                        preparedStatement = connection.prepareStatement(query);
                                        int result = preparedStatement.executeUpdate();

                                        if (result != 0) {
                                            Alert alert2 = new Alert(Alert.AlertType.WARNING);
                                            alert.setHeaderText(null);
                                            alert.setContentText("تم الحذف");
                                            alert.showAndWait();

                                            payTable.getItems().remove(pay);
                                        } else {
                                            Alert alert2 = new Alert(Alert.AlertType.WARNING);
                                            alert.setHeaderText(null);
                                            alert.setContentText("حدث مشكلة في الحذف");
                                            alert.showAndWait();
                                        }
                                    } catch (Exception ex) {
                                        STATICDATA.ExceptionHandle(this.getClass().getName(), "loadData - delete", ex);
                                    }

                                } else if (response == ButtonType.CANCEL) {

                                }
                            });

                        });
                        editIcon.setOnMouseClicked((MouseEvent event) -> {

                        });

                        HBox managebtn = new HBox(deleteIcon);
                        managebtn.setStyle("-fx-alignment:center");
                        //    HBox.setMargin(deleteIcon, new javafx.geometry.Insets(2, 2, 0, 3));
                        HBox.setMargin(editIcon, new javafx.geometry.Insets(2, 2, 0, 3));

                        setGraphic(managebtn);

                        setText(null);

                    }
                }

            };

            return cell;
        };
        payEditCol.setCellFactory(cellFoctory);
        payTable.setItems(payList);

    }

    @FXML
    private void savePay(MouseEvent event) {
        printInvoiceAndPay();
    }

    public void setTextField(int id, int role) {
        USER_ID = id;
        ROLE = role;
    }
}
