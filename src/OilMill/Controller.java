package OilMill;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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

    private static final String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost/mill?";
    private static final String DATABASE_USERNAME = "user";
    private static final String DATABASE_PASSWORD = "sathindu";
    private static final DataConnection data = new DataConnection();

    public static HashMap<Integer, String> productNames = null;
    private HashMap<Integer, Integer> price = null;
    private List<Product> sales = null;
    private final List<Product> bill = new ArrayList<>();

    private LocalDate currentDate = null;


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
    }

    public void prepareTable() {
        //  TableView tableView = new TableView();

        code.setCellValueFactory(new PropertyValueFactory<>("code"));
        // code.setCellFactory(TextFieldTableCell.<Product>forTableColumn());

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        unit.setStyle("-fx-alignment: CENTER-RIGHT;");

        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantity.setStyle("-fx-alignment: CENTER-RIGHT;");

        total.setCellValueFactory(new PropertyValueFactory<>("total"));
        total.setStyle("-fx-alignment: CENTER-RIGHT;");
    }

    public void printStat(LocalDate date) throws SQLException {
        double totalB=data.getTotalQuantity(date,1);
        double totalL=data.getTotalQuantity(date,2);
        double totalK=data.getTotalQuantity(date,3);
        double total=(totalB*.75+totalL)*.9+totalK;

        double totalS=data.getTotalSale(date);

        String s=String.format("Sale(Kg,Income,Avg) : %.2f Kg , Rs. %.2f , Rs. %.2f",total,totalS,totalS/total);
        stat.setText(s);
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
                this.printStat(currentDate);
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
            id.setDisable(true);
            cashGiven.setDisable(false);
            cashGiven.requestFocus();
        }
        if (value.matches("\\d+")) {
            int code = Integer.parseInt(value);

            if(bill.isEmpty()) {
                cashGiven.setText("");
                balance.setText("");
                billTotal.setText("");
            }

            switch (code) {
                case 0:
                    productName.setText(productNames.get(code)+" in and out");
                    if (keyCode.equals(KeyCode.ENTER)) {
                        id.setDisable(true);
                        unitPrice.setText(Integer.toString(price.get(code)));
                        quan.setDisable(false);
                        quan.requestFocus();
                    }
                    break;
                case 1, 2, 3, 4:
                    productName.setText(productNames.get(code)+" Price");
                    if (keyCode.equals(KeyCode.ENTER)) {
                        id.setDisable(true);
                        unitPrice.setDisable(false);
                        unitPrice.requestFocus();
                        unitPrice.setText(Integer.toString(price.get(code)));
                        unitPrice.selectAll();
                    }
                    break;
                default:
                    productName.setText("");
            }
        } else id.setText("");
        keyEvent.consume();
    }

    public void setUnitPrice(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        String value = unitPrice.getText();

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

        String quanValue = quan.getText();
     //   if(quanValue.matches("([+-]?\\d*\\.?\\d*)+")){
        if (quanValue.matches("[+-]?\\d{0,7}([\\.]\\d{0,2})?")) {
            if (!quanValue.isEmpty() && keyEvent.getCode().equals(KeyCode.ENTER)) {
                int code = Integer.parseInt(idValue);
                int unitPrice=Integer.parseInt(unitValue);
                double quantity = Double.parseDouble(quanValue);
                this.addTableRaw(code, unitPrice,quantity);
                quan.setDisable(true);
                id.setDisable(false);
                id.requestFocus();
            }
        } else quan.setText("");
        keyEvent.consume();
    }

    public void addTableRaw(int code, int uniPrice,double quantity) throws SQLException {
        switch (code) {
            case 0, 1, 2, 3, 4:
                Product p = new Product(code, productNames.get(code),uniPrice,quantity);
                tableView.getItems().add(p);
                tableView.scrollTo(p);
                if(code!=0) bill.add(p);
                data.putSale(currentDate, p);
                cashTotal.setText(this.printDouble(this.getTotal()));
                billTotal.setText(this.printDouble(this.getBillTotal()));
                this.printStat(currentDate);
                break;
            default:
        }
        id.setText("");
        quan.setText("");
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

        if (value.matches("\\d+")) {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                int given = Integer.parseInt(cashGiven.getText());
                double total=this.getBillTotal();
                double bal = given - total;
                billTotal.setText(this.printDouble(total));
                balance.setText(this.printDouble(bal));

                printControl printBill=new printControl(total,given,bal);
                printBill.printView(bill);

                if (bal < 0) {
                    Product p = new Product(0, productNames.get(0), price.get(0), bal);
                    tableView.getItems().add(p);
                    data.putSale(currentDate, p);
                    cashTotal.setText(this.printDouble(this.getTotal()));
                }

                if (bill != null) bill.clear();
                balance.setText("");
                billTotal.setText("");
                cashGiven.setText("");
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

}
