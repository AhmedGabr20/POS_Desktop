package controllers;

import PDF.salesReport;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
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
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import models.sales;

public class UserProfileController implements Initializable {

    @FXML
    private TableView<sales> salesTable;
    @FXML
    private TableColumn<String, String> remainCol;
    @FXML
    private TableColumn<String, String> paidCol;
    @FXML
    private TableColumn<String, String> priceCol;
    @FXML
    private TableColumn<String, String> nameCol;
    @FXML
    private TableColumn<String, String> dateCol;
    @FXML
    private TableColumn<String, String> codeCol;
    @FXML
    private TextField search;
    @FXML
    private Text user_id_field;
    @FXML
    private Text userNameField;

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    sales sale = null;

    ObservableList<sales> salesList = FXCollections.observableArrayList();

    long millis = System.currentTimeMillis();
    java.sql.Date CDate = new java.sql.Date(millis);
    String currentDate = CDate + "";

    int hour = Calendar.getInstance().get(Calendar.HOUR);
    int min = Calendar.getInstance().get(Calendar.MINUTE);

    STATICDATA pdfPath = new STATICDATA();

    //String invoicefileLocation;
    String salesfileLocation;

    DecimalFormat decimal = new DecimalFormat("#.##");
    
    double all_total , all_paid , all_remain ;
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
    
     String FROM_DATE;
    String TO_DATE;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
         from_date.setValue(LocalDate.now());
        to_date.setValue(LocalDate.now());

        FROM_DATE = from_date.getValue() + "";
        TO_DATE = to_date.getValue() + "";
        
         loadDate();

        to_date.setOnAction(e -> {

            FROM_DATE = from_date.getValue() + "";
            TO_DATE = to_date.getValue() + "";

            loadDate();
        });

        from_date.setOnAction(e -> {

            to_date.setValue(null);
        });
        
       
    }

    private void refresh() {
        all_total = 0.00;
        all_remain = 0.00;
        all_paid = 0.00;

        try {
            salesList.clear();

            query = "select sales.id , sales.date , sales.price , sales.paid , customers.name ,user.name as username from sales "
                    + ", customers ,user where sales.cus_id = customers.id AND sales.user_id= user.id AND user.id=? AND date between ? AND ? order by date";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(user_id_field.getText()));
            preparedStatement.setString(2, FROM_DATE);
            preparedStatement.setString(3, TO_DATE);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                double total = resultSet.getDouble("price") ;
                double paid = resultSet.getDouble("paid") ;
                double remain =  total - paid ;
                String remain_txt = decimal.format(remain);
                double REMAIN = Double.parseDouble(remain_txt);
                
                String total_txt = decimal.format(total);
                if (total_txt.equals("0")) {
                    total_txt = "سداد";
                }

                salesList.add(new sales(
                        resultSet.getInt("id"),
                        total_txt,
                        paid,
                        REMAIN,
                        resultSet.getString("name"),
                        0,
                        resultSet.getString("date"),
                        resultSet.getString("username")));

                salesTable.setItems(salesList);
                
                 all_total += total ;
                all_paid += paid ;
                all_remain += remain ;

            }

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "refresh", ex);
        }
        
        TOTAL.setText(decimal.format(all_total));
        PAID.setText(decimal.format(all_paid));
        REMAIN.setText(decimal.format(all_remain));

    }

    public void loadDate() {

        connection = DbConnect.getConnect();
        refresh();

        codeCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("total_txt"));
        paidCol.setCellValueFactory(new PropertyValueFactory<>("paid"));
        remainCol.setCellValueFactory(new PropertyValueFactory<>("remain"));

        codeCol.setStyle("-fx-alignment: CENTER;");
        dateCol.setStyle("-fx-alignment: CENTER;");
        nameCol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");
        priceCol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");
        paidCol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");
        remainCol.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");

        salesTable.setItems(salesList);

    }

    @FXML
    private void save(MouseEvent event) {

        salesReport saleReport;
        saleReport = new salesReport();
        try {
        // Add Data To Report   
        String[] header = {"كود الفاتورة", "العميل", "التاريخ", "الاجمالي", "المدفوع", "المتبقي"};

        ArrayList<String[]> salelist = new ArrayList<>();
        ArrayList<Double> totalList = new ArrayList<>();
        ArrayList<Double> paidList = new ArrayList<>();
        ArrayList<Double> remainList = new ArrayList<>();

        String CustomerName = userNameField.getText();

        for (int i = 0; i < salesList.size(); i++) {

            sales s = salesList.get(i);
            String SaleId = String.valueOf(s.getId());
            String customerName = s.getName();
            String InvDATE = s.getDate();
            String Total = s.getTotal_txt();
            String paid = String.valueOf(s.getPaid());
            String remain = String.valueOf(s.getRemain());
            String userName = s.getUserName();

            String[] data = {SaleId, customerName, InvDATE, Total, paid, remain};
            salelist.add(data);

        }


          //  int remain = sum - paid ;
        LocalDate date = LocalDate.now();
        String Date = date + "";

        String userName = userNameField.getText();
           // String userSalesLocation = salesfileLocation+userName;

        String PDFNAME = "مبيعات " + userName + currentDate + ".pdf";

        saleReport.openDocument(PDFNAME, salesfileLocation);
        saleReport.addCompanyInfo(Date, CustomerName, salelist.size());
        saleReport.addTable(header, salelist);

        saleReport.addTotalTable(decimal.format(all_paid), decimal.format(all_total), decimal.format(all_remain));

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
                String code = cust.getId() + "";

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

    public void setTextField(int id, String name) {

        user_id_field.setText(id + "");
        userNameField.setText(name);
        user_id_field.setVisible(false);

        salesfileLocation = pdfPath.PDF_MAIN_FOLDER + pdfPath.PDF_USER_SALES_FOLDER;

    }

}
