/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import helper.DbConnect;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import models.customer;

/**
 * FXML Controller class
 *
 * @author Ahmed Gabr
 */
public class ViewCompanesController implements Initializable {

    @FXML
    private TextField search;
    @FXML
    private TableView<customer> customerTable;
    @FXML
    private TableColumn<customer, String> editCol;
    @FXML
    private TableColumn<customer, String> telcol;
    @FXML
    private TableColumn<customer, String> namecol;
    @FXML
    private TableColumn<customer, String> idcol;

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    customer custmr = null;

    ObservableList<customer> customerList = FXCollections.observableArrayList();
    @FXML
    private TableColumn<customer, String> addresscol;
    @FXML
    private TableColumn<customer, String> emailcol;
    @FXML
    private Button btn_print;
    @FXML
    private TextField REMAIN;
    @FXML
    private TextField PAID;
    @FXML
    private TextField TOTAL;

    int USER_ID;
    int ROLE;

    MainController MainController = new MainController();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        loadDate();
        btn_print.setVisible(false);

        TOTAL.setEditable(false);
        PAID.setEditable(false);
        REMAIN.setEditable(false);
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

    @FXML
    private void AddCustomer(MouseEvent event) {
        close(event);
        openADDCompanePage(USER_ID, ROLE);
    }

    private void close(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void refresh() {
        try {
            customerList.clear();

            query = "SELECT * FROM `companes`";
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

            String total_text_all = "0.00";
            String paid_text_all = "0.00";
            String remain_text_all = "0.00";
            query = "select FORMAT(sum(total),2) as toal, FORMAT(sum(paid),2) as paid ,FORMAT(sum(remain),2) as remain from companysales ";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                total_text_all = resultSet.getString("toal");
                paid_text_all = resultSet.getString("paid");
                remain_text_all = resultSet.getString("remain");
            }

            TOTAL.setText(total_text_all + "");
            PAID.setText(paid_text_all + "");
            REMAIN.setText(remain_text_all + "");

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
                            alert.setHeaderText("هل انت متأكد من حذف العنصر");
                            alert.getButtonTypes().addAll(ButtonType.CANCEL);

                            Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                            Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
                            okButton.setText("موافق‚");
                            cancelButton.setText("الغاء");

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    try {
                                        event.consume();

                                        custmr = customerTable.getSelectionModel().getSelectedItem();
                                        query = "DELETE FROM `companes` WHERE id  =" + custmr.getId();
                                        connection = DbConnect.getConnect();
                                        preparedStatement = connection.prepareStatement(query);
                                        preparedStatement.execute();
                                        refresh();
                                    } catch (SQLException ex) {
                                        STATICDATA.ExceptionHandle(this.getClass().getName(), "loadDate - delete", ex);
                                    }

                                } else if (response == ButtonType.CANCEL) {

                                }
                            });

                        });
                        editIcon.setOnMouseClicked((MouseEvent event) -> {

                            custmr = customerTable.getSelectionModel().getSelectedItem();

                            Stage stagemain = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            stagemain.close();

                            openEditCompanePage(USER_ID, ROLE, custmr);

                        });

                        open.setOnMouseClicked((MouseEvent event) -> {

                            Stage stagemain = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            stagemain.close();

                            custmr = customerTable.getSelectionModel().getSelectedItem();
                            openCompanyProfile(USER_ID, ROLE, custmr.getId(), custmr.getName());

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

    @FXML
    private void print(MouseEvent event) {
    }

    public void openCompanyProfile(int userId, int role, int cusId, String name) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/viewCompanyProfile.fxml"));
            loader.load();

            ViewCompanyProfileController viewCompanyProfile = loader.getController();
            viewCompanyProfile.setTextField(userId, role, cusId, name);
            viewCompanyProfile.loadDate();
            Parent parent = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.setTitle("شركة " + name);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    MainController.openCompanesPage(userId, role);
                }
            });
            stage.show();

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openCompanyProfile", ex);
        }

    }

    public void openADDCompanePage(int id, int role) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/addCompany.fxml"));
            loader.load();

            AddCompanyController addCompanyController = loader.getController();
            addCompanyController.setTextField(id, role);
            Scene scene = new Scene(loader.getRoot());
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    MainController.openCompanesPage(id, role);
                }
            });
            stage.show();
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openADDCompanePage", ex);
        }
    }

    public void openEditCompanePage(int id, int role , customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/addCompany.fxml"));
            loader.load();

            AddCompanyController addCompanyController = loader.getController();
            addCompanyController.setTextField(id, role);
            addCompanyController.setUpdate(true);
            addCompanyController.setTextField(customer);
            Scene scene = new Scene(loader.getRoot());
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    MainController.openCompanesPage(id, role);
                }
            });
            stage.show();
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openEditCompanePage", ex);
        }
    }

    void setTextField(int id, int role) {
        USER_ID = id;
        ROLE = role;
    }

}
