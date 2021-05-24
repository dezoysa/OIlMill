package OilMill;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


public class Controller {
    @FXML public TableView<Product> tableView;
    @FXML private TableColumn<Product, String> code;
    @FXML private TableColumn<Product, String> name;
    @FXML private TableColumn<Product, String> unit;
    @FXML private TableColumn<Product, String> quantity;
    @FXML private TableColumn<Product, String> total;

    @FXML public TextField id;
    @FXML public TextField quan;
    @FXML public TextField cashGiven;
    @FXML public TextField balance;

    private static final String DATABASE_DRIVER="com.mysql.cj.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost/mill?";
    private static final String DATABASE_USERNAME = "user";
    private static final String DATABASE_PASSWORD = "sathindu";
    private static final DataConnection data=new DataConnection();

    private HashMap<Integer,String> productNames=null;
    private HashMap<Integer,Integer> price=null;
    private List<Product> sales=null;
    private List<Product> bill=new ArrayList<>();


    public void initialize() throws SQLException, ClassNotFoundException {
        data.DataAccessor(DATABASE_DRIVER,DATABASE_URL,DATABASE_USERNAME,DATABASE_PASSWORD);
        price=data.getPriceList();
        productNames=data.getProductNames();
        sales=data.getSalesList();
    }

    public void prapareTable() throws SQLException, ClassNotFoundException {
      //  TableView tableView = new TableView();

        code.setCellValueFactory(new PropertyValueFactory<>("code"));
       // code.setCellFactory(TextFieldTableCell.<Product>forTableColumn());

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        unit.setCellValueFactory(new PropertyValueFactory<>("unit"));

        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
       // quantity.setCellFactory(TextFieldTableCell.<Product>forTableColumn());

        total.setCellValueFactory(new PropertyValueFactory<>("total"));

//        private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/javafx_registration?useSSL=false";
        // private static final String INSERT_QUERY = "INSERT INTO registration (full_name, email_id, password) VALUES (?, ?, ?)";

        tableView.getItems().addAll(sales);
    }

    public void readCode(KeyEvent keyEvent) {
        KeyCode keyCode=keyEvent.getCode();
        if (keyCode.equals(KeyCode.SPACE)) cashGiven.requestFocus();
        if (keyCode.equals(KeyCode.ENTER)) quan.requestFocus();
    }

    public void readQuantity(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            this.addTableRaw();
            id.requestFocus();
        }
    }

    public void addTableRaw() throws SQLException {
        if(id.getText().equals("") || quan.getText().equals("")) return;

        int code = Integer.parseInt(id.getText());
        int quantity = Integer.parseInt(quan.getText());
        switch (code) {
            case 0,1, 2, 3, 4:
                Product p = new Product(code, productNames.get(code), price.get(code), quantity, quantity * price.get(code));
                tableView.getItems().add(p);
                bill.add(p);
                if(data.putSale(p)>0) System.out.println("A new user was inserted successfully!");

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
           total+=Integer.parseInt(p.getTotal());
       }
       return total;
    }

    public void readCash(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("print.fxml"));
            Parent print = loader.load();
            Controller printController = loader.getController();

            int total=0;
            for(Product p:bill){
                total+=Integer.parseInt(p.getTotal());
            }
            System.out.println(total);

            bill.clear();
            balance.setText("");
            id.setText("");
            quan.setText("");

            balance.setText(Integer.toString(this.getTotal()));
            id.requestFocus();
        }

    }
}
