package OilMill;

import com.mysql.cj.util.StringUtils;
import com.sun.javafx.print.PrintHelper;
import com.sun.javafx.print.Units;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.print.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

public class printControl {
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private final Date  date = new Date();
    private final String printDate = dateFormat.format(date);
    private final String printTime = timeFormat.format(date);
    //private final DecimalFormat decim1 = new DecimalFormat("0.0");
    private final DecimalFormat decim2 = new DecimalFormat("0.00");


    private  String printerName;

    printControl(){
        printerName = "TM-T82-S-A";
    }

    printControl(String name){
        printerName = name;
    }

    private PrintService findPrintService(String printerName,PrintService[] services) {
        for (PrintService service : services) {
           // System.out.println(service.getName());
            if (service.getName().equalsIgnoreCase(printerName)) {
                System.out.println("Found "+printerName);
                return service;
            } else{
                System.out.println(printerName+" was not found");
                return null;
            }
        }
        return null;
    }

    private Printer findPrinter(String printerName) {
        ObservableSet<Printer> printers = Printer.getAllPrinters();
        for (Printer printer : printers) {
            if (printer.getName().equalsIgnoreCase(printerName)) {
                System.out.println("Found "+printerName);
                return printer;
            } else {
                System.out.println(printerName+" was not found");
                return null;
            }
        }
        return null;
    }

    private String setHeader() {
         String header = "          Sekkuwa\n" ;
                header+= "        091-2255898\n";
                header+=String.format("%-16s","Date:"+printDate);
                header+=String.format("%12s","Time:"+printTime);
                header+= "\n----------------------------\n";
                header+=String.format("%-6s","Name");
                header+=String.format("%6s","Qty");
                header+=String.format("%6s","Rate");
                header+=String.format("%10s","Amount");
                header+="\n----------------------------\n";

         return header;
    }

    private String setTagLine() {
        String tag = "****************************\n"+
                     "Coconut -  Miracle of Nature\n\n";
        return tag;
    }

    /*
    public void printView(List<Product> bill){
        VBox node=this.getNode(bill);

        Button b1= new Button("OK");
        BorderPane pane=new BorderPane();
        pane.setCenter(b1);

        node.getChildren().add(pane);
        Group root = new Group(node);
        Scene scene = new Scene(root, 200,350);
        Stage billStage = new Stage();
        billStage.setTitle("View Bill");
        billStage.setScene(scene);
      //  this.printBill(node);

        b1.setOnMouseClicked(event-> {
          //  b1.setVisible(false);
       //     this.printBill(node);
         //   billStage.close();
        });
        b1.setOnKeyPressed(event-> {
            KeyCode key=event.getCode();
            if(key.equals(KeyCode.ENTER)) {
                //b1.setVisible(false);
              //  this.printBill(node);
                this.print(bill);
            }
           // billStage.close();
        });

        billStage.showAndWait();
    }


    public void printBill(Node node){
        Printer myPrinter = this.findPrinter(printerName);
        if(myPrinter==null) return;

        PrinterJob job = PrinterJob.createPrinterJob(myPrinter);
        Paper paper = PrintHelper.createPaper("Roll80", 80, 300, Units.MM);
        PageLayout pageLayout = job.getPrinter().createPageLayout(paper, PageOrientation.PORTRAIT, 10, 10, 10, 10);

        double scale = 0.791;
        node.getTransforms().add(new Scale(scale, scale));
        if (job != null) {
            boolean success = job.printPage(pageLayout, node);
            if (success) job.endJob();
        }
    }

    public VBox getNode(List<Product> bill){

           VBox printArea = new VBox();
           printArea.prefHeight(400);
           printArea.setSpacing(0);
           printArea.setMaxWidth(200);
           printArea.setBorder(Border.EMPTY);

           ObservableList<Node> list = printArea.getChildren();

           TextFlow header = new TextFlow(new Text(this.setHeader()));
           header.setTextAlignment(TextAlignment.CENTER);
           list.add(header);

            String billBody="";
            for(Product p:bill) {
                billBody += p.getName() + "\t" + p.getQuantity() + "\t" + p.getUnit() + "\t" + p.getTotal();
                billBody += "\n";
            }
            TextFlow body = new TextFlow(new Text(billBody));
            body.setTextAlignment(TextAlignment.JUSTIFY);
            list.add(body);

            TextFlow footer = new TextFlow(new Text(this.setFooter()));
            footer.setTextAlignment(TextAlignment.JUSTIFY);
            list.add(footer);

            TextFlow tag = new TextFlow(new Text(this.setTagLine()));
            tag.setTextAlignment(TextAlignment.CENTER);
            list.add(tag);

            return printArea;
    }
*/

    //Printing the body of the bill/daily report
    private String printBillBody(List<Product> bill){
        String billBody="";
        for(Product p:bill) {
            billBody += String.format("%-6s",p.getName());
            billBody+=  String.format("%6s",p.getQuantity());
            billBody+= String.format("%6s",p.getUnit());
            billBody+= String.format("%10s", p.getTotal());
            billBody += "\n";
        }
        return billBody;
    }

    //Printing the footer of the daily report
    private String setStatFooter(List<Double> footer) {

        String foot="\nTotal Kilo\t= "+String.format("%12.2f",footer.get(0));
        foot+="\nKilo Price\t= "+String.format("%12.2f",footer.get(1));
        foot+="\nOil Sale  \t= "+String.format("%12.2f",footer.get(2));
        foot+="\nTotal Sale\t= "+String.format("%12.2f",footer.get(3));
        foot+="\nCash in   \t= "+String.format("%12.2f",footer.get(4));
        foot+="\nCash out  \t= "+String.format("%12.2f",footer.get(5));
        foot+="\nCash      \t= "+String.format("%12.2f",footer.get(6));
        foot+="\n";

        return foot;
    }

    //Printing the daily report
    public void printStat(List<Product> bill,List<Double> footer){
        String printArea=this.setHeader()+this.printBillBody(bill);
        printArea+=this.setStatFooter(footer)+this.setTagLine();
        this.print(printArea);
    }

    //Printing the footer of the bill
    private String setBillFooter(List<Double> footer) {
        String foot="\nTotal Amount\t= "+String.format("%10.2f",footer.get(0));
        foot+="\nGiven Amount\t= "+String.format("%10.2f",footer.get(1));
        foot+="\nBalance\t\t\t= "+String.format("%10.2f",footer.get(2));
        foot+="\n";
        return foot;
    }

    //Printing the bill
    public void printBill(List<Product> bill,List<Double> footer){
        String printArea=this.setHeader()+this.printBillBody(bill);
        printArea+=this.setBillFooter(footer)+this.setTagLine();
        this.print(printArea);
    }


    //Printing a string to a thermal printer
    public void print(String billText)  {
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;

        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
     //   PrintService printService[] = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.GIF, pras);
        PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
        PrintService service = this.findPrintService(printerName, printService);
      //  String billText = this.formatBill(bill);
        if(service==null) {
            System.out.println(billText);
            return;
        }
        DocPrintJob job = service.createPrintJob();
        byte[] bytes = billText.getBytes();
        Doc doc = new SimpleDoc(bytes, flavor, null);

        try {
            job.print(doc, null);
        }catch(Exception e){
            System.out.println(e);
        }
    }



}
