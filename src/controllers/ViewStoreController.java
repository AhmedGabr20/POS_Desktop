package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import helper.DbConnect;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import models.store;

/**
 * FXML Controller class
 *
 * @author Ahmed Farahat
 */
public class ViewStoreController implements Initializable {

    @FXML
    private TextField editStoreName;
    @FXML
    private TableColumn<store, String> editCol;
    @FXML
    private TableColumn<store, String> nameCol;
    @FXML
    private TableColumn<store, String> codeCol;
    @FXML
    private TableView<store> storeTable;

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    store stre = null;

    ObservableList<store> storeList = FXCollections.observableArrayList();
    @FXML
    private TextField search;

    int USER_ID;
    int ROLE;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        loadDate();
    }

    @FXML
    private void AddStore(MouseEvent event) {

        String Name = editStoreName.getText();
        try {
            if (!Name.equals("")) {

                connection = DbConnect.getConnect();
                query = "INSERT INTO `store`(`storeName`) VALUES (?)";

                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, Name);
                preparedStatement.execute();
                refresh();
                editStoreName.setText("");

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("تم التسجيل");
                alert.showAndWait();

            } else {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("يجب كتابة اسم المخزن");
                alert.showAndWait();
            }
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "AddStore", ex);
        }

    }

    @FXML
    private void refresh() {

        try {
            storeList.clear();

            query = "SELECT * FROM `store`";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                storeList.add(new store(
                        resultSet.getInt("id"),
                        resultSet.getInt("storeItems"),
                        resultSet.getString("storeName")));
                storeTable.setItems(storeList);

            }

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "refresh", ex);
        }

    }

    private void loadDate() {

        connection = DbConnect.getConnect();
        refresh();

        codeCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("storeName"));

        codeCol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;");
        nameCol.setStyle("-fx-alignment: CENTER;-fx-font-size:24px;-fx-font-weight:bold;");

        //add cell of button edit 
        Callback<TableColumn<store, String>, TableCell<store, String>> cellFoctory = (TableColumn<store, String> param) -> {
            // make cell containing buttons
            final TableCell<store, String> cell = new TableCell<store, String>() {
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

                            Button button1 = new Button();
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("هل انت متاكد من حذف المخزن");
                            alert.setContentText("سوف يتم حذف جميع الاصناف بهذا المخزن");
                            alert.getButtonTypes().addAll(ButtonType.CANCEL);

                            Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                            Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
                            okButton.setText("موافق");
                            cancelButton.setText("إلغاء");

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    try {
                                        event.consume();
                                        stre = storeTable.getSelectionModel().getSelectedItem();
                                        query = "DELETE FROM `store` WHERE id  =" + stre.getId();
                                        connection = DbConnect.getConnect();
                                        preparedStatement = connection.prepareStatement(query);
                                        preparedStatement.execute();
                                        refresh();

                                    } catch (Exception ex) {
                                        STATICDATA.ExceptionHandle(this.getClass().getName(), "loadData - delete", ex);
                                    }
                                } else if (response == ButtonType.CANCEL) {

                                }
                            });

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

        storeTable.setItems(storeList);

    }

    @FXML
    private void searchMeal(KeyEvent event) {

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<store> filteredData = new FilteredList<>(storeList, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(cust -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (cust.getStoreName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<store> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(storeTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        storeTable.setItems(sortedData);

    }

    void setTextField(int id, int role) {
        USER_ID = id;
        ROLE = role;
    }

}
