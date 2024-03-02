package Main;

import Logging.CreateFile;
import controllers.STATICDATA;
import helper.DbConnect;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.text.Format;
import java.text.SimpleDateFormat;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.image.Image;


/**
 *
 * @author Ahmed Farahat
 */
public class Main extends Application {

    String PDF_PARTATION = null;

   // private static final Logger logger = LogManager.getLogger(Main.class);
    @Override
    public void start(Stage primaryStage) {

       
        Connection connection = DbConnect.getConnect();
        
        
        if (connection == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("خطأ الأتصال بقاعدة البيانات");
            alert.showAndWait();
            STATICDATA.ExceptionHandle(this.getClass().getName(), "start", "\"خطأ الأتصال بقاعدة البيانات\"");

        } else {

            try {
               
                Parent parent = FXMLLoader.load(getClass().getResource("/tableView/logIn.fxml"));
                Scene scene = new Scene(parent);
                primaryStage.setScene(scene);
                primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/img/company_logo.png")));
                primaryStage.show();
            } catch (Exception ex) {
                STATICDATA.ExceptionHandle(this.getClass().getName(), "start", ex);
            }

        }
    }

    public static void main(String[] args) {
        
        try {
            long millis = System.currentTimeMillis();
            Format format = new SimpleDateFormat("EEEE");
            String currentDay = format.format(new java.sql.Date(millis));
            
            CreateFile cf = new CreateFile();
            System.setOut(new PrintStream(cf.getFile(currentDay)));
            
        } catch (IOException ex) {
            STATICDATA.ExceptionHandle("Main.java", "start", ex);
        }
        
        launch(args);
    }
}
