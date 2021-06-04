package OilMill;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
       FXMLLoader loader = new FXMLLoader(getClass().getResource("OilMill.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        controller.prepareTable();
        primaryStage.setTitle("New Wellamadda Oil Mills");
        primaryStage.setScene(new Scene(root, 620, 720));

        primaryStage.setOnShown(event-> {
            controller.date.requestFocus();
        });

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
