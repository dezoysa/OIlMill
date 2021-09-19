package OilMill;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
       FXMLLoader loader = new FXMLLoader(getClass().getResource("OilMill.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        controller.prepareTable();
        primaryStage.setTitle("New Wellamadda Oil Mills");
        Scene scene= new Scene(root, 620, 720);
        scene.getRoot().setStyle("-fx-font-family: Courier; -fx-font-size: 14pt");


      //  scene.getRoot().setStyle(setStyleText);

        primaryStage.setScene(scene);

        primaryStage.setOnShown(event-> {
            controller.date.requestFocus();
        });

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
