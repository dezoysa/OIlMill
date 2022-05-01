package OilMill;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Balance {

    private static Stage stage;
    private int cashTot=0;
    @FXML private TextField n1000;
    @FXML private TextField n500;
    @FXML private TextField n100;
    @FXML private TextField n50;
    @FXML private TextField n20;
    @FXML private TextField nCoin;
    @FXML private TextField cTotal;
    @FXML private TextField total;
    @FXML private TextField balance;

    public void setStage(Stage stage, String t){
        this.stage = stage;
        int x=(int)Double.parseDouble(t);
        total.setText(Integer.toString(x));
        balance.setText(Integer.toString(x));
        cashTot=Integer.parseInt(total.getText());

        n1000.setText("0");
        n500.setText("0");
        n100.setText("0");
        n50.setText("0");
        n20.setText("0");
        nCoin.setText("0");
        cTotal.setText("0");

    }

    public void readCoin(KeyEvent keyEvent) {
        String value = nCoin.getText();
        KeyCode keyCode = keyEvent.getCode();

        if (value.matches("\\d+")) {
            if (keyCode.equals(KeyCode.ENTER)) {
                nCoin.setText(Integer.toString(Integer.parseInt(value)*1));
                int t=this.Total();
                int b=t-cashTot;
                cTotal.setText(Integer.toString(t));
                if(b<0) balance.setStyle("-fx-text-inner-color: red;");
                else balance.setStyle("-fx-text-inner-color: green;");
                balance.setText(Integer.toString(b));
                nCoin.requestFocus();
            }
        } else {
            nCoin.setText("0");
        }

        keyEvent.consume();

    }

    public void read20(KeyEvent keyEvent) {
        String value = n20.getText();
        KeyCode keyCode = keyEvent.getCode();

        if (value.matches("\\d+")) {
            if (keyCode.equals(KeyCode.ENTER)) {
                n20.setText(Integer.toString(Integer.parseInt(value)*20));
                int t=this.Total();
                int b=t-cashTot;
                cTotal.setText(Integer.toString(t));
                if(b<0) balance.setStyle("-fx-text-inner-color: red;");
                else balance.setStyle("-fx-text-inner-color: green;");
                balance.setText(Integer.toString(b));
                nCoin.requestFocus();
            }
        } else {
            n20.setText("0");
        }

        keyEvent.consume();
    }

    public void read50(KeyEvent keyEvent) {
        String value = n50.getText();
        KeyCode keyCode = keyEvent.getCode();

        if (value.matches("\\d+")) {
            if (keyCode.equals(KeyCode.ENTER)) {
                n50.setText(Integer.toString(Integer.parseInt(value)*50));
                int t=this.Total();
                int b=t-cashTot;
                cTotal.setText(Integer.toString(t));
                if(b<0) balance.setStyle("-fx-text-inner-color: red;");
                else balance.setStyle("-fx-text-inner-color: green;");
                balance.setText(Integer.toString(b));
                n20.requestFocus();
            }
        } else {
            n50.setText("0");
        }

        keyEvent.consume();
    }

    public void read100(KeyEvent keyEvent) {
        String value = n100.getText();
        KeyCode keyCode = keyEvent.getCode();

        if (value.matches("\\d+")) {
            if (keyCode.equals(KeyCode.ENTER)) {
                n100.setText(Integer.toString(Integer.parseInt(value)*100));
                int t=this.Total();
                int b=t-cashTot;
                cTotal.setText(Integer.toString(t));
                if(b<0) balance.setStyle("-fx-text-inner-color: red;");
                else balance.setStyle("-fx-text-inner-color: green;");
                balance.setText(Integer.toString(b));
                n50.requestFocus();
            }
        } else {
            n100.setText("0");
        }

        keyEvent.consume();
    }

    public void read500(KeyEvent keyEvent) {
        String value = n500.getText();
        KeyCode keyCode = keyEvent.getCode();

        if (value.matches("\\d+")) {
            if (keyCode.equals(KeyCode.ENTER)) {
                n500.setText(Integer.toString(Integer.parseInt(value)*500));
                int t=this.Total();
                int b=t-cashTot;
                cTotal.setText(Integer.toString(t));
                if(b<0) balance.setStyle("-fx-text-inner-color: red;");
                else balance.setStyle("-fx-text-inner-color: green;");
                balance.setText(Integer.toString(b));
                n100.requestFocus();
            }
        } else {
            n500.setText("0");
        }

        keyEvent.consume();
    }

    public void read1000(KeyEvent keyEvent) {
        String value = n1000.getText();
        KeyCode keyCode = keyEvent.getCode();

        if (value.matches("\\d+")) {
            if (keyCode.equals(KeyCode.ENTER)) {
                n1000.setText(Integer.toString(Integer.parseInt(value)*1000));
                int t=this.Total();
                int b=t-cashTot;
                cTotal.setText(Integer.toString(t));
                if(b<0) balance.setStyle("-fx-text-inner-color: red;");
                else balance.setStyle("-fx-text-inner-color: green;");
                balance.setText(Integer.toString(b));
                n500.requestFocus();
            }
        } else {
            n1000.setText("0");
        }

        keyEvent.consume();

    }

    private int Total(){
        int total=0;
        total+=Integer.parseInt(n1000.getText());
        total+=Integer.parseInt(n500.getText());
        total+=Integer.parseInt(n100.getText());
        total+=Integer.parseInt(n50.getText());
        total+=Integer.parseInt(n20.getText());
        total+=Integer.parseInt(nCoin.getText());
        return total;
    }
}
