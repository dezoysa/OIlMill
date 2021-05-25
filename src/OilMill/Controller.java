package OilMill;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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

    private static final String DATABASE_DRIVER="com.mysql.cj.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost/mill?";
    private static final String DATABASE_USERNAME = "user";
    private static final String DATABASE_PASSWORD = "sathindu";
    private static final DataConnection data=new DataConnection();

    public static HashMap<Integer,String> productNames=null;
    private HashMap<Integer,Integer> price=null;
    private List<Product> sales=null;
    private List<Product> bill=new ArrayList<>();

    private LocalDate currentDate=null;


    public void initialize() throws SQLException, ClassNotFoundException {
        data.DataAccessor(DATABASE_DRIVER,DATABASE_URL,DATABASE_USERNAME,DATABASE_PASSWORD);
        price=data.getPriceList();
        productNames=data.getProductNames();
        // Set the Locale to US
        Locale.setDefault(Locale.ENGLISH);
        id.setDisable(true);
        quan.setDisable(true);
        cashGiven.setDisable(true);
    }

    public void prapareTable() throws SQLException, ClassNotFoundException {
      //  TableView tableView = new TableView();

        code.setCellValueFactory(new PropertyValueFactory<>("code"));
       // code.setCellFactory(TextFieldTableCell.<Product>forTableColumn());

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        unit.setStyle( "-fx-alignment: CENTER-RIGHT;");

        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantity.setStyle( "-fx-alignment: CENTER-RIGHT;");
       // quantity.setCellFactory(TextFieldTableCell.<Product>forTableColumn());

        total.setCellValueFactory(new PropertyValueFactory<>("total"));
        total.setStyle( "-fx-alignment: CENTER-RIGHT;");
    }

    public void readCode(KeyEvent keyEvent) {
        KeyCode keyCode=keyEvent.getCode();
        String value=id.getText();
//Check Number format
        //Check date null
        if (keyCode.equals(KeyCode.SPACE)) {
            cashGiven.setDisable(false);
            cashGiven.requestFocus();
        }
        if(value.matches("\\d+")){
            int code=Integer.parseInt(value);
            switch (code) {
                case 0, 1, 2, 3, 4:
                    productName.setText(productNames.get(code));
                    System.out.println(productNames);

                    if (keyCode.equals(KeyCode.ENTER)) {
                        quan.setDisable(false);
                        quan.requestFocus();
                    }
                    break;
                default:
                    productName.setText("");
            }
        } else id.setText("");
    }

    public void readQuantity(KeyEvent keyEvent) throws SQLException {
        String idValue=id.getText();
        String quanValue=quan.getText();
       // [+-]?([0-9]*[.])?[0-9]+
        if (idValue.matches("\\d+") &&
                quanValue.matches("[+-]?\\d{0,7}([\\.]\\d{0,2})?")) {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                int code = Integer.parseInt(idValue);
                double quantity = Double.parseDouble(quanValue);
                this.addTableRaw(code,quantity);
                id.requestFocus();
            }
        }else quan.setText("");
    }

    public void addTableRaw(int code,double quantity) throws SQLException {
    //    if(id.getText().equals("") || quan.getText().equals("")) return;
        switch (code) {
            case 0,1, 2, 3, 4:
                Product p = new Product(code, productNames.get(code), price.get(code), quantity, quantity * price.get(code));
                tableView.getItems().add(p);
                bill.add(p);
                if(data.putSale(currentDate,p)>0) System.out.println("A new user was inserted successfully!");
                cashTotal.setText(Integer.toString(this.getTotal()));
                break;
            default:
        }
        id.setText("");
        quan.setText("");
    }

    public int getTotal(){
       ObservableList<Product> rows=tableView.getItems();
       int total=0;
       for(Product p:rows){
           total+=Double.parseDouble(p.getTotal());
       }
       return total;
    }

    public void readCash(KeyEvent keyEvent) throws IOException, SQLException {
        String value=cashGiven.getText();

        if(value.matches("\\d+")) {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                int given = Integer.parseInt(cashGiven.getText());

                double total = 0;
                for (Product p : bill) {
                    total += Double.parseDouble(p.getTotal());
                }
                double bal = given - total;

                billTotal.setText(Double.toString(total));
                balance.setText(Double.toString(bal));

                if (bal < 0) {
                    Product p = new Product(0, productNames.get(0), price.get(0), bal, bal * price.get(0));
                    tableView.getItems().add(p);
                    if (data.putSale(currentDate, p) > 0) System.out.println("A new user was inserted successfully!");
                    cashTotal.setText(Integer.toString(this.getTotal()));
                }

                if (bill != null) bill.clear();
                // balance.setText("");
                id.setText("");
                quan.setText("");
                id.requestFocus();
            }
        } else cashGiven.setText("");
    }

    public void readDate(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            currentDate= date.getValue();
            System.out.println(currentDate.toString());

            if(sales!=null) {
                sales.clear();
                tableView.getItems().clear();
            }
            if(currentDate!=null) {
                sales = data.getSalesList(currentDate);
                tableView.getItems().addAll(sales);
                cashTotal.setText(Integer.toString(this.getTotal()));
                id.setDisable(false);
                id.requestFocus();
            }
                keyEvent.consume();
        }
    }
}
