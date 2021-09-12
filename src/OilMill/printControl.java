package OilMill;

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
    private final DecimalFormat decim = new DecimalFormat("0.00");

    private final double tot;
    private final double giv;
    private final double bal;
    private final static String printerName = "TM-T82-S-A";

    public printControl(double tot,double giv,double bal){
        this.tot=tot;
        this.giv=giv;
        this.bal=bal;
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
         String header = "\t\t  Sekkuwa\n" +
                        "\t\t091-2255898\n" +
                        "Date:"+printDate+"\t\tTime:"+printTime+"\n"
                        +"----------------------------------------\n"
                        +"Name\t\tQty\tRate\tAmt\n"
                        +"----------------------------------------\n";
         return header;
    }


    private String setFooter() {
        String footter =  "\nTotal Amount\t=\t"+decim.format(this.tot)+"\n"+
                        "Given Amount\t=\t"+decim.format(this.giv)+"\n"+
                        "Balance \t=\t"+decim.format(this.bal)+"\n";
        return footter;
    }
    private String setTagLine() {
        String tag = "****************************************\n"+
                     "\tCoconut Oil: Miracle of Nature\n\n";
        return tag;
    }

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

    public String formatBill(List<Product> bill){
        String billBody="";
        for(Product p:bill) {
            billBody += p.getName() + "\t" + p.getQuantity() + "\t" + p.getUnit() + "\t" + p.getTotal();
            billBody += "\n";
        }

        String printArea=this.setHeader()+billBody+this.setFooter()+this.setTagLine();

        System.out.println(printArea);

        return printArea;
    }

    public void print(List<Product> bill)  {
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;

        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
     //   PrintService printService[] = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.GIF, pras);
        PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
        PrintService service = this.findPrintService(printerName, printService);
        if(service==null) return;
        DocPrintJob job = service.createPrintJob();
        byte[] bytes = this.formatBill(bill).getBytes();
        Doc doc = new SimpleDoc(bytes, flavor, null);

        try {
            job.print(doc, null);
        }catch(Exception e){
            System.out.println(e);
        }
    }

}
