package controllers;

import PDF.offerPriceReport;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.invoice;

public class ViewOfferPriceController implements Initializable {

    @FXML
    private DatePicker dateField;
    @FXML
    private TextField itempriceField;
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<String> itemnameField;
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
    private ComboBox<String> storeNameField;
    private TextField paidField;
    @FXML
    private TextField customerCol;
    @FXML
    private TextField offerNum;

    String query = null;
    String query2 = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    PreparedStatement preparedStatement2 = null;
    ResultSet resultSet = null;
    invoice inv = null;
    ArrayList<String> item = new ArrayList<>();
    ArrayList<String> item2 = new ArrayList<>();
    ArrayList<String> item3 = new ArrayList<>();
    String itemCode;

    ObservableList<invoice> tableList = FXCollections.observableArrayList();

    ArrayList<String[]> amontlist = new ArrayList<>();

    long millis = System.currentTimeMillis();
    java.sql.Date CDate = new java.sql.Date(millis);
    String currentDate = CDate + "";

    int hour = Calendar.getInstance().get(Calendar.HOUR);
    int min = Calendar.getInstance().get(Calendar.MINUTE);

    STATICDATA pdfPath = new STATICDATA();

    String offerPricefileLocation;

    int USER_ID;
    int ROLE;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {

            loadStoreCombo();

        } catch (SQLException ex) {
            Logger.getLogger(NewInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
        }

        itemnameField.setOnAction(e -> {
            String selected = itemnameField.getSelectionModel().getSelectedItem();

            try {
                getitemData(selected);
            } catch (SQLException ex) {
                Logger.getLogger(NewInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

  //    get StoreName From ComboBox
        storeNameField.setOnAction(e -> {

            item2.clear();
            itemnameField.getSelectionModel().clearSelection();
            itemnameField.getItems().clear();

            String StoreName = storeNameField.getSelectionModel().getSelectedItem();

            try {

                int code = getStoreCode(StoreName);
                loadItemCombo(code);

            } catch (SQLException ex) {
                Logger.getLogger(NewInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

        offerPricefileLocation = pdfPath.PDF_MAIN_FOLDER + pdfPath.PDF_OFFER_PRICE_FOLDER + "//";

    }

    @FXML
    private void itemCombo(MouseEvent event) {
    }

    @FXML
    private void save(MouseEvent event) throws SQLException, DocumentException, FileNotFoundException, BadElementException, IOException {

        LocalDate date = dateField.getValue();
        String CustomerName = customerCol.getText();
        String Date = date + "";
        int offNUM = Integer.parseInt(offerNum.getText());
//        int paid = Integer.parseInt(paidField.getText());

        String PDFNAME = "عرض سعر رقم" + offNUM + ".pdf";

        offerPriceReport offerReport;
        offerReport = new offerPriceReport();
        offerReport.openDocument(PDFNAME, offerPricefileLocation);
        offerReport.addCompanyInfo(Date, CustomerName, offNUM);

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

        offerReport.addTable(header, itemlist);

        offerReport.closeDocument();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

        try {
            System.out.println("offerPricefileLocation : "+offerPricefileLocation);
            Desktop.getDesktop().open(new File(offerPricefileLocation + PDFNAME));
        } catch (IOException ex) {
            Logger.getLogger(AddCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void addItemToInvoice(MouseEvent event) throws SQLException {

        double price = Double.parseDouble(itempriceField.getText());
        int amount = Integer.parseInt(amountField.getText());
        double total = price * amount;

        String name = itemnameField.getSelectionModel().getSelectedItem();

        invoice inv = new invoice(itemCode, amount, price, total, name);

        tableList.add(inv);

        itemCodeTAB.setCellValueFactory(new PropertyValueFactory<>("code"));
        amountTAB.setCellValueFactory(new PropertyValueFactory<>("amount"));
        priceTAB.setCellValueFactory(new PropertyValueFactory<>("price"));
        totalTAB.setCellValueFactory(new PropertyValueFactory<>("total"));
        itemnameTAB.setCellValueFactory(new PropertyValueFactory<>("name"));

        itemCodeTAB.setStyle("-fx-alignment: CENTER;");
        amountTAB.setStyle("-fx-alignment: CENTER;");
        priceTAB.setStyle("-fx-alignment: CENTER;");
        totalTAB.setStyle("-fx-alignment: CENTER;");
        itemCodeTAB.setStyle("-fx-alignment: CENTER;");

        itemnameField.getSelectionModel().clearSelection();
        itemnameField.setValue(null);
        storeNameField.getSelectionModel().clearSelection();
        storeNameField.setValue(null);
        itempriceField.setText("");
        amountField.setText("");

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

    private void loadItemCombo(int s) throws SQLException {

        item2.clear();

        connection = DbConnect.getConnect();

        query = "SELECT name FROM `items` where storeCode =?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, s);
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            item2.add(resultSet.getString("name"));
        }
        itemnameField.getItems().addAll(item2);
    }

    private void loadStoreCombo() throws SQLException {

        connection = DbConnect.getConnect();

        query = "SELECT storeName FROM `store`";
        preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {

            item3.add(resultSet.getString("storeName"));

        }

        storeNameField.getItems().addAll(item3);
    }

    private void getitemData(String d) throws SQLException {

        connection = DbConnect.getConnect();
        query = "SELECT * FROM `items` where name =?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, d);
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {

            itempriceField.setText(resultSet.getString("priceforcustomer"));
            amountField.setText(resultSet.getString("amount"));
            itemCode = resultSet.getString("code");

        }

    }

    private int getStoreCode(String StoreName) throws SQLException {

        int code = 0;
        connection = DbConnect.getConnect();
        query = "SELECT id FROM `store` where storeName =?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, StoreName);
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {

            code = resultSet.getInt("id");

            return code;

        }
        return code;
    }

    void setTextField(int id, int role) {
        USER_ID = id;
        ROLE = role;
    }

}
