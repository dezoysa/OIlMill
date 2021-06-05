package OilMill;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


public class Controller {
    @FXML public TableView<Product> tableView;
    @FXML private TableColumn<Product, String> code;
    @FXML private TableColumn<Product, String> name;
    @FXML private TableColumn<Product, String> unit;
    @FXML private TableColumn<Product, String> quantity;
    @FXML private TableColumn<Product, String> total;

    @FXML public TextField id;
    @FXML public DatePicker date;
    @FXML public Label cashTotal;
    @FXML public TextField quan;
    @FXML public TextField cashGiven;
    @FXML public Label balance;
    @FXML public Label billTotal;
    @FXML public Label productName;
    @FXML public TextField unitPrice;
    @FXML public Button stat;

    public static final String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DATABASE_URL = "jdbc:mysql://localhost/mill?";
    public static final String DATABASE_USERNAME = "sathindu";
    public static final String DATABASE_PASSWORD = "123456";
    private static final DataConnection data = new DataConnection();

    public static HashMap<Integer, String> productNames = null;
    public static HashMap<Integer, Integer> price = null;
    private List<Product> sales = null;
    private final List<Product> bill = new ArrayList<>();

    public static LocalDate currentDate = null;


    public void initialize() throws SQLException, ClassNotFoundException {
        data.DataAccessor(DATABASE_DRIVER, DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
        price = data.getPriceList();
        productNames = data.getProductNames();

        id.setDisable(true);
        unitPrice.setDisable(true);
        quan.setDisable(true);
        cashGiven.setDisable(true);

        LocalDate  localDate = LocalDate.now();
        date.setValue(localDate);
        stat.setText(this.productNames.toString());
    }

    public void prepareTable() {
        //  TableView tableView = new TableView();

        code.setCellValueFactory(new PropertyValueFactory<>("code"));

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setCellFactory(TextFieldTableCell.<Product>forTableColumn());

        unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        unit.setStyle("-fx-alignment: CENTER-RIGHT;");

        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantity.setStyle("-fx-alignment: CENTER-RIGHT;");

        total.setCellValueFactory(new PropertyValueFactory<>("total"));
        total.setStyle("-fx-alignment: CENTER-RIGHT;");
    }

    public String printDouble(double number){
        return String.format("%.2f",number);
    }

    public void readDate(KeyEvent keyEvent) throws SQLException {

        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            currentDate = date.getValue();
         //   System.out.println(currentDate.toString());

            if (sales != null) {
                sales.clear();
                tableView.getItems().clear();
            }
            if (currentDate != null) {
                sales = data.getSalesList(currentDate);
                tableView.getItems().addAll(sales);
                cashTotal.setText(this.printDouble(this.getTotal()));

                id.setDisable(false);
                id.requestFocus();
            }
            keyEvent.consume();
        }
    }

    public void readCode(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        String value = id.getText();

        if (keyCode.equals(KeyCode.SPACE)) {
            if(billTotal.getText().equals("")) return;
            if(Double.parseDouble(billTotal.getText())<=0) return;
            id.setDisable(true);
            quan.setText("");
            unitPrice.setText("");
            cashGiven.setDisable(false);
            cashGiven.requestFocus();

        } else if (value.matches("\\d+")) {
            int code = Integer.parseInt(value);
            productName.setText(productNames.get(code));

            if(bill.isEmpty()) {
                cashGiven.setText("");
                balance.setText("");
                billTotal.setText("");
            }

            if (keyCode.equals(KeyCode.ENTER)) {
                if(code == 0) {
                    id.setDisable(true);
                    unitPrice.setText("");
                    quan.setDisable(false);
                    quan.requestFocus();
                } else if(isCode(this.price.keySet(),code)){
                    id.setDisable(true);
                    unitPrice.setDisable(false);
                    unitPrice.requestFocus();
                    unitPrice.setText(Integer.toString(price.get(code)));
                    unitPrice.selectAll();
                } else id.setText("");
            }
        } else id.setText("");
        keyEvent.consume();
    }

    private boolean isCode(Set<Integer> codes,int num){
        for (int code:codes)
            if(code == num) return true ;

            return false;
    }

    public void setUnitPrice(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        String value = unitPrice.getText();

        if (keyCode.equals(KeyCode.ESCAPE)) {
            id.setDisable(false);
            unitPrice.setDisable(true);
            id.requestFocus();
        }

        if (value.matches("\\d+")) {
            if (keyCode.equals(KeyCode.ENTER)) {
                unitPrice.setDisable(true);
                quan.setDisable(false);
                quan.requestFocus();
            }
        } else {
            unitPrice.setText("");
        }
        keyEvent.consume();
    }

    public void readQuantity(KeyEvent keyEvent) throws SQLException {
        String idValue = id.getText();
        String unitValue = unitPrice.getText();

        KeyCode keyCode=keyEvent.getCode();
        if (keyCode.equals(KeyCode.ESCAPE)) {
            id.setDisable(false);
            quan.setDisable(true);
            id.requestFocus();
        }

        String quanValue = quan.getText();
     //   if(quanValue.matches("([+-]?\\d*\\.?\\d*)+")){
        if (quanValue.matches("[+-]?\\d{0,7}([\\.]\\d{0,2})?")) {
            if (!quanValue.isEmpty() && keyCode.equals(KeyCode.ENTER)) {
                int code = Integer.parseInt(idValue);
                int unitPrice =1;
                if(code!=0) unitPrice = Integer.parseInt(unitValue);
                double quantity = Double.parseDouble(quanValue);
                if (quantity != 0) this.addTableRaw(code, unitPrice, quantity);
                quan.setDisable(true);
                id.setDisable(false);
                id.requestFocus();
            }
        } else quan.setText("");
        keyEvent.consume();
    }

    public void addTableRaw(int code, int uniPrice,double quantity) throws SQLException {

        if (isCode(productNames.keySet(), code)) {
            Product p = new Product(code, productNames.get(code), uniPrice, quantity);
            tableView.getItems().add(p);
            tableView.scrollTo(p);
            if (code != 0) {
                bill.add(p);
            }
            data.putSale(currentDate, p);
            cashTotal.setText(this.printDouble(this.getTotal()));
            billTotal.setText(this.printDouble(this.getBillTotal()));
        } else {
            id.setText("");
            quan.setText("");
        }
    }

    public double getTotal() {
        ObservableList<Product> rows = tableView.getItems();
        int total = 0;
        for (Product p : rows) {
            total += Double.parseDouble(p.getTotal());
        }
        return total;
    }

    public double getBillTotal() {
        double total = 0;
        for (Product p : bill) {
            total += Double.parseDouble(p.getTotal());
        }
        return total;
    }

    public void readCash(KeyEvent keyEvent) throws SQLException{
        String value = cashGiven.getText();

        KeyCode keyCode=keyEvent.getCode();

        if (keyCode.equals(KeyCode.ESCAPE)) {
            id.setDisable(false);
            cashGiven.setDisable(true);
            id.requestFocus();
        }


        if (value.matches("\\d+")) {

            if (keyCode.equals(KeyCode.ENTER)) {
                int given = Integer.parseInt(cashGiven.getText());
                double total=this.getBillTotal();
                double bal = given - total;
                billTotal.setText(this.printDouble(total));
                balance.setText(this.printDouble(bal));

                printControl printBill=new printControl(total,given,bal);
              //  printBill.printView(bill);
                printBill.print(bill);

                if (bal < 0) {
                    Product p = new Product(0,"Cash",1, bal);
                    tableView.getItems().add(p);
                    data.putSale(currentDate, p);
                    cashTotal.setText(this.printDouble(this.getTotal()));
                }

                if (bill != null) bill.clear();
             //   balance.setText("");
               // billTotal.setText("");
               // cashGiven.setText("");
                id.setText("");
                unitPrice.setText("");
                quan.setText("");
                cashGiven.setDisable(true);
                id.setDisable(false);
                id.requestFocus();
            }
        } else cashGiven.setText("");
        keyEvent.consume();
    }

    public void closeApp(ActionEvent actionEvent) throws SQLException {
        data.shutdown();
        System.exit(0);
    }

    public void edit(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edit.fxml"));
        Parent root = loader.load();
        Edit edit = loader.getController();
        edit.setEditTable();
        Stage newWindow = new Stage();
        newWindow.setTitle("New Wellamadda Oil Mills");
        newWindow.setScene(new Scene(root));
        edit.setStage(newWindow);
        newWindow.showAndWait();
        stat.setText(this.productNames.toString());
    }

    public void stat(ActionEvent actionEvent) throws IOException, SQLException {
        if(this.currentDate==null) return;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edit.fxml"));
        Parent root = loader.load();
        Edit edit = loader.getController();
        edit.setStatTable();
        Stage newWindow = new Stage();
        newWindow.setTitle("New Wellamadda Oil Mills");
        newWindow.setScene(new Scene(root));
        edit.setStage(newWindow);
        newWindow.show();
    }
}
