package controllers;

import PDF.ReportTemplete;
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
import java.util.Calendar;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import models.User;
import models.sales;

public class DashBoardController implements Initializable {

    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<String, String> id_col;
    @FXML
    private TableColumn<String, String> name_col;
    @FXML
    private TableColumn<String, String> phone_col;
    @FXML
    private TableColumn<String, String> pass_col;
    @FXML
    private TableColumn<String, String> role_col;
    @FXML
    private TableColumn<User, String> edit_col;

    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    ObservableList<User> userList = FXCollections.observableArrayList();
    ObservableList<sales> salesList = FXCollections.observableArrayList();
    ObservableList<sales> salesListToday = FXCollections.observableArrayList();
    ObservableList<sales> expensesList = FXCollections.observableArrayList();

    User selectedUser;

    double AllTotal = 0;
    double Allremain = 0;
    double Allpaid = 0;

    double Exp_AllTotal = 0.00;
    double Exp_Allremain = 0.00;
    double Exp_Allpaid = 0.00;


    /*  
     ObservableList<sales> revenueList = FXCollections.observableArrayList();
     ObservableList<sales> expensesList = FXCollections.observableArrayList();

     double TOTAL_IN = 0.00;
     double PAID_IN = 0.00;
     double REMAIN_IN = 0.00;

     double TOTAL_OUT = 0.00;
     double PAID_OUT = 0.00;
     double REMAIN_OUT = 0.00;
     */
    long millis = System.currentTimeMillis();
    java.sql.Date CDate = new java.sql.Date(millis);

    String currentDate = CDate + "";

    @FXML
    private Label all_inv;
    @FXML
    private Label all_total;
    @FXML
    private Label all_paid;
    @FXML
    private Label all_remain;
    @FXML
    private Label exp_all_inv;
    @FXML
    private Label exp_all_total;
    @FXML
    private Label exp_all_paid;
    @FXML
    private Label exp_all_remain;

    STATICDATA pdfPath = new STATICDATA();
    String mainFolder;
    @FXML
    private Text total_Profit;

    DecimalFormat decimal = new DecimalFormat("#.##");

    @FXML
    private Text total_customers;
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

        from_date.setValue(LocalDate.now());
        to_date.setValue(LocalDate.now());

        FROM_DATE = from_date.getValue() + "";
        TO_DATE = to_date.getValue() + "";

        to_date.setValue(null);

        loadDate();

        calcAllTotals();
        calcAllExpenses();

        mainFolder = pdfPath.PDF_MAIN_FOLDER;

        total_customers.setText(totalCustomers() + "");

        to_date.setOnAction(e -> {

            FROM_DATE = from_date.getValue() + "";
            TO_DATE = to_date.getValue() + "";

            calcAllTotals();
            calcAllExpenses();

        });

        from_date.setOnAction(e -> {

            to_date.setValue(null);
        });

    }

    @FXML
    private void addUser(MouseEvent event) {

        Stage stagemain = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stagemain.close();
        openEditUser(USER_ID, ROLE, 0, "", "", "", "", false);

    }

    public void openEditUser(int AdminID, int Admin_Role, int UserID, String name, String phone, String password, String User_Role, boolean update) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/tableView/addUser.fxml"));
            loader.load();
            AddUserController addUserController = loader.getController();
            addUserController.setTextField(AdminID, Admin_Role, UserID, name, phone, password, User_Role, update);
            Parent parent = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    MainController mainController = new MainController();
                    mainController.openDashBoardPage(AdminID, Admin_Role);
                }
            });
            stage.show();
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openEditUser", ex);
        }
    }

    private void refresh() {
        try {
            userList.clear();

            query = "SELECT * FROM `user`";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                int role = resultSet.getInt("role");
                String role_name = null;
                if (role == 0) {
                    role_name = "موظف";
                } else {
                    role_name = "مشرف";
                }

                userList.add(new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("phone"),
                        resultSet.getString("password"),
                        role_name));

                usersTable.setItems(userList);

            }

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "refresh", ex);
        }

    }

    private void loadDate() {

        connection = DbConnect.getConnect();
        refresh();

        id_col.setCellValueFactory(new PropertyValueFactory<>("id"));
        name_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        phone_col.setCellValueFactory(new PropertyValueFactory<>("phone"));
        pass_col.setCellValueFactory(new PropertyValueFactory<>("password"));
        role_col.setCellValueFactory(new PropertyValueFactory<>("role_name"));

        id_col.setStyle("-fx-alignment: CENTER;");
        name_col.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;-fx-font-weight:bold;");
        phone_col.setStyle("-fx-alignment: CENTER;-fx-font-size:20px;");
        pass_col.setStyle("-fx-alignment: CENTER;");
        role_col.setStyle("-fx-alignment: CENTER;");

        //add cell of button edit 
        Callback<TableColumn<User, String>, TableCell<User, String>> cellFoctory = (TableColumn<User, String> param) -> {
            // make cell containing buttons
            final TableCell<User, String> cell = new TableCell<User, String>() {
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
                            alert.setHeaderText("هل انت متأكد من حذف المستخدم");
                            alert.getButtonTypes().addAll(ButtonType.CANCEL);

                            Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                            Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
                            okButton.setText("موافق‚");
                            cancelButton.setText("الغاء");

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    try {
                                        event.consume();

                                        selectedUser = usersTable.getSelectionModel().getSelectedItem();
                                        query = "DELETE FROM `user` WHERE id  =" + selectedUser.getId();
                                        connection = DbConnect.getConnect();
                                        preparedStatement = connection.prepareStatement(query);
                                        preparedStatement.execute();
                                        refresh();
                                    } catch (Exception ex) {
                                        STATICDATA.ExceptionHandle(this.getClass().getName(), "loadDate - delete", ex);
                                        
                                    }

                                } else if (response == ButtonType.CANCEL) {

                                }
                            });

                        });
                        editIcon.setOnMouseClicked((MouseEvent event) -> {

                            selectedUser = usersTable.getSelectionModel().getSelectedItem();

                            Stage stagemain = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            stagemain.close();

                            openEditUser(USER_ID, ROLE, selectedUser.getId(), selectedUser.getName(), selectedUser.getPhone(), selectedUser.getPassword(), selectedUser.getRole_name(), true);

                        });

                        open.setOnMouseClicked((MouseEvent event) -> {

                            selectedUser = usersTable.getSelectionModel().getSelectedItem();
                            FXMLLoader loader = new FXMLLoader();
                            loader.setLocation(getClass().getResource("/tableView/userProfile.fxml"));
                            try {
                                loader.load();
                            } catch (Exception ex) {
                                STATICDATA.ExceptionHandle(this.getClass().getName(), "loadDate - open", ex);
                            }

                            UserProfileController userProfileController = loader.getController();
                            userProfileController.setTextField(selectedUser.getId(), selectedUser.getName());
                            userProfileController.loadDate();
                            // userProfileController.calcAllTotal();
                            Parent parent = loader.getRoot();
                            Stage stage = new Stage();
                            stage.setScene(new Scene(parent));
                            stage.setTitle("المستخدم:  " + selectedUser.getName());
                            // stage.initStyle(StageStyle.UTILITY);
                            stage.show();

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
        edit_col.setCellFactory(cellFoctory);
        usersTable.setItems(userList);

    }

    private void calcAllTotals() {

        try {
            salesList.clear();
            // get Data from sales table 

            query = "select sales.id , sales.date , sales.price , sales.profit , customers.name ,user.name as username "
                    + "from sales , customers , user where sales.cus_id = customers.id AND sales.user_id= user.id "
                    + "AND sales.date between ? AND ? order by sales.date";

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, FROM_DATE);
            preparedStatement.setString(2, TO_DATE);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                salesList.add(new sales(
                        resultSet.getInt("id"),
                        resultSet.getDouble("price"),
                        0,
                        0,
                        resultSet.getString("name"),
                        resultSet.getString("date"),
                        resultSet.getString("username"),
                        resultSet.getDouble("profit")));

            }

            preparedStatement.close();
            resultSet.close();

            // get Data from OutSales table 
            query = "select outsales.id , outsales.date , outsales.price  , customers.name ,user.name as username "
                    + "from outsales , customers , user where outsales.cus_id = customers.id AND outsales.user_id= user.id "
                    + "AND outsales.date between ? AND ? order by outsales.date";

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, FROM_DATE);
            preparedStatement.setString(2, TO_DATE);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                salesList.add(new sales(
                        resultSet.getInt("id"),
                        resultSet.getDouble("price"),
                        0,
                        0,
                        resultSet.getString("name"),
                        resultSet.getString("date") + " (خارجي) ",
                        resultSet.getString("username"),
                        0));

            }

            preparedStatement.close();
            resultSet.close();

            query = "select outsales_pay.id , outsales_pay.date , outsales_pay.paid  , customers.name ,user.name as username "
                    + "from outsales_pay , customers , user where outsales_pay.cus_id = customers.id AND outsales_pay.user_id= user.id "
                    + "AND outsales_pay.date between ? AND ? order by outsales_pay.date";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, FROM_DATE);
            preparedStatement.setString(2, TO_DATE);
            resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                double Remain = 0 - resultSet.getDouble("paid");
                String remain_tx = decimal.format(Remain);
                double REMAIN = Double.parseDouble(remain_tx);
                salesList.add(new sales(
                        resultSet.getInt("id"),
                        0,
                        resultSet.getDouble("paid"),
                        REMAIN,
                        resultSet.getString("name"),
                        resultSet.getString("date") + " (خارجي) ",
                        resultSet.getString("username"),
                        0));

            }
            
            preparedStatement.close();
            resultSet.close();
            
            query = "select sales_pay.id , sales_pay.date , sales_pay.paid , customers.name ,user.name as username "
                    + "from sales_pay , customers , user where sales_pay.cus_id = customers.id AND sales_pay.user_id= user.id "
                    + "AND sales_pay.date between ? AND ? order by sales_pay.date";

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, FROM_DATE);
            preparedStatement.setString(2, TO_DATE);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                double Remain = 0 - resultSet.getDouble("paid");
                String remain_tx = decimal.format(Remain);
                double REMAIN = Double.parseDouble(remain_tx);
                salesList.add(new sales(
                        resultSet.getInt("id"),
                        0,
                        resultSet.getDouble("paid"),
                        REMAIN,
                        resultSet.getString("name"),
                        resultSet.getString("date"),
                        resultSet.getString("username"),
                        0));

            }
            

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "calcAllTotals", ex);
        }

        ArrayList<Double> result = loopTotals(salesList);
        AllTotal = result.get(0);
        Allpaid = result.get(1);
        Allremain = result.get(2);
        total_Profit.setText(result.get(3) + "");

        all_inv.setText(salesList.size() + "");
        all_total.setText(decimal.format(AllTotal));
        all_paid.setText(decimal.format(Allpaid));
        all_remain.setText(decimal.format(Allremain));

    }

    private void calcAllExpenses() {

        try {
            expensesList.clear();

            //   `item`,`total`, `paid`, `remain`, `date`, `cus_id`,`user_id`, `link`
            query = "SELECT companysales.item , companysales.total , companysales.paid , companysales.remain , "
                    + "companysales.date , companes.name as customername ,user.name as username  "
                    + "FROM (`companysales` left join companes on companysales.cus_id = companes.id left join user on companysales.user_id= user.id) "
                    + "where companysales.date between ? AND ? order by companysales.date ";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, FROM_DATE);
            preparedStatement.setString(2, TO_DATE);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                expensesList.add(new sales(
                        resultSet.getDouble("total"),
                        resultSet.getDouble("paid"),
                        resultSet.getDouble("remain"),
                        resultSet.getString("item"),
                        resultSet.getString("date"),
                        resultSet.getString("username"),
                        resultSet.getString("customername")
                ));
            }

            ArrayList<Double> result = loopTotals(expensesList);
            Exp_AllTotal = result.get(0);
            Exp_Allpaid = result.get(1);
            Exp_Allremain = result.get(2);
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "calcAllExpenses", ex);
        }

        exp_all_inv.setText(expensesList.size() + "");
        exp_all_total.setText(decimal.format(Exp_AllTotal));
        exp_all_paid.setText(decimal.format(Exp_Allpaid));
        exp_all_remain.setText(decimal.format(Exp_Allremain));

    }

    public ArrayList<Double> loopTotals(ObservableList<sales> list) {

        double total = 0;
        double paid = 0;
        double remian = 0;
        double profit = 0;

        ArrayList<Double> totalList = new ArrayList<>();
        ArrayList<Double> paidList = new ArrayList<>();
        ArrayList<Double> remainList = new ArrayList<>();
        ArrayList<Double> profitList = new ArrayList<>();

        ArrayList<Double> result = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            sales s = list.get(i);

            double totalNUM = s.getPrice();
            double paidNUM = s.getPaid();
            double remainNUM = totalNUM - paidNUM ;
            double profitNUM = s.getProfit();
            totalList.add(totalNUM);
            paidList.add(paidNUM);
            remainList.add(remainNUM);
            profitList.add(profitNUM);

        }

        for (int i = 0; i < totalList.size(); i++) {
            total = total + totalList.get(i);
            paid = paid + paidList.get(i);
            remian = remian + remainList.get(i);
            profit = profit + profitList.get(i);
        }

        result.add(total);
        result.add(paid);
        result.add(remian);
        result.add(profit);

        return result;

    }

    private void inv_folder(MouseEvent event) {

        String invLocation = mainFolder + pdfPath.PDF_INV_FOLDER;
        openFolder(invLocation);

    }

    private void sales_folder(MouseEvent event) {

        String salesfileLocation = mainFolder + pdfPath.PDF_SALES_FOLDER;

        openFolder(salesfileLocation);

    }

    @FXML
    private void report_folder(MouseEvent event) {

        openFolder(mainFolder);

    }

    public void openFolder(String location) {

        try {
            Desktop.getDesktop().open(new File(location));
        } catch (IOException ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "openFolder", ex);
        }
    }

    public int totalCustomers() {
        int number = 0;
        try {
            query = "SELECT count(*) as total FROM `customers`";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                number = resultSet.getInt("total");
            }

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "totalCustomers", ex);
        }

        return number;
    }

    public void backUpMethod() {

        try {

            String folderPath = STATICDATA.PDF_BackUp_FOLDER;
            // String folderPath = "G://BACKUP";
            File f1 = new File(folderPath);
            f1.mkdir();
            String savePath = folderPath + "\\backUp" + "elnoor.sql";

            String dbName = "elnoor_v2";
            String dbUser = DbConnect.getUser();
            String dbPass = DbConnect.getPass();

            //String executeCmd = "c:\\Program Files\\MySQL\\MySQL Workbench 8.0\\mysqldump --no-defaults --user=hbstudent --password=hbstudent -h localhost center -r G://ba.sql" ;
            String executeCmd = "c:\\Program Files\\MySQL\\MySQL Workbench 8.0\\mysqldump --no-defaults --user=" + dbUser + "  --password=" + dbPass + "  -h localhost " + dbName + " -r " + savePath;

            Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);

            int processComplete = runtimeProcess.waitFor();

            if (processComplete == 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                alert.setContentText("تم عمل نسخ احتياطي");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("حدث خطأ في النسح");
                alert.showAndWait();
            }
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "backUpMethod", ex);
        }

    }

    @FXML
    private void save(MouseEvent event) {

        STATICDATA pdfPath = new STATICDATA();
        String MAIN_PATH = pdfPath.PDF_MAIN_FOLDER;
        String EXP_REV = pdfPath.PDF_REPORT_FOLDER;

        int sec = Calendar.getInstance().get(Calendar.SECOND);
        int MIN = Calendar.getInstance().get(Calendar.MINUTE);
        int HOUR = Calendar.getInstance().get(Calendar.HOUR);
        String DOCNAME = "تقرير يوم " + LocalDate.now() + "-(" + HOUR + "-" + MIN + "-" + sec + ")";

        String path = MAIN_PATH + EXP_REV;

        ReportTemplete templete;
        try {
            
        
        templete = new ReportTemplete();

        templete.openDocument(DOCNAME, path);

        String[] Revenueheader = {"التاريخ", "اسم العميل", "اسم المستخدم", "اجمالي", "مدفوع"};
        String[] Expensesheader = {"التاريخ", "اسم الشركة", "اسم المستخدم", "البيان", "اجمالي", "مدفوع"};

        ArrayList<String[]> RevenueItem = new ArrayList<String[]>();
        ArrayList<String[]> ExpensesItems = new ArrayList<String[]>();

        for (sales revenueList1 : salesList) {

            String customername = revenueList1.getName();
            String date = revenueList1.getDate();
            String username = revenueList1.getUserName();
            double total = revenueList1.getPrice();
            double paid = revenueList1.getPaid();
            double remain = revenueList1.getRemain();
            
            String totalString ;
            if(total==0){
                totalString = "سداد";
            }else{
                totalString = decimal.format(total) ;
            }

            String[] data = {date, customername, username, totalString, decimal.format(paid)};
            RevenueItem.add(data);
        }

        for (sales expensesList1 : expensesList) {

            String name = expensesList1.getName();
            String customername = expensesList1.getCustomerName();
            String date = expensesList1.getDate();
            String username = expensesList1.getUserName();
            double total = expensesList1.getPrice();
            double paid = expensesList1.getPaid();
            double remain = expensesList1.getRemain();

            String totalString ;
            if(total==0){
                totalString = "سداد";
            }else{
                totalString = decimal.format(total) ;
            }
            String[] data = {date, customername, username, name, totalString, decimal.format(paid)};
            ExpensesItems.add(data);
        }

        templete.addTopHeader("تقرير إيرادات ومصروفات", "في الفترة من :   " + FROM_DATE, "    إلــــــي:   " + TO_DATE);

        templete.revenueTable(Revenueheader, RevenueItem);
        templete.expensesTable(Expensesheader, ExpensesItems, "جــــــــدول الـــــمــــــصـــــــروفـــــــات");

        templete.addInfo(all_paid.getText(), exp_all_paid.getText());
        templete.closeDocument();

        //  close(event);
            Desktop.getDesktop().open(new File(path));
        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "save", ex);
            STATICDATA.AlertMessage(ex.getMessage());
        }

    }

    @FXML
    private void backUp(MouseEvent event) {
        backUpMethod();
        sendMail();
    }

    private void close(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    void setTextField(int id, int role) {
        USER_ID = id;
        ROLE = role;
    }

    public void sendMail() {

        //      
        System.out.println("Preparing to send email");
        Properties properties = new Properties();

        //Enable authentication
        properties.put("mail.smtp.auth", "true");
        //Set TLS encryption enabled
        properties.put("mail.smtp.starttls.enable", "true");
        //Set SMTP host
        properties.put("mail.smtp.host", "smtp.gmail.com");
        //Set smtp port
        properties.put("mail.smtp.port", "587");

        //Your gmail address
        String myAccountEmail = "ahmedaly.g.2020@gmail.com";

        String to = "ahmedaligabr.20@gmail.com";
        //Your gmail password
        String password = "hhmjmtvacywknthu";

        //Create a session with account credentials
        // Get the Session object.
        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(myAccountEmail, password);
                    }
                });

        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(myAccountEmail));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject("Testing Subject");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setText("This is message body");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            String filename = "E://mmm.png";
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (Exception ex) {
            STATICDATA.ExceptionHandle(this.getClass().getName(), "sendMail", ex);
        }

    }
}
