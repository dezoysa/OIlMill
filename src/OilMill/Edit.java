package OilMill;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

        double kilo=0;
        double punak=0;
        double quan=0;
        double total=0;

        List<Product> p = new ArrayList<>();
        //Bottles
        quan=connection.getProductQuantity(Controller.currentDate,1);
        quan+= connection.getProductQuantity(Controller.currentDate,11);
        total=connection.getProductTotal(Controller.currentDate,1);
        total+=connection.getProductTotal(Controller.currentDate,11);
        kilo+=quan*.67;
        this.addStatTableRow(quan,"Bottles",total);
        p.add(new Product(1,"Bottle",(int)(total/quan),quan,total));

        //Litters
        quan=connection.getProductQuantity(Controller.currentDate,2);
        quan+= connection.getProductQuantity(Controller.currentDate,22);
        total=connection.getProductTotal(Controller.currentDate,2);
        total+=connection.getProductTotal(Controller.currentDate,22);
        kilo+=quan*.9;
        this.addStatTableRow(quan,"Litters",total);
        p.add(new Product(1,"Litter",(int)(total/quan),quan,total));

        //Kilos
        quan=connection.getProductQuantity(Controller.currentDate,3);
        quan+= connection.getProductQuantity(Controller.currentDate,33);
        total=connection.getProductTotal(Controller.currentDate,3);
        total+=connection.getProductTotal(Controller.currentDate,33);
        kilo+=quan;
        this.addStatTableRow(quan,"Kilos",total);
        p.add(new Product(1,"Killo",(int)(total/quan),quan,total));


        //Punak
        quan=connection.getProductQuantity(Controller.currentDate,4);
        punak=connection.getProductTotal(Controller.currentDate,4);
        this.addStatTableRow(quan,"Punak",punak);
        p.add(new Product(1,"Punak",(int)(punak/quan),quan,punak));


        double totalSale=connection.getTotalSale(Controller.currentDate);
        double cashInHand=connection.getCashInHand(Controller.currentDate);
        double cashOut=connection.getCashOut(Controller.currentDate);
        double cashIn=connection.getCashIn(Controller.currentDate);

        this.addStatTableRow(0,"Oil Sale in Kilo",kilo);
        this.addStatTableRow(0,"Per Kilo Price",(totalSale-punak)/kilo);
        this.addStatTableRow(0,"Oil Sale",totalSale-punak);
        this.addStatTableRow(0,"Total Sale",totalSale);
        this.addStatTableRow(0,"Cash In",cashIn);
        this.addStatTableRow(0,"Cash out",cashOut);
        this.addStatTableRow(0,"Cash in Hand",cashInHand);

        //Printing the bill
        printControl printBill=new printControl(totalSale+cashIn,cashOut,cashInHand);
        printBill.print(p);

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
