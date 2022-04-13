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
    private List<Product> printStat = new ArrayList<>();
    private List<Double> footer=new ArrayList<>();


    DataConnection connection = new DataConnection();

    //Connecting to the database
    public void initialize() throws SQLException, ClassNotFoundException {
        connection.DataAccessor(Controller.DATABASE_DRIVER, Controller.DATABASE_URL, Controller.DATABASE_USERNAME, Controller.DATABASE_PASSWORD);
    }

    //Prepare and show the sales summary view
    public void setStatTable() throws SQLException {
        title.setText("Sales Summary on "+Controller.currentDate.toString());
        add.setVisible(false);
        delete.setVisible(false);
        //Change Save button to Print
        save.setVisible(true);
        save.setText("Print");

        code.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        code.setStyle("-fx-alignment: CENTER-LEFT;");
        code.setText("QTY");

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        price.setCellValueFactory(new PropertyValueFactory<>("total"));
        price.setStyle("-fx-alignment: CENTER-RIGHT;");
        price.setText("Total");

       // double kilo=0;
       // double punak=0;
       // double quan=0;
        //double total=0;

        //Get Number 1 Summary
        double quanB1=connection.getProductQuantity(Controller.currentDate,1);
        double totalB1=connection.getProductTotal(Controller.currentDate,1);

        double quanL1=connection.getProductQuantity(Controller.currentDate,2);
        double totalL1=connection.getProductTotal(Controller.currentDate,2);

        double quanK1=connection.getProductQuantity(Controller.currentDate,3);
        double totalK1=connection.getProductTotal(Controller.currentDate,3);

        double killoK1=quanB1*.66+quanL1*.9+quanK1;
        double priceK1=totalB1+totalL1+totalK1;

        this.addStatTableRow(0,"** Oil Grade 1 **",killoK1);
        this.addStatTableRow(quanB1,"Bottles No 1",totalB1);
        this.addStatTableRow(quanL1,"Litters No 1",totalL1);
        this.addStatTableRow(quanK1,"Kilo No 1",totalK1);
        this.addStatTableRow(killoK1,"Summary (Kilo)",priceK1);

        //Get Number 2 Summary
        double quanB2=connection.getProductQuantity(Controller.currentDate,11);
        double totalB2=connection.getProductTotal(Controller.currentDate,11);

        double quanL2=connection.getProductQuantity(Controller.currentDate,22);
        double totalL2=connection.getProductTotal(Controller.currentDate,22);

        double quanK2=connection.getProductQuantity(Controller.currentDate,33);
        double totalK2=connection.getProductTotal(Controller.currentDate,33);

        double killoK2=quanB2*.66+quanL2*.9+quanK2;
        double priceK2=totalB2+totalL2+totalK2;

        this.addStatTableRow(0,"** Oil Grade 2 **",killoK2);
        this.addStatTableRow(quanB2,"Bottles No 2",totalB2);
        this.addStatTableRow(quanL2,"Litters No 2",totalL2);
        this.addStatTableRow(quanK2,"Kilo No 2",totalK2);
        this.addStatTableRow(killoK2,"Summary (Kilo)",priceK2);

        //Punak summary
        double quanP=connection.getProductQuantity(Controller.currentDate,4);
        double totalP=connection.getProductTotal(Controller.currentDate,4);

        this.addStatTableRow(0,"** Punak **",quanP);
        this.addStatTableRow(quanP,"Summary",totalP);

        //Calculate the total summary
        double totalSale=connection.getTotalSale(Controller.currentDate);
        double cashInHand=connection.getCashInHand(Controller.currentDate);
        double cashOut=connection.getCashOut(Controller.currentDate);
        double cashIn=connection.getCashIn(Controller.currentDate);

        this.addStatTableRow(0,"**** Summary ****",0);
        this.addStatTableRow(0,"Total Sale",totalSale);
        this.addStatTableRow(0,"Cash In",cashIn);
        this.addStatTableRow(0,"Cash out",cashOut);
        this.addStatTableRow(0,"Cash in Hand",cashInHand);

        double kilo=killoK1+killoK2;
        this.addStatTableRow(kilo,"Total Oil Sale",priceK1+priceK2);
        if(kilo>0) this.addStatTableRow(0,"Per Kilo Price",(priceK1+priceK2)/kilo);


/*
        printStat.add(new Product(1,"Punak",(int)(punak/quan),quan,punak));
        printStat.add(new Product(1,"Bottle",(int)(total/quan),quan,total));
        //Add the total summary as footer of the report
        footer.add(kilo); footer.add((totalSale-punak)/kilo);
        footer.add(totalSale-punak); footer.add(totalSale);
        footer.add(cashIn); footer.add(cashOut);
        footer.add(cashInHand);

 */
    }


    //Show the editable table to change products
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

    //Saving the edit record or printing the bill
    public void save(MouseEvent mouseEvent) throws SQLException {

        //Printing the bill
        if(save.getText().contains("Print")){
            //Printing the bill
            printControl printBill=new printControl();
            printBill.printStat(printStat,footer);
        }

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
        mouseEvent.consume();
    }

    private boolean isCode(Set<Integer> codes, int num){
        for (int code:codes)
            if(code == num) return true ;
        return false;
    }

    public void add(MouseEvent mouseEvent) {
        addEditTableRow(0,"Type code,name and price in this raw)",0);
        mouseEvent.consume();
    }

    //Delete the product row
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
        mouseEvent.consume();
    }

    public void cancel(MouseEvent mouseEvent) {
        this.stage.close();
        mouseEvent.consume();
    }
}
