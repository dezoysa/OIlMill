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

 //   public static final String DATABASE_URL = "jdbc:mysql://localhost/mill?";
    public static final String DATABASE_URL = "jdbc:mysql://sg2plcpnl0239.prod.sin2.secureserver.net/mill?";
    public static final String DATABASE_USERNAME = "sathindu";
    public static final String DATABASE_PASSWORD = "Kasun@home1";

    //public static final String DATABASE_USERNAME = "user";
    //public static final String DATABASE_PASSWORD = "sathindu";

    private static final DataConnection data = new DataConnection();

    public static HashMap<Integer, String> productNames = null;
    public static HashMap<Integer, Integer> price = null;
    private List<Product> sales = null;
    private final List<Product> bill = new ArrayList<>();

    public static LocalDate currentDate = null;


    // This function connect the database and initialize fields.
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
        stat.setText(productNames.toString());

    }

    // This function shows the table view
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

    //Check the product code
    private boolean isCode(Set<Integer> codes,int num){
        for (int code:codes)
            if(code == num) return true ;

        return false;
    }

    //Read the date
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

    //Step 1: Reading the product code
    public void readCode(KeyEvent keyEvent) throws SQLException {
        KeyCode keyCode = keyEvent.getCode();
        String value = id.getText();

        if (keyCode.equals(KeyCode.SPACE)) {
            //Moving to Step 4: Finalizing  the bill
            if(billTotal.getText().equals("")) return;
            if(Double.parseDouble(billTotal.getText())<=0) return;
            id.setDisable(true);
            cashGiven.setDisable(false);
            cashGiven.requestFocus();

        } else if (keyCode.equals(KeyCode.ESCAPE)){
            //Delete the bill and start it over
            this.deleteBill();

        } else if (value.matches("\\d+")) {

            //Reading the code number
            int code = Integer.parseInt(value);
            productName.setText(productNames.get(code));

            //initializing a new bill
            if(bill.isEmpty()) {
                cashGiven.setText("");
                balance.setText("");
                billTotal.setText("");
            }

            if (keyCode.equals(KeyCode.ENTER)) {

                if(code == 0) {
                    //if code is cash moving to the step 3
                    // Reading product Quantity
                    id.setDisable(true);
                    unitPrice.setText("");
                    quan.setDisable(false);
                    quan.requestFocus();
                } else if(isCode(price.keySet(),code)){

                    //In case of valid product code moving to the step 2
                    // Reading product Quantity
                    id.setDisable(true);
                    quan.setText("");
                    unitPrice.setDisable(false);

                    unitPrice.setText(Integer.toString(price.get(code)));
                    //Jump to Quantity
                    quan.requestFocus();
                    quan.setDisable(false);
                    quan.requestFocus();

                } else id.setText("");
            }
        } else id.setText("");
        keyEvent.consume();
    }


    //Step 2: Reading the unit price
    public void setUnitPrice(KeyEvent keyEvent) throws SQLException {
        KeyCode keyCode = keyEvent.getCode();
        String value = unitPrice.getText();


        if (keyCode.equals(KeyCode.ESCAPE)) {
            //Delete the bill and start over
            id.setDisable(false);
            unitPrice.setDisable(true);
            id.requestFocus();
            this.deleteBill();
        }

        if (value.matches("\\d+")) {
            if (keyCode.equals(KeyCode.ENTER)) {
                //Moving to the step 3: Reading product Quantity
                unitPrice.setDisable(true);
                quan.setDisable(false);
                quan.requestFocus();
            }
        } else {
            unitPrice.setText("");
        }
        keyEvent.consume();
    }

    //Step3 : Reading the product quantity
    public void readQuantity(KeyEvent keyEvent) throws SQLException {
        String idValue = id.getText();
        String unitValue = unitPrice.getText();

        KeyCode keyCode=keyEvent.getCode();
        if (keyCode.equals(KeyCode.ESCAPE)) {
            //Delete the bill and start over
            id.setDisable(false);
            quan.setDisable(true);
            id.requestFocus();

            this.deleteBill();
        }

        String quanValue = quan.getText();
     //   if(quanValue.matches("([+-]?\\d*\\.?\\d*)+")){
        if (quanValue.matches("[+-]?\\d{0,7}([\\.]\\d{0,2})?")) {
            if (!quanValue.isEmpty() && keyCode.equals(KeyCode.ENTER)) {
                int code = Integer.parseInt(idValue);
                int unitPrice =1;
                if(code!=0) unitPrice = Integer.parseInt(unitValue);
                double quantity = Double.parseDouble(quanValue);

                //Adding the entry to tableview and database
                //Moving to Step 1 to read the next product
                if (quantity != 0) this.addTableRaw(code, unitPrice, quantity);
                quan.setDisable(true);
                id.setDisable(false);
                id.requestFocus();
            }
        } else quan.setText("");
        keyEvent.consume();
    }

    //Adding the entry to tableview and database
    public void addTableRaw(int code, int uniPrice,double quantity) throws SQLException {

        if (isCode(productNames.keySet(), code)) {
            Product p = new Product(code, productNames.get(code), uniPrice, quantity);

            //Adding to the table view
            tableView.getItems().add(p);
            tableView.scrollTo(p);

            //Adding the bill
            if (code != 0) {
                bill.add(p);
            }

            //Adding to the database
            data.putSale(currentDate, p);

            //Update the bill information
            cashTotal.setText(this.printDouble(this.getTotal()));
            billTotal.setText(this.printDouble(this.getBillTotal()));
        } else {
            id.setText("");
            quan.setText("");
        }
    }

    //Calculating the case in hand vale
    public double getTotal() {
        ObservableList<Product> rows = tableView.getItems();
        int total = 0;
        for (Product p : rows) {
            total += Double.parseDouble(p.getTotal());
        }
        return total;
    }

    //Calculating the bill total
    public double getBillTotal() {
        double total = 0;
        for (Product p : bill) {
            total += Double.parseDouble(p.getTotal());
        }
        return total;
    }

    //Step 4: Reading the given cash amount
    public void readCash(KeyEvent keyEvent) throws SQLException{
        String value = cashGiven.getText();

        KeyCode keyCode=keyEvent.getCode();

        if (keyCode.equals(KeyCode.ESCAPE)) {
            //Delete the bill and start over
            id.setDisable(false);
            cashGiven.setDisable(true);
            id.requestFocus();
            this.deleteBill();
        }


        if (value.matches("\\d+")) {
            //Printing the bill and moving back to Step 1
            if (keyCode.equals(KeyCode.ENTER)) {
                int given = Integer.parseInt(cashGiven.getText());
                double total=this.getBillTotal();
                double bal = given - total;
                billTotal.setText(this.printDouble(total));
                balance.setText(this.printDouble(bal));


                //Printing the bill
                List<Double> footerData=new ArrayList<>();
                footerData.add(total);
                footerData.add((double) given);
                footerData.add(bal);

                //  printBill.printView(bill);
                printControl print=new printControl();
                print.printBill(bill,footerData);

                if (bal < 0) {
                    //Update the sale table
                    Product p = new Product(0,"Cash",1, bal);
                    tableView.getItems().add(p);
                    data.putSale(currentDate, p);
                    cashTotal.setText(this.printDouble(this.getTotal()));
                }

                //Clearing the bill and start over
                if (bill != null) bill.clear();
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

    //Editing the product table
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
        stat.setText(productNames.toString());
        actionEvent.consume();
    }

    //Showing and printing the daily sale report
    public void stat(ActionEvent actionEvent) throws IOException, SQLException {
        if(currentDate==null) return;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edit.fxml"));
        Parent root = loader.load();
        Edit edit = loader.getController();
        edit.setStatTable();
        Stage newWindow = new Stage();
        newWindow.setTitle("New Wellamadda Oil Mills");
        newWindow.setScene(new Scene(root));
        edit.setStage(newWindow);
        newWindow.show();
        actionEvent.consume();
    }

    //Deleting the selected raw from the database
    public void deleteRaw(KeyEvent keyEvent) throws SQLException {
        KeyCode keyCode = keyEvent.getCode();
        if (keyCode.equals(KeyCode.DELETE)) {
          //  Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete  this raw? ", ButtonType.NO,  ButtonType.YES,ButtonType.CANCEL);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete  this raw? ");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
            //Deactivate default behavior for yes-Button:
            Button yesButton = (Button) alert.getDialogPane().lookupButton( ButtonType.YES );
            yesButton.setDefaultButton( false );
            //Activate default behavior for no-Button:
            Button noButton = (Button) alert.getDialogPane().lookupButton( ButtonType.NO );
            noButton.setDefaultButton( true );
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {

                ObservableList<Product> rows = tableView.getItems();
                Product p = tableView.getSelectionModel().getSelectedItem();
                rows.remove(p);
                data.deleteSale(currentDate, p);
                cashTotal.setText(this.printDouble(this.getTotal()));
            }
        }
    }

    //Deleting the current bill without closing
    public void deleteBill() throws SQLException {
        if (bill== null) return;
        ObservableList<Product> rows = tableView.getItems();
        for (Product p : bill) {
            rows.remove(p);
            data.deleteSale(currentDate, p);
        }
        bill.clear();

        quan.setText("");
        unitPrice.setText("");
        balance.setText("");
        billTotal.setText("");
        cashTotal.setText(this.printDouble(this.getTotal()));
    }

    ///Closing the application
    public void closeApp(ActionEvent actionEvent) throws SQLException {
        data.shutdown();
        System.exit(0);
        actionEvent.consume();
    }

    //Backup the sales table to godaddy
    public void backupData(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String BACKUP_DATABASE_URL = "jdbc:mysql://sg2plcpnl0239.prod.sin2.secureserver.net/mill?";
        String DATABASE_USERNAME = "sathindu";
        String DATABASE_PASSWORD = "Kasun@home1";
        DataConnection backupServer = new DataConnection();
        backupServer.DataAccessor(DATABASE_DRIVER, BACKUP_DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
        if (currentDate != null) {
            List<Product> sales = data.getSalesList(currentDate);
            backupServer.deleteAllSales(currentDate);
            for(Product sale:sales) {
                backupServer.putSale(currentDate, sale);
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully Backup "+sales.size()+" records.");
            alert.showAndWait();
        }
        actionEvent.consume();
    }
}
