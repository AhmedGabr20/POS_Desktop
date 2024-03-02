package controllers;

import PDF.ItemsReprot;
import PDF.ItemsBarCodeTemplete;
import PDF.ItemsBarCodeTempleteOne;
import com.codoid.products.fillo.Fillo;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import helper.DbConnect;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import models.User;
import models.items;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

public class ViewItemsController implements Initializable {

    @FXML
    private TextField barcodeSearch;
    @FXML
    private TableView<items> customerTable;
    @FXML
    private TableColumn<items, String> pricecol;
    @FXML
    private TableColumn<items, String> storecol;
    @FXML
    private TableColumn<items, String> Quncol;
    @FXML
    private TableColumn<items, String> namecol;
    @FXML
    private TableColumn<items, String> idcol;
    @FXML
    private TableColumn<items, String> editCol;
    @FXML
    private ComboBox<String> storeNameField;
    @FXML
    private TableColumn<items, String> priceforcustomercol;
    @FXML
    private TextField search;
    @FXML
    private VBox vbox1;
    @FXML
    private FontAwesomeIconView print_btn;
    @FXML
    private Button ref_btn;
    @FXML
    private Button add_btn;
    @FXML
    private Label text_store;
    @FXML
    private Label waring_amount;
    @FXML
    private TableColumn<String, String> totalMony;
    @FXML
    private TextField TOTAL;
    @FXML
    private Button print_barcode_btn;
    @FXML
    private VBox update_layout;
    @FXML
    private Label total_items;
    @FXML
    private ImageView excel_icon;
    @FXML
    private ProgressIndicator progress_index;

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    items itm = null;
    ArrayList<String> storesArray = new ArrayList<>();
    User user = null;
    ObservableList<items> itemsList = FXCollections.observableArrayList();
    ObservableList<items> searchList;

    long millis = System.currentTimeMillis();
    java.sql.Date CDate = new java.sql.Date(millis);
    String currentDate = CDate + "";

    int hour = Calendar.getInstance().get(Calendar.HOUR);
    int min = Calendar.getInstance().get(Calendar.MINUTE);

    STATICDATA pdfPath = new STATICDATA();

    String itemsfileLocation;
    String itemsfileLocation_Barcode;

    String PDFNAME = "تقرير رقم" + currentDate + "_" + hour + "-" + min + ".pdf";

    int USER_ID = 0;
    int ROLE;

    MainController MainController = new MainController();

    DecimalFormat decimal = new DecimalFormat("#.##");

    DecimalFormat decimalLarge = new DecimalFormat("#.#######");

    int STORE_CODE;

    static int STORE_ALARM;

    File pdfFile;

    static String BARCODE_PRINTER_NAME = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {

            progress_index.setVisible(false);
            Image img = new Image("/img/excel.png");
            excel_icon.setImage(img);

            File xmlFile = new File("settings.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            Document doc = (Document) builder.parse(xmlFile);
            getXmlData(doc);

            // الصلاحيات
            if (ROLE == 0) {
                pricecol.setVisible(false);
                editCol.setVisible(false);
                totalMony.setVisible(false);
                add_btn.setVisible(false);
                update_layout.setVisible(false);
            } else {
                pricecol.setVisible(true);
                editCol.setVisible(true);
                totalMony.setVisible(true);
                add_btn.setVisible(true);
                update_layout.setVisible(true);
            }

            loadStoreCombo();

            barcodeSearch.requestFocus();
            barcodeSearch.setFocusTraversable(true);
            customerTable.setFocusTraversable(false);
            print_btn.setFocusTraversable(false);
//            warning.setFocusTraversable(false);
            search.setFocusTraversable(false);
            ref_btn.setFocusTraversable(false);

            loadDate();

            storeNameField.setOnAction(e -> {

                String StoreName = storeNameField.getSelectionModel().getSelectedItem();

                STORE_CODE = getStoreCode(StoreName);
                loadItemByStore(STORE_CODE);

                if (ROLE != 0) {
                    update_layout.setVisible(true);
                }

            });

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "initialize", ex);
        }

        itemsfileLocation = pdfPath.PDF_MAIN_FOLDER + pdfPath.PDF_ITEMS_FOLDER + "//";
        itemsfileLocation_Barcode = pdfPath.PDF_MAIN_FOLDER + pdfPath.PDF_ITEMS_BARCODE_FOLDER + "//";

    }

    private static void getXmlData(Document doc) {
        NodeList nodes = doc.getElementsByTagName("config");

        org.w3c.dom.Node itemNode = nodes.item(0);
        if (itemNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            Element element = (Element) itemNode;
            STORE_ALARM = Integer.parseInt(element.getElementsByTagName("StoreAlarm").item(0).getTextContent());
            BARCODE_PRINTER_NAME = element.getElementsByTagName("BarcodePrinterName").item(0).getTextContent();
        }
    }

    @FXML
    private void AddCustomer(MouseEvent event) {
        close(event);
        openADDItemPage(USER_ID, ROLE);
    }

    @FXML
    private void refresh() {

        try {
            search.clear();

            storeNameField.getSelectionModel().select("");

            update_layout.setVisible(false);
            editCol.setVisible(true);
            add_btn.setVisible(true);
            storeNameField.setVisible(true);
            barcodeSearch.setVisible(true);
            text_store.setText("البحث بالمخزن");

            barcodeSearch.setText("");
            ref_btn.setFocusTraversable(false);
            barcodeSearch.setFocusTraversable(true);

            checkitem();

            itemsList.clear();

            query = "select items.id , items.code , items.name , items.amount , items.price , items.priceforcustomer , store.storeName from items , store where items.storeCode = store.id ";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                double TotalMoney = resultSet.getInt("amount") * resultSet.getDouble("priceforcustomer");
                double price = resultSet.getDouble("price");
                double priceCustomer = resultSet.getDouble("priceforcustomer");
                itemsList.add(new items(
                        resultSet.getInt("id"),
                        resultSet.getString("code"),
                        resultSet.getString("name"),
                        resultSet.getInt("amount"),
                        Double.parseDouble(decimal.format(price)),
                        Double.parseDouble(decimal.format(priceCustomer)),
                        resultSet.getString("storeName"),
                        TotalMoney));

                customerTable.setItems(itemsList);

            }

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "refresh", ex);
        }

        if (ROLE == 0) {
            pricecol.setVisible(false);
            editCol.setVisible(false);
            totalMony.setVisible(false);
            add_btn.setVisible(false);
            update_layout.setVisible(false);
        } else {
            pricecol.setVisible(true);
            editCol.setVisible(true);
            totalMony.setVisible(true);
            add_btn.setVisible(true);
            update_layout.setVisible(true);
        }

        calcAllTotal();
        total_items.setText(itemsList.size() + "");
    }

    @FXML
    private void print(MouseEvent event) {

        ItemsReprot itemsReprot;

        itemsReprot = new ItemsReprot();

        try {
            itemsReprot.openDocument(PDFNAME, itemsfileLocation);

            // Add Data To Report   
            String[] header = {"الكود", "أسم الصــنف", "الكمية", "سعر البيع", "المخزن"};

            ArrayList<String[]> itemList = new ArrayList<String[]>();

            //  for(int i=0 ; i<itemsList.size();i++){
            for (int i = 0; i < itemsList.size(); i++) {

                items m = itemsList.get(i);
                String code = String.valueOf(m.getCode());
                String name = m.getName();
                String amount = String.valueOf(m.getQuantity());
                String price = String.valueOf(m.getPrice());
                String priceSales = String.valueOf(m.getPriceforcustomer());
                String store = m.getStoreName();

                String[] data = {code, name, amount, priceSales, store};
                itemList.add(data);
            }
            itemsReprot.addTable(header, itemList);

            // itemsReprot.Frame();
            itemsReprot.closeDocument();

            Desktop.getDesktop().open(new File(itemsfileLocation));
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "print", ex);
            STATICDATA.AlertMessage(ex.getMessage());
        }
    }

    private void close(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void loadDate() {

        try {
            connection = DbConnect.getConnect();
            refresh();

            idcol.setCellValueFactory(new PropertyValueFactory<>("code"));
            namecol.setCellValueFactory(new PropertyValueFactory<>("name"));
            Quncol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            storecol.setCellValueFactory(new PropertyValueFactory<>("storeName"));
            pricecol.setCellValueFactory(new PropertyValueFactory<>("price"));
            priceforcustomercol.setCellValueFactory(new PropertyValueFactory<>("priceforcustomer"));
            totalMony.setCellValueFactory(new PropertyValueFactory<>("totalMoney"));

            idcol.setStyle("-fx-alignment: CENTER;");
            namecol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");
            Quncol.setStyle("-fx-alignment: CENTER;");
            storecol.setStyle("-fx-alignment: CENTER;");
            pricecol.setStyle("-fx-alignment: CENTER;");
            priceforcustomercol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");
            totalMony.setStyle("-fx-alignment: CENTER;");

            //add cell of button edit 
            Callback<TableColumn<items, String>, TableCell<items, String>> cellFoctory = (TableColumn<items, String> param) -> {
                // make cell containing buttons
                final TableCell<items, String> cell = new TableCell<items, String>() {
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
                            FontAwesomeIconView print = new FontAwesomeIconView(FontAwesomeIcon.BARCODE);

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
                            print.setStyle(
                                    " -fx-cursor: hand ;"
                                    + "-glyph-size:28px;"
                                    + "-fx-fill:#00E676;"
                            );
                            deleteIcon.setOnMouseClicked((MouseEvent event) -> {

                                Button button1 = new Button();
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setHeaderText("هل انت متاكد من مسح الصنف");
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

                                            itm = customerTable.getSelectionModel().getSelectedItem();
                                            query = "DELETE FROM `items` WHERE id  =" + itm.getId();
                                            connection = DbConnect.getConnect();
                                            preparedStatement = connection.prepareStatement(query);
                                            preparedStatement.execute();
                                            refresh();

                                        } catch (SQLException ex) {
                                            Logger.getLogger(ViewCustomerController.class
                                                    .getName()).log(Level.SEVERE, null, ex);
                                        }
                                    } else if (response == ButtonType.CANCEL) {

                                    }
                                });

                            });
                            editIcon.setOnMouseClicked((MouseEvent event) -> {

                                Stage stagemain = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                stagemain.close();

                                itm = customerTable.getSelectionModel().getSelectedItem();
                                openEditItemPage(USER_ID, ROLE, itm);

                            });
                            print.setOnMouseClicked((MouseEvent event) -> {

                                itm = customerTable.getSelectionModel().getSelectedItem();

                                ItemsBarCodeTempleteOne temp = new ItemsBarCodeTempleteOne();
                                temp.openDocument(itm.getCode(), itemsfileLocation_Barcode);
                                temp.addTopHeader(itm);
                                temp.closeDocument();

                                try {
                                    // print Service
                                    PDDocument doc1 = Loader.loadPDF(new File(itemsfileLocation_Barcode+itm.getCode()+".pdf"));
                                    //takes standard printer defined by OS
                                    PrintService myPrintService = PrintServiceLookup.lookupDefaultPrintService();
                                    myPrintService = findPrintService(BARCODE_PRINTER_NAME);
                                    if (myPrintService == null) {
                                        Alert alert = new Alert(Alert.AlertType.WARNING);
                                        alert.setHeaderText(null);
                                        alert.setContentText("خطأ الأتصال بالطــــابـــعة" + "\n" + BARCODE_PRINTER_NAME);
                                        alert.showAndWait();

                                    }
                                    PrinterJob job = PrinterJob.getPrinterJob();
                                    job.setPageable(new PDFPageable(doc1));
                                    job.setPrintService(myPrintService);

                                    job.print();

                                    Desktop.getDesktop().open(new File(itemsfileLocation_Barcode));

                                } catch (Exception ex) {
                                    STATICDATA.ExceptionHandle(this.getClass().getName(), "loadData Print barcode", ex);
                                }

                            });

                            HBox managebtn = new HBox(editIcon, deleteIcon, print);
                            managebtn.setStyle("-fx-alignment:center");
                            HBox.setMargin(deleteIcon, new javafx.geometry.Insets(2, 2, 0, 3));
                            HBox.setMargin(editIcon, new javafx.geometry.Insets(2, 2, 0, 3));
                            HBox.setMargin(print, new javafx.geometry.Insets(2, 2, 0, 3));

                            setGraphic(managebtn);

                            setText(null);

                        }
                    }

                };

                return cell;
            };

            if (ROLE == 0) {
                pricecol.setVisible(false);
                editCol.setVisible(false);
                add_btn.setVisible(false);
                totalMony.setVisible(false);
            }

            editCol.setCellFactory(cellFoctory);

            customerTable.setItems(itemsList);
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "loadData", ex);
        }

    }

    public int getStoreCode(String StoreName) {

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

    public void loadDataByAmount() {

        connection = DbConnect.getConnect();
        try {
            itemsList.clear();

            query = "SELECT items.id , items.code , items.name , items.amount , items.price , items.priceforcustomer , store.storeName from items , store where items.storeCode=store.id AND items.amount <" + STORE_ALARM;

            preparedStatement = connection.prepareStatement(query);
            //    preparedStatement.setString(1, searchName);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                double TotalMoney = resultSet.getInt("amount") * resultSet.getDouble("priceforcustomer");
                double price = resultSet.getDouble("price");
                double priceCustomer = resultSet.getDouble("priceforcustomer");
                itemsList.add(new items(
                        resultSet.getInt("id"),
                        resultSet.getString("code"),
                        resultSet.getString("name"),
                        resultSet.getInt("amount"),
                        Double.parseDouble(decimal.format(price)),
                        Double.parseDouble(decimal.format(priceCustomer)),
                        resultSet.getString("storeName"),
                        TotalMoney));

                customerTable.setItems(itemsList);

            }

        } catch (SQLException ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "loadDataByAmount", ex);
        }

        //vbox1.setVisible(false);
        idcol.setCellValueFactory(new PropertyValueFactory<>("code"));
        namecol.setCellValueFactory(new PropertyValueFactory<>("name"));
        Quncol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        storecol.setCellValueFactory(new PropertyValueFactory<>("storeName"));
        pricecol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceforcustomercol.setCellValueFactory(new PropertyValueFactory<>("priceforcustomer"));
        totalMony.setCellValueFactory(new PropertyValueFactory<>("totalMoney"));

        //  label1.setText("الاصناف علي وشك النفاذ : "+itemsList.size());
        calcAllTotal();
        total_items.setText(itemsList.size() + "");

    }

    @FXML
    private void searchMeal(KeyEvent event) {

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<items> filteredData = new FilteredList<>(itemsList, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (item.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (item.getCode().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<items> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(customerTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        customerTable.setItems(sortedData);

    }

    @FXML
    private void warning(MouseEvent event) {

        loadDataByAmount();
        editCol.setVisible(false);
        print_btn.setVisible(true);
        add_btn.setVisible(false);
        storeNameField.setVisible(false);
        barcodeSearch.setVisible(false);
        text_store.setText("الأصناف علي وشك النفاذ");
    }

    public int checkitem() {
        int size = 0;
        try {

            connection = DbConnect.getConnect();

            query = "SELECT count(*) as count from items  where amount <" + STORE_ALARM;
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                size = resultSet.getInt("count");
            }

            waring_amount.setText(size + "");
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "checkitem", ex);
        }

        return size;
    }

    // not used any more 
    //
    //
    private void barcodeSearch(KeyEvent event) {

        try {
            itemsList.clear();

            // query = "select * from items where code=?";
            query = "SELECT items.id , items.code , items.name , items.amount , items.price , items.priceforcustomer , store.storeName from items , store where items.storeCode=store.id AND items.code = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, barcodeSearch.getText());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                double TotalMoney = resultSet.getInt("amount") * resultSet.getDouble("priceforcustomer");
                double price = resultSet.getDouble("price");
                double priceCustomer = resultSet.getDouble("priceforcustomer");
                itemsList.add(new items(
                        resultSet.getInt("id"),
                        resultSet.getString("code"),
                        resultSet.getString("name"),
                        resultSet.getInt("amount"),
                        Double.parseDouble(decimal.format(price)),
                        Double.parseDouble(decimal.format(priceCustomer)),
                        resultSet.getString("storeName"),
                        TotalMoney));

                customerTable.setItems(itemsList);

            }

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "barcodeSearch", ex);
        }
        calcAllTotal();

    }

    private void loadStoreCombo() {

        try {

            connection = DbConnect.getConnect();

            query = "SELECT storeName FROM `store`";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                storesArray.add(resultSet.getString("storeName"));

                //   storeNameField.getItems().add(resultSet.getInt("id"), resultSet.getString("storeName"));
            }

            storeNameField.getItems().addAll(storesArray);
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "loadStoreCombo", ex);
        }
    }

    private void loadItemByStore(int s) {

        try {

            itemsList.clear();

            connection = DbConnect.getConnect();

            query = "SELECT items.id , items.code , items.name , items.amount , items.price , items.priceforcustomer , store.storeName from items , store where items.storeCode=store.id AND items.storeCode=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, s);
            // preparedStatement.setInt(2, s);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                double TotalMoney = resultSet.getInt("amount") * resultSet.getDouble("priceforcustomer");
                double price = resultSet.getDouble("price");
                double priceCustomer = resultSet.getDouble("priceforcustomer");
                itemsList.add(new items(
                        resultSet.getInt("id"),
                        resultSet.getString("code"),
                        resultSet.getString("name"),
                        resultSet.getInt("amount"),
                        Double.parseDouble(decimal.format(price)),
                        Double.parseDouble(decimal.format(priceCustomer)),
                        resultSet.getString("storeName"),
                        TotalMoney));

                customerTable.setItems(itemsList);

            }

            calcAllTotal();
            total_items.setText(itemsList.size() + "");
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "loadItemByStore", ex);
        }
    }

    public void calcAllTotal() {

        // ArrayList<Double>TotalList = new ArrayList<>(); 
        double totalMoney = 0.00;

        for (int i = 0; i < itemsList.size(); i++) {

            items v = itemsList.get(i);

            String TotaL = String.valueOf(v.getTotalMoney());

            totalMoney += Double.parseDouble(TotaL);

        }

        // TOTAL.setText(decimal.format(totalMoney));
        TOTAL.setText(decimal.format(totalMoney));
        TOTAL.setEditable(false);
    }

    @FXML
    private void printBarcode(MouseEvent event) {

        ItemsBarCodeTemplete temp = new ItemsBarCodeTemplete();
        try {

            temp.openDocument("BarcodeReport", itemsfileLocation_Barcode);
            temp.addTopHeader(itemsList);
            temp.closeDocument();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            
            Desktop.getDesktop().open(new File(itemsfileLocation_Barcode+"BarcodeReport.pdf"));

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "printBarcode", ex);
        }

    }

    @FXML
    private void updateAll(MouseEvent event) {

        openEditStoreSalesPage(USER_ID, ROLE, storesArray);
        /*
         double PERSENT = Integer.parseInt(persent.getText());

         //   if (PERSENT > 0) {
         try {
         PERSENT = PERSENT / 100;

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

         loadItemByStore(STORE_CODE);

         persent.setText("0");
         PERSENT = 0;

         }

         } catch (SQLException ex) {
         Logger.getLogger(ViewItemsController.class
         .getName()).log(Level.SEVERE, null, ex);
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

    @FXML
    private void printExcel(MouseEvent event) {

        try {

            progress_index.setVisible(true);
            Fillo fillo = new Fillo();
            File folder = new File(itemsfileLocation);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            pdfFile = new File(folder, "items.xlsx");
            // remove all data from excel sheet 

            String query_delete = "delete from  items ";
            com.codoid.products.fillo.Connection c = (com.codoid.products.fillo.Connection) fillo.getConnection(pdfFile.getPath());
            c.executeUpdate(query_delete);

            // Excel 
            // items itemm : itemsList
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {

                        BigDecimal p = new BigDecimal(0);
                        //double size = itemsList.size();
                        double size = itemsList.size();
                        double inc = (double) 1 / size;
                        String in = decimalLarge.format(inc);
                        double d = Double.parseDouble(in);
                        BigDecimal increment = BigDecimal.valueOf(d);

                        for (int i = 1; i < size; i++) {
                            items itemm = itemsList.get(i);
                            String query_insert = "Insert into items (code,name,amount,priceSales,store) values (' "
                                    + itemm.getCode() + "' , ' "
                                    + itemm.getName() + " ' , ' "
                                    + itemm.getQuantity() + " ' , ' "
                                    + itemm.getPriceforcustomer() + " ' , ' "
                                    + itemm.getStoreName() + " ' ) ";

                            //  c = (com.codoid.products.fillo.Connection) fillo.getConnection(pdfFile.getPath());
                            c.executeUpdate(query_insert);
                            //  Recordset r = c.executeQuery(query_insert);

                            p = p.add(increment);

                            while (i == size - 1) {
                                p = p.add(p);
                                size = 0;
                            }

                            progress_index.setProgress(p.doubleValue());

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            );

            //   progress_index.setProgress(p);
            //   progress_index.setPrefWidth(p);
            //    progress_index.setProgress(40);
            // System.out.println("count : "+r.getCount());
            // progress.setProgress(0.5);
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "printExcel", ex);
        }
    }

    void setTextField(int id, int role) {
        USER_ID = id;
        ROLE = role;
        if (ROLE == 0) {
            pricecol.setVisible(false);
            editCol.setVisible(false);
            totalMony.setVisible(false);
            add_btn.setVisible(false);
            update_layout.setVisible(false);
        } else {
            pricecol.setVisible(true);
            editCol.setVisible(true);
            totalMony.setVisible(true);
            add_btn.setVisible(true);
            update_layout.setVisible(true);
        }
    }

    public void openADDItemPage(int id, int role) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/addItem.fxml"));
            loader.load();

            AddItemController addItemController = loader.getController();
            addItemController.setTextField(id, role);
            Scene scene = new Scene(loader.getRoot());
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    MainController.openItemsPage(id, role);
                }
            });
            stage.show();
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openADDItemPage", ex);
        }
    }

    public void openEditItemPage(int id, int role, items item) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/addItem.fxml"));
            loader.load();

            AddItemController addItemController = loader.getController();
            addItemController.setTextField(id, role);
            addItemController.setUpdate(true);
            addItemController.setTextField(item);
            Scene scene = new Scene(loader.getRoot());
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    MainController.openItemsPage(id, role);
                }
            });
            stage.show();
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openEditItemPage", ex);
        }
    }

    @FXML
    private void barcodeSearch(ActionEvent event) {

        try {
            itemsList.clear();

            // query = "select * from items where code=?";
            query = "SELECT items.id , items.code , items.name , items.amount , items.price , items.priceforcustomer , store.storeName from items , store where items.storeCode=store.id AND items.code = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, barcodeSearch.getText());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                double TotalMoney = resultSet.getInt("amount") * resultSet.getDouble("priceforcustomer");
                double price = resultSet.getDouble("price");
                double priceCustomer = resultSet.getDouble("priceforcustomer");
                itemsList.add(new items(
                        resultSet.getInt("id"),
                        resultSet.getString("code"),
                        resultSet.getString("name"),
                        resultSet.getInt("amount"),
                        Double.parseDouble(decimal.format(price)),
                        Double.parseDouble(decimal.format(priceCustomer)),
                        resultSet.getString("storeName"),
                        TotalMoney));

                customerTable.setItems(itemsList);

            }

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "barcodeSearch", ex);
        }
        calcAllTotal();
        barcodeSearch.setText("");

    }

    public void openEditStoreSalesPage(int id, int role, ArrayList<String> stores) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/editStoreSales.fxml"));
            loader.load();

            EditStoreSales editStoreSales = loader.getController();
            editStoreSales.setTextField(id, role, stores);
            Scene scene = new Scene(loader.getRoot());
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    //   logInController.openMainPage(id);
                }
            });
            stage.show();
        } catch (IOException ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openEditStoreSalesPage", ex);
        }
    }

    private static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }

}
