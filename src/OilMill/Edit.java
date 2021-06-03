package OilMill;

import com.sun.media.jfxmediaimpl.platform.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

public class Edit {

    @FXML public TableView<Product> edit_table;
    @FXML private TableColumn<Product, String> code;
    @FXML private TableColumn<Product, String> name;
    @FXML private TableColumn<Product, String> price;


    DataConnection connection = new DataConnection();
    Controller controller = new Controller();

    public void initialize() throws SQLException, ClassNotFoundException {
        connection.DataAccessor(controller.DATABASE_DRIVER, controller.DATABASE_URL, controller.DATABASE_USERNAME, controller.DATABASE_PASSWORD);
       set_table();
    }
    public void table(){
        code.setCellValueFactory(new PropertyValueFactory<>("code"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        setEdit_table();
    }

    public void setEdit_table(){
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setOnEditCommit(e->{
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setName(e.getNewValue());
        });

        price.setCellFactory(TextFieldTableCell.forTableColumn());
        price.setOnEditCommit(e->{
            e.getTableView().getItems().get(e.getTablePosition().getRow()).setPrice(Integer.parseInt(e.getNewValue()));
        });
        edit_table.setEditable(true);

    }


    public void addTableRow(int code,String name,int price){
        Product p = new Product(code,name,price);
        edit_table.getItems().add(p);
        edit_table.scrollTo(p);
    }

    public void set_table(){
        for (int code : controller.productNames.keySet()){
                this.addTableRow(code,controller.productNames.get(code),controller.price.get(code));
        }

    }


    public void save(MouseEvent mouseEvent) throws SQLException {
        //connection.delete();
        ObservableList<Product> rows = edit_table.getItems();
        for (Product p : rows) {
            int code = Integer.parseInt(p.getCode());
            String name = p.getName();
            int price = Integer.parseInt(p.getPrice());

            if (isCode(controller.productNames.keySet(),code)){
                controller.productNames.put(code, name);
                controller.price.put(code, price);
                connection.update(code, name, price);
            }else {
                controller.productNames.put(code, name);
                controller.price.put(code, price);
                connection.putProduct(code, name, price);
            }
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("saved");
        alert.showAndWait();

    }

    private boolean isCode(Set<Integer> codes, int num){
        for (int code:codes)
            if(code == num) return true ;

        return false;
    }

    public void add(MouseEvent mouseEvent) {
        addTableRow(edit_table.getItems().size()+1,"null",00);
    }
    }
