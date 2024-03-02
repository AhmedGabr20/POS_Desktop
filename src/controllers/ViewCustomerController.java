package controllers;

import PDF.Templete;
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
import java.util.ArrayList;
import java.util.Calendar;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.customer;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author Ahmed Farahat
 */
public class ViewCustomerController implements Initializable {

    @FXML
    private TableView<customer> customerTable;
    @FXML
    private TableColumn<customer, String> addresscol;
    @FXML
    private TableColumn<customer, String> emailcol;
    @FXML
    private TableColumn<customer, String> telcol;
    @FXML
    private TableColumn<customer, String> namecol;
    @FXML
    private TableColumn<customer, String> idcol;
    @FXML
    private TableColumn<customer, String> editCol;
    @FXML
    private TextField search;
    @FXML
    private TextField REMAIN;
    @FXML
    private TextField PAID;
    @FXML
    private TextField TOTAL;

    DecimalFormat decimal = new DecimalFormat("#.##");

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    customer custmr = null;

    ObservableList<customer> customerList = FXCollections.observableArrayList();

    long millis = System.currentTimeMillis();
    java.sql.Date CDate = new java.sql.Date(millis);
    String currentDate = CDate + "";

    int hour = Calendar.getInstance().get(Calendar.HOUR);
    int min = Calendar.getInstance().get(Calendar.MINUTE);

    STATICDATA pdfPath = new STATICDATA();

    String customerfileLocation;

    String PDFNAME = "تقرير عملاء " + currentDate + "_" + hour + "-" + min + ".pdf";

    int USER_ID;
    int ROLE;

    MainController MainController = new MainController();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadDate();

        customerfileLocation = pdfPath.PDF_MAIN_FOLDER + pdfPath.PDF_CUSTOMERS_FOLDER + "//";

        TOTAL.setEditable(false);
        PAID.setEditable(false);
        REMAIN.setEditable(false);

    }

    @FXML
    private void AddCustomer(MouseEvent event) {
        close(event);
        openADDCustomerPage(USER_ID, ROLE);
    }

    @FXML
    private void print(MouseEvent event) {

        Templete templete = new Templete();

        try {
            templete.openDocument(PDFNAME, customerfileLocation);

            String[] header = {"الكود", "الأسم", "التليفون", "الأيميل", "العنوان"};
            ArrayList<String[]> item = new ArrayList<String[]>();
            for (int i = 0; i < customerList.size(); i++) {

                customer c = customerList.get(i);
                String id = String.valueOf(c.getId());
                String name = c.getName();
                String phone = c.getPhone();
                String email = c.getEmail();
                String addr = c.getAddress();

                String[] data = {id, name, phone, email, addr};
                item.add(data);
            }

            templete.addTable(header, item);
            templete.closeDocument();

            Desktop.getDesktop().open(new File(customerfileLocation));
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "print", ex);
        }

    }

    private void close(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void refresh() {
        try {

            customerList.clear();

            query = "SELECT * FROM `customers`";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                customerList.add(new customer(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("phone"),
                        resultSet.getString("email"),
                        resultSet.getString("address")));

                customerTable.setItems(customerList);

            }

            preparedStatement.close();
            resultSet.close();

            double all_total = 0;
            double all_paid = 0;
            double all_remain = 0;
            String paid_text_all = "0.00";
            String remain_text_all = "0.00";
            query = "select sum(price) as toal  from sales ";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                all_total = resultSet.getDouble("toal");
            }
            preparedStatement.close();
            resultSet.close();

            query = "select sum(price) as toal  from outsales ";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                all_total += resultSet.getDouble("toal");
            }

            preparedStatement.close();
            resultSet.close();

            query = "select sum(paid) as toal  from outsales_pay ";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                all_paid = resultSet.getDouble("toal");
            }

            preparedStatement.close();
            resultSet.close();

            query = "select sum(paid) as toal  from sales_pay ";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                all_paid += resultSet.getDouble("toal");
            }

            all_remain = all_total - all_paid;
            TOTAL.setText(decimal.format(all_total));
            PAID.setText(decimal.format(all_paid));
            REMAIN.setText(decimal.format(all_remain));

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "refresh", ex);
        }
    }

    private void loadDate() {

        connection = DbConnect.getConnect();
        refresh();

        idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        namecol.setCellValueFactory(new PropertyValueFactory<>("name"));
        telcol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailcol.setCellValueFactory(new PropertyValueFactory<>("email"));
        addresscol.setCellValueFactory(new PropertyValueFactory<>("address"));

        idcol.setStyle("-fx-alignment: CENTER;");
        namecol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");
        telcol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;");
        emailcol.setStyle("-fx-alignment: CENTER;");
        addresscol.setStyle("-fx-alignment: CENTER;");

        //add cell of button edit 
        Callback<TableColumn<customer, String>, TableCell<customer, String>> cellFoctory = (TableColumn<customer, String> param) -> {
            // make cell containing buttons
            final TableCell<customer, String> cell = new TableCell<customer, String>() {
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
                        FontAwesomeIconView open = new FontAwesomeIconView(FontAwesomeIcon.USER);

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

                            Button button1 = new Button();
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("هل انت متاكد من حذف العميل");
                            alert.setContentText("سوف يتم حذف جميع مبيعات هذا العميل");
                            alert.getButtonTypes().addAll(ButtonType.CANCEL);

                            Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                            Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
                            okButton.setText("موافق");
                            cancelButton.setText("إلغاء");

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    try {
                                        event.consume();
                                        custmr = customerTable.getSelectionModel().getSelectedItem();
                                        query = "DELETE FROM `customers` WHERE id  =" + custmr.getId();
                                        connection = DbConnect.getConnect();
                                        preparedStatement = connection.prepareStatement(query);
                                        preparedStatement.execute();
                                        refresh();

                                    } catch (SQLException ex) {
                                        STATICDATA.ExceptionHandle(this.getClass().getName(), "loadData - delete", ex);
                                    }
                                } else if (response == ButtonType.CANCEL) {

                                }
                            });

                        });
                        editIcon.setOnMouseClicked((MouseEvent event) -> {

                            Stage stagemain = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            stagemain.close();

                            custmr = customerTable.getSelectionModel().getSelectedItem();
                            openEditCustomerPage(USER_ID, ROLE, custmr);
                        });

                        open.setOnMouseClicked((MouseEvent event) -> {

                            Stage stagemain = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            stagemain.close();

                            custmr = customerTable.getSelectionModel().getSelectedItem();

                            openCustomerProfile(USER_ID, ROLE, custmr.getId(), custmr.getName());

                        });

                        HBox managebtn = new HBox(editIcon, deleteIcon, open);
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
        customerTable.setItems(customerList);

    }

    private void loadDataByName(String searchName) {

        connection = DbConnect.getConnect();
        try {
            customerList.clear();

            query = "SELECT * FROM `customers` where name LIKE ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, searchName);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                customerList.add(new customer(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("phone"),
                        resultSet.getString("email"),
                        resultSet.getString("address")));

                customerTable.setItems(customerList);
            }

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "loadDataByName", ex);
        }

        idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        namecol.setCellValueFactory(new PropertyValueFactory<>("name"));
        telcol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailcol.setCellValueFactory(new PropertyValueFactory<>("email"));
        addresscol.setCellValueFactory(new PropertyValueFactory<>("address"));

    }

    private void search(MouseEvent event) {

        loadDataByName("%" + search.getText() + "%");

    }

    @FXML
    private void searchMeal(KeyEvent event) {

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<customer> filteredData = new FilteredList<>(customerList, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(cust -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (cust.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (cust.getPhone().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<customer> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(customerTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        customerTable.setItems(sortedData);

    }

    public void openCustomerProfile(int userId, int role, int cusId, String name) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/customerProfile.fxml"));
            loader.load();

            CustomerProfileController customerProfileController = loader.getController();
            customerProfileController.setTextField(userId, role, cusId, name);
            //customerProfileController.loadDate();
            // userProfileController.calcAllTotal();
            Parent parent = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.setTitle("العميل:  " + name);
            // stage.initStyle(StageStyle.UTILITY);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    MainController.openCustomerPage(userId, role);
                }
            });
            stage.show();
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openCustomerProfile", ex);
        }

    }

    public void openADDCustomerPage(int id, int role) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/addCustomer.fxml"));
            loader.load();

            AddCustomerController addCustomerController = loader.getController();
            addCustomerController.setTextField(id, role);
            Scene scene = new Scene(loader.getRoot());
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    MainController.openCustomerPage(id, role);
                }
            });
            stage.show();
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openADDCustomerPage", ex);
        }
    }

    public void openEditCustomerPage(int id, int role, customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/addCustomer.fxml"));
            loader.load();

            AddCustomerController addCustomerController = loader.getController();
            addCustomerController.setTextField(id, role);
            addCustomerController.setUpdate(true);
            addCustomerController.setTextField(customer);
            Scene scene = new Scene(loader.getRoot());
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    MainController.openCustomerPage(id, role);
                }
            });
            stage.show();
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openEditCustomerPage", ex);
        }
    }

    void setTextField(int id, int role) {
        USER_ID = id;
        ROLE = role;
    }

}
