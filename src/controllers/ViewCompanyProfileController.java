/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import PDF.salesReport;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.customerInv;

/**
 * FXML Controller class
 *
 * @author Ahmed Gabr
 */
public class ViewCompanyProfileController implements Initializable {

    @FXML
    private TableView<customerInv> invoiceTable;
    @FXML
    private TableColumn<customerInv, String> editCol;
    @FXML
    private TableColumn<customerInv, String> remainTab;
    @FXML
    private TableColumn<customerInv, String> paidTab;
    @FXML
    private TableColumn<customerInv, String> totalTab;
    @FXML
    private TableColumn<customerInv, String> itemnameTab;
    @FXML
    private TableColumn<customerInv, String> dateTab;
    @FXML
    private TableColumn<customerInv, String> itemIdTab;
    @FXML
    private Label customerCol;
    @FXML
    private TextField cus_id;
    @FXML
    private TextField invTotal;
    //  private TextField itempriceField;
    //  private TextField amountField;
    @FXML
    private TextArea itemnameField;
    @FXML
    private TextField link;
    @FXML
    private TextField paidField;

    int customer_id;
    String query = null;
    String query2 = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    PreparedStatement preparedStatement2 = null;
    ResultSet resultSet = null;
    customerInv cusInv = null;

    ArrayList<String> item = new ArrayList<>();

    String itemCode;
    File pdfFile;

    ObservableList<customerInv> tableList = FXCollections.observableArrayList();

    ArrayList<String[]> amontlist = new ArrayList<>();
    @FXML
    private TextField TOTAL;
    @FXML
    private TextField PAID;
    @FXML
    private TextField REMAIN;

    //  User user = null;
    /**
     * Initializes the controller class.
     */
    DecimalFormat decimal = new DecimalFormat("#.##");
    @FXML
    private DatePicker from_date;
    @FXML
    private DatePicker to_date;

    String FROM_DATE;
    String TO_DATE;

    int USER_ID;
    int ROLE;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // customerCol.setEditable(false);
        // invTotal.setEditable(false);
        cus_id.setVisible(false);
        link.setVisible(false);

        loadDate();

        to_date.setOnAction(e -> {

            FROM_DATE = from_date.getValue() + "";
            TO_DATE = to_date.getValue() + "";

            getBetweenDate();

        });

        from_date.setOnAction(e -> {

            to_date.setValue(null);
        });

    }

    @FXML
    private void addItemToInvoice(MouseEvent event) {

        long millis = System.currentTimeMillis();
        java.sql.Date CurrectDate = new java.sql.Date(millis);
        String data = CurrectDate + "";
        try {

            if (itemnameField.getText().isEmpty() || invTotal.getText().isEmpty() || paidField.getText().isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("الرجاء ادخال البيانات");
                alert.showAndWait();
            } else {

                double total = Double.parseDouble(invTotal.getText());
                double paid = Double.parseDouble(paidField.getText());
                double remain = total - paid;
                // String remain_formated = decimal.format(remain);
                double remain_formated = Double.parseDouble(decimal.format(remain));

                String Link = link.getText();

                String ItemName = itemnameField.getText();
                int Id = Integer.parseInt(cus_id.getText());

                SaveInvoice(Id, data, ItemName, total, paid, remain_formated, Link);

                refresh();

                itemnameField.setText("");
                invTotal.setText("");
                link.setText("");
                paidField.setText("");

                calcAllTotal();

            }
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "addItemToInvoice", ex);
        }
    }

    private void SaveInvoice(int cus_id, String date, String item, double Total, double paid, double remain_formated, String link) {

        try {
            connection = DbConnect.getConnect();

            query = "INSERT INTO `companysales`(`item`, `total`, `paid`, `remain`, `date`, `cus_id`,`user_id`, `link`) VALUES (?,?,?,?,?,?,?,?)";

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, item);
            preparedStatement.setDouble(2, Total);
            preparedStatement.setDouble(3, paid);
            preparedStatement.setDouble(4, remain_formated);
            preparedStatement.setString(5, date);
            preparedStatement.setInt(6, cus_id);
            preparedStatement.setInt(7, USER_ID);
            preparedStatement.setString(8, link);

            preparedStatement.execute();
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "SaveInvoice", ex);
        }
    }

    @FXML
    void getLink(MouseEvent event) {

        chooseFileAndPrintRelativePath();
    }

    void HideID() {
        cus_id.setVisible(false);
        link.setVisible(false);
    }

    void setTextField(int userId, int role, int cusId, String name) {

        USER_ID = userId;
        ROLE = role;
        customer_id = cusId;
        customerCol.setText(name);
        cus_id.setText(cusId + "");
        HideID();
        calcAllTotal();

    }

    @FXML
    private void refresh() {

        from_date.setValue(null);
        to_date.setValue(null);

        try {
            tableList.clear();

            query = "SELECT * FROM `companysales` where cus_id=? ";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cus_id.getText());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                tableList.add(new customerInv(
                        resultSet.getInt("id"),
                        resultSet.getInt("cus_id"),
                        resultSet.getDouble("total"),
                        resultSet.getDouble("paid"),
                        resultSet.getDouble("remain"),
                        resultSet.getString("date"),
                        resultSet.getString("item"),
                        resultSet.getString("link")));

                invoiceTable.setItems(tableList);

            }

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "refresh", ex);
        }

        calcAllTotal();

    }

    private void getBetweenDate() {
        try {
            tableList.clear();

            query = "SELECT * FROM `companysales` where cus_id=? AND date between ? AND ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cus_id.getText());
            preparedStatement.setString(2, FROM_DATE);
            preparedStatement.setString(3, TO_DATE);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                tableList.add(new customerInv(
                        resultSet.getInt("id"),
                        resultSet.getInt("cus_id"),
                        resultSet.getDouble("total"),
                        resultSet.getDouble("paid"),
                        resultSet.getDouble("remain"),
                        resultSet.getString("date"),
                        resultSet.getString("item"),
                        resultSet.getString("link")));

                invoiceTable.setItems(tableList);

            }

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "getBetweenDate", ex);
        }

        calcAllTotal();

    }

    void loadDate() {

        connection = DbConnect.getConnect();
        refresh();

        itemIdTab.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateTab.setCellValueFactory(new PropertyValueFactory<>("date"));
        itemnameTab.setCellValueFactory(new PropertyValueFactory<>("item"));
        totalTab.setCellValueFactory(new PropertyValueFactory<>("total"));
        paidTab.setCellValueFactory(new PropertyValueFactory<>("paid"));
        remainTab.setCellValueFactory(new PropertyValueFactory<>("remain"));

        itemIdTab.setStyle("-fx-alignment: CENTER;");
        dateTab.setStyle("-fx-alignment: CENTER;");
        itemnameTab.setStyle("-fx-alignment: CENTER;");
        totalTab.setStyle("-fx-alignment: CENTER;");
        paidTab.setStyle("-fx-alignment: CENTER;");
        remainTab.setStyle("-fx-alignment: CENTER;");

        //add cell of button edit 
        Callback<TableColumn<customerInv, String>, TableCell<customerInv, String>> cellFoctory = (TableColumn<customerInv, String> param) -> {
            // make cell containing buttons
            final TableCell<customerInv, String> cell = new TableCell<customerInv, String>() {
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

                            try {
                                cusInv = invoiceTable.getSelectionModel().getSelectedItem();
                                query = "DELETE FROM `companysales` WHERE id  =" + cusInv.getId();
                                connection = DbConnect.getConnect();
                                preparedStatement = connection.prepareStatement(query);
                                preparedStatement.execute();
                                loadDate();

                            } catch (SQLException ex) {
                                STATICDATA.ExceptionHandle(this.getClass().getName(), ";loadData - delete", ex);
                            }

                        });
                        editIcon.setOnMouseClicked((MouseEvent event) -> {

                            /*
                             Stage stagemain = (Stage) ((Node) event.getSource()).getScene().getWindow();
                             stagemain.close();

                             cusInv = invoiceTable.getSelectionModel().getSelectedItem();

                             FXMLLoader loader = new FXMLLoader();
                             loader.setLocation(getClass().getResource("/tableView/editSales.fxml"));
                             try {
                             loader.load();
                             } catch (IOException ex) {
                             Logger.getLogger(ViewCustomerController.class.getName()).log(Level.SEVERE, null, ex);
                             }

                             EditSalesController editSalesController = loader.getController();
                             // علشان نفرق بين الموردين و المبيعات
                             editSalesController.setUpdateSales(false);
                             editSalesController.setTextField(cusInv.getId(), cusInv.getTotal(), cusInv.getPaid(), cusInv.getRemain(), customerCol.getText(), customer_id);
                             Parent parent = loader.getRoot();
                             Stage stage = new Stage();
                             stage.setScene(new Scene(parent));
                             stage.initStyle(StageStyle.UTILITY);
                             stage.show();

                             loadDate();

                             */
                        });

                        open.setOnMouseClicked((MouseEvent event) -> {

                            cusInv = invoiceTable.getSelectionModel().getSelectedItem();

                            String path = cusInv.getLink();
                            File pdf = new File(path);

                            if (cusInv.getLink().isEmpty() || !pdf.exists()) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setHeaderText(null);
                                alert.setContentText("لم يتم حفظ ملف");
                                alert.showAndWait();
                            } else {

                                try {
                                    Desktop.getDesktop().open(pdf);
                                } catch (IOException ex) {
                                    STATICDATA.ExceptionHandle(this.getClass().getName(), ";loadData - open", ex);
                                }
                            }
                        });

                        HBox managebtn = new HBox(deleteIcon, open);
                        managebtn.setStyle("-fx-alignment:center");
                        HBox.setMargin(deleteIcon, new javafx.geometry.Insets(2, 2, 0, 3));
                        //HBox.setMargin(editIcon, new javafx.geometry.Insets(2, 2, 0, 3));
                        HBox.setMargin(open, new javafx.geometry.Insets(2, 2, 0, 3));

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

    public void chooseFileAndPrintRelativePath() {

        FileChooser photo = new FileChooser();
        Stage stage = new Stage();
        stage.setTitle("مسار الملف");
        Button openButton = new Button("اختر الملف");
        openButton.setOnAction((t) -> {
            File file = photo.showOpenDialog(stage);
            if (file != null) {
                String cwd = System.getProperty("user.dir");
                String path = new File(cwd).toURI().relativize(file.toURI()).getPath();
                link.setText(path);
                stage.close();
            }

        });
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(400, 200);
        gridPane.add(openButton, 0, 0);
        Scene scene = new Scene(gridPane);
        stage.setScene(scene);
        stage.show();

    }

    public void calcAllTotal() {

        ArrayList<Double> TotalList = new ArrayList<>();
        ArrayList<Double> PaidList = new ArrayList<>();
        ArrayList<Double> RemainList = new ArrayList<>();

        for (int i = 0; i < tableList.size(); i++) {

            customerInv v = tableList.get(i);

            String TotaL = String.valueOf(v.getTotal());
            String Paid = String.valueOf(v.getPaid());
            String Remain = String.valueOf(v.getRemain());

            double totalNUM = Double.parseDouble(TotaL);
            double paidNUM = Double.parseDouble(Paid);
            double remainNUM = Double.parseDouble(Remain);
            TotalList.add(totalNUM);
            PaidList.add(paidNUM);
            RemainList.add(remainNUM);
        }

        double INVTOtal = 0.00;
        double INVPaid = 0.00;
        double INVRemain = 0.00;
        for (int i = 0; i < TotalList.size(); i++) {

            INVTOtal = INVTOtal + TotalList.get(i);
            INVPaid = INVPaid + PaidList.get(i);
            INVRemain = INVRemain + RemainList.get(i);
        }

        decimal.format(INVPaid);

        //   String StringTotal = String.valueOf(INVTOtal);
        //   String stringPaid = String.valueOf(INVPaid);
        //   String StringRemain = String.valueOf(INVRemain);
        String StringTotal = decimal.format(INVTOtal);
        String stringPaid = decimal.format(INVPaid);
        String StringRemain = decimal.format(INVRemain);

        TOTAL.setText(StringTotal);
        PAID.setText(stringPaid);
        REMAIN.setText(StringRemain);

        TOTAL.setEditable(false);
        PAID.setEditable(false);
        REMAIN.setEditable(false);

    }

    @FXML
    private void save(MouseEvent event) {
        STATICDATA pdfPath = new STATICDATA();
        String salesfileLocation = pdfPath.PDF_MAIN_FOLDER + pdfPath.PDF_COMPANY_SALES_FOLDER;

        salesReport saleReport;
        saleReport = new salesReport();
        try {
            // Add Data To Report   
            String[] header = {"كود الفاتورة", "البيان", "التاريخ", "الاجمالي", "المدفوع", "المتبقي"};

            ArrayList<String[]> salelist = new ArrayList<>();

            String CustomerName = customerCol.getText();

            for (int i = 0; i < tableList.size(); i++) {

                customerInv s = tableList.get(i);
                String SaleId = String.valueOf(s.getId());
                String customerName = s.getItem();
                String InvDATE = s.getDate();
                String Total = s.getTotal() + "";
                String paid = String.valueOf(s.getPaid());
                String remain = String.valueOf(s.getRemain());
                // String userName = s.getUserName();

                String[] data = {SaleId, customerName, InvDATE, Total, paid, remain};
                salelist.add(data);

            }

            LocalDate date = LocalDate.now();
            String Date = date + "";

            String userName = customerCol.getText();
            // String userSalesLocation = salesfileLocation+userName;

            String PDFNAME = "مورد " + userName + Date + ".pdf";

            saleReport.openDocument(PDFNAME, salesfileLocation);
            saleReport.addTopHeader(" سجل مورد " + userName, "في الفترة من :    " + from_date.getValue(), "   إلـــي:   " + to_date.getValue());
            saleReport.addCompanyInfo(Date, CustomerName, salelist.size());
            saleReport.addTable(header, salelist);

            saleReport.addTotalTable(PAID.getText(), TOTAL.getText(), REMAIN.getText());

            saleReport.closeDocument();

            Desktop.getDesktop().open(new File(salesfileLocation));
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "save", ex);
        }

    }

}
