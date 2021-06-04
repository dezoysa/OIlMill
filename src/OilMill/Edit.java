package OilMill;

import com.sun.media.jfxmediaimpl.platform.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

public class Edit {

    @FXML public TableView<Product> edit_table;
    @FXML private TableColumn<Product, String> code;
    @FXML private TableColumn<Product, String> name;
    @FXML private TableColumn<Product, String> price;
    @FXML private Button add;
    @FXML private Button save;
    @FXML private Button delete;


    private static Stage stage;

    DataConnection connection = new DataConnection();
   // Controller controller = new Controller();

    public void initialize() throws SQLException, ClassNotFoundException {
        connection.DataAccessor(Controller.DATABASE_DRIVER, Controller.DATABASE_URL, Controller.DATABASE_USERNAME, Controller.DATABASE_PASSWORD);
    }
    public void table(){
        code.setCellValueFactory(new PropertyValueFactory<>("code"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        setEdit_table();
    }

    public void tableStat() throws SQLException {
        add.setVisible(false);
        delete.setVisible(false);
        save.setVisible(false);

        code.setCellValueFactory(new PropertyValueFactory<>("code"));
        code.setStyle("-fx-alignment: CENTER-LEFT;");
        code.setText("QTY");

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        price.setStyle("-fx-alignment: CENTER-RIGHT;");
        price.setText("Total");

        int total=0;
        double killo=0;
        double punak=0;
        for (int code : Controller.productNames.keySet()){
            String name=Controller.productNames.get(code);
            int quan=(int)connection.getTotalQuantity(Controller.currentDate,code);
            int price=Controller.price.get(code);
            if(code!=0) total+=quan*price;

            if(name.toLowerCase().contains("litter")) killo+=quan*.75;
            else if (name.toLowerCase().contains("bottle")) killo+=quan*.675;
            else if (name.toLowerCase().contains("killo")) killo+=quan;
            else  if (name.toLowerCase().contains("punak")) punak+=quan*price;

            this.addTableRow(quan,name,quan*price);
        }
        this.addTableRow(0,"Total Sale",total);
        this.addTableRow(0,"Oil Sale",(int)(total-punak));
        this.addTableRow(0,"Oil Sale in Kilo",(int)killo);
        this.addTableRow(0,"Per Kilo Price",(int)((total-punak)/killo));
    }


    public void setEdit_table(){
        set_table();
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

    }


    public void addTableRow(int code,String name,int price){
        Product p = new Product(code,name,price);
        edit_table.getItems().add(p);
        edit_table.scrollTo(p);
    }

    public void set_table(){
        for (int code : Controller.productNames.keySet()){
                this.addTableRow(code,Controller.productNames.get(code),Controller.price.get(code));
        }

    }


    public void setStage(Stage stage){
        this.stage = stage;
    }


    public void save(MouseEvent mouseEvent) throws SQLException {
        ObservableList<Product> rows = edit_table.getItems();
        Set <Integer> keys=Controller.productNames.keySet();

        for (Product p : rows) {
            int code = Integer.parseInt(p.getCode());
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
        addTableRow(0,"Type code,name and price in this raw)",0);
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
