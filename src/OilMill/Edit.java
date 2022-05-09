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

        double kiloNo1=0;
        double kiloNo2=0;
        double punak=0;
        double quan=0;
        double total=0;

        //No-1
        //Get Bottles summary
        quan=connection.getProductQuantity(Controller.currentDate,1);
        total=connection.getProductTotal(Controller.currentDate,1);
        this.addStatTableRow(quan,"Bottle No1",total);
        printStat.add(new Product(1,"BottleN1",(int)(total/quan),quan,total));
        kiloNo1+=quan*.69;
        //Get Litters summary
        quan=connection.getProductQuantity(Controller.currentDate,2);
        total=connection.getProductTotal(Controller.currentDate,2);
        this.addStatTableRow(quan,"Litter No1",total);
        printStat.add(new Product(1,"LitterN1",(int)(total/quan),quan,total));
        kiloNo1+=quan*.9;
        //Get Kilos summary
        quan=connection.getProductQuantity(Controller.currentDate,3);
        total=connection.getProductTotal(Controller.currentDate,3);
        this.addStatTableRow(quan,"Kilo No1",total);
        printStat.add(new Product(1,"KilloN1",(int)(total/quan),quan,total));
        kiloNo1+=quan;
        this.addStatTableRow(0,"Oil Sale in Kilo No1",kiloNo1);

        //No 2
        //Get Bottles summary
        quan= connection.getProductQuantity(Controller.currentDate,11);
        total=connection.getProductTotal(Controller.currentDate,11);
        this.addStatTableRow(quan,"Bottle No2",total);
        printStat.add(new Product(1,"BottleN2",(int)(total/quan),quan,total));
        kiloNo2+=quan*.69;
        //Get Litters summary
        quan= connection.getProductQuantity(Controller.currentDate,22);
        total=connection.getProductTotal(Controller.currentDate,22);
        this.addStatTableRow(quan,"Litter No2",total);
        printStat.add(new Product(1,"LitterN2",(int)(total/quan),quan,total));
        kiloNo2+=quan*.9;
        //Get Kilos summary
        quan= connection.getProductQuantity(Controller.currentDate,33);
        total=connection.getProductTotal(Controller.currentDate,33);
        this.addStatTableRow(quan,"Kilo No2",total);
        printStat.add(new Product(1,"KilloN2",(int)(total/quan),quan,total));
        kiloNo2+=quan;
        this.addStatTableRow(0,"Oil Sale in Kilo No2",kiloNo2);

        //Punak summary
        quan=connection.getProductQuantity(Controller.currentDate,4);
        punak=connection.getProductTotal(Controller.currentDate,4);
        this.addStatTableRow(quan,"Punak",punak);
        printStat.add(new Product(1,"Punak",(int)(punak/quan),quan,punak));

        //Calculate the total summary
        double totalSale=connection.getTotalSale(Controller.currentDate);
        double cashInHand=connection.getCashInHand(Controller.currentDate);
        double cashOut=connection.getCashOut(Controller.currentDate);
        double cashIn=connection.getCashIn(Controller.currentDate);

        double kilos=kiloNo1+kiloNo2;
        this.addStatTableRow(0,"Total Oil Sale in Kilos",kilos);
        if((kilos)>0) this.addStatTableRow(0,"Per Kilo Price",(totalSale-punak)/kilos);

        this.addStatTableRow(0,"Oil Sale",totalSale-punak);
        this.addStatTableRow(0,"Total Sale",totalSale);
        this.addStatTableRow(0,"Cash In",cashIn);
        this.addStatTableRow(0,"Cash out",cashOut);
        this.addStatTableRow(0,"Cash in Hand",cashInHand);

        //Add the total summary as footer of the report
        footer.add(kilos); footer.add((totalSale-punak)/kilos);
        footer.add(totalSale-punak); footer.add(totalSale);
        footer.add(cashIn); footer.add(cashOut);
        footer.add(cashInHand);
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
