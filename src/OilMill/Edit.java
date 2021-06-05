package OilMill;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Set;

public class Edit {

    @FXML public TableView<Product> edit_table;
    @FXML private TableColumn<Product, String> code;
    @FXML private TableColumn<Product, String> name;
    @FXML private TableColumn<Product, String> price;
    @FXML private Button add;
    @FXML private Button save;
    @FXML private Button delete;
    @FXML private Label title;

    private static Stage stage;

    DataConnection connection = new DataConnection();

    public void initialize() throws SQLException, ClassNotFoundException {
        connection.DataAccessor(Controller.DATABASE_DRIVER, Controller.DATABASE_URL, Controller.DATABASE_USERNAME, Controller.DATABASE_PASSWORD);
    }

    public void setStatTable() throws SQLException {
        title.setText("Sales Summary on "+Controller.currentDate.toString());
        add.setVisible(false);
        delete.setVisible(false);
        save.setVisible(false);

        code.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        code.setStyle("-fx-alignment: CENTER-LEFT;");
        code.setText("QTY");

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        price.setCellValueFactory(new PropertyValueFactory<>("total"));
        price.setStyle("-fx-alignment: CENTER-RIGHT;");
        price.setText("Total");

        double killo=0;
        double punak=0;
        for (int code : Controller.productNames.keySet()){
            if(code==0) continue;
            double quan=connection.getProductQuantity(Controller.currentDate,code);
            if(quan==0) continue;
            double total= connection.getProductTotal(Controller.currentDate,code);
            String name=Controller.productNames.get(code);

            if(name.toLowerCase().contains("litter")) killo+=quan * 0.9;
            else if (name.toLowerCase().contains("bottle")) killo+=quan * 0.675;
            else if (name.toLowerCase().contains("killo")) killo+=quan;
            else  if (name.toLowerCase().contains("punak")) punak+=total;

            this.addStatTableRow(quan,name,total);
        }
        double totalSale=connection.getTotalSale(Controller.currentDate);
        this.addStatTableRow(0,"Total Sale",totalSale);
        this.addStatTableRow(0,"Oil Sale",totalSale-punak);
        this.addStatTableRow(0,"Oil Sale in Kilo",killo);
        this.addStatTableRow(0,"Per Kilo Price",(totalSale-punak)/killo);
    }


    public void setEditTable(){

        code.setCellValueFactory(new PropertyValueFactory<>("code"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));

        code.setCellFactory(TextFieldTableCell.forTableColumn());
        code.setOnEditCommit(e->{
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setCode(Integer.parseInt(e.getNewValue()));
        });

        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setOnEditCommit(e->{
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setName(e.getNewValue());
        });

        price.setCellFactory(TextFieldTableCell.forTableColumn());
        price.setOnEditCommit(e->{
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setPrice(Integer.parseInt(e.getNewValue()));
        });
        edit_table.setEditable(true);

        for (int code : Controller.productNames.keySet()){
            this.addEditTableRow(code,Controller.productNames.get(code),Controller.price.get(code));
        }

    }

    public void addEditTableRow(int code,String name,int price){
        Product p = new Product(code,name,price);
        edit_table.getItems().add(p);
        edit_table.scrollTo(p);
    }

    public void addStatTableRow(double quantity,String name,double total){
        Product p = new Product(0,name,0,quantity,total);
        edit_table.getItems().add(p);
        edit_table.scrollTo(p);
    }


    public void setStage(Stage stage){
        this.stage = stage;
    }


    public void save(MouseEvent mouseEvent) throws SQLException {
        ObservableList<Product> rows = edit_table.getItems();
        Set <Integer> keys=Controller.productNames.keySet();

        for (Product p : rows) {
            int code = Integer.parseInt(p.getCode());
            if(code==0) continue;
            String name = p.getName();
            int price = Integer.parseInt(p.getPrice());

            if (isCode(keys,code)){
                Controller.productNames.replace(code,name);
             //   Controller.productNames.put(code, name);
                Controller.price.replace(code, price);
                connection.update(code, name, price);
            }else {
                Controller.productNames.put(code, name);
                Controller.price.put(code, price);
                connection.putProduct(code, name, price);
            }
        }

        this.stage.close();

    }

    private boolean isCode(Set<Integer> codes, int num){
        for (int code:codes)
            if(code == num) return true ;
        return false;
    }

    public void add(MouseEvent mouseEvent) {
        addEditTableRow(0,"Type code,name and price in this raw)",0);
    }

    public void delete(MouseEvent mouseEvent) throws SQLException {
        Product p=edit_table.getSelectionModel().getSelectedItem();
        Integer code=Integer.parseInt(p.getCode());
        if(code==0) return;
        edit_table.getItems().remove(p);
        if(isCode(Controller.productNames.keySet(),code)){
            Controller.productNames.remove(code);
            Controller.price.remove(code);
            connection.delete(code);
        }
    }

    public void cancel(MouseEvent mouseEvent) {
        this.stage.close();
    }
}
