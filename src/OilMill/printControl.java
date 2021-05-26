package OilMill;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

public class printControl {
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private final Date  date = new Date();
    private final String printDate = dateFormat.format(date);
    private final String printTime = timeFormat.format(date);
    private final DecimalFormat decim = new DecimalFormat("0.00");

    private double tot=0;
    private double giv=0;
    private double bal=0;

    public printControl(double tot,double giv,double bal){
        this.tot=tot;
        this.giv=giv;
        this.bal=bal;
    }

    private PrintService findPrintService(String printerName,PrintService[] services) {
        for (PrintService service : services) {
            System.out.println(service.getName());
            if (service.getName().equalsIgnoreCase(printerName)) return service;
        }
        return null;
    }

    private String setHeader() {
        String header = "\t\t**  Sekkuwa  **\n" +
                        "\t\t  091-2255898\n" +
                        "Date: "+printDate+"\tTime: "+printTime+"\n"
                        +"---------------------------------\n"
                        +"Name\t\tQty\t\tRate\tAmt\n"
                        +"---------------------------------\n";
        return header;
    }
    private String setFooter() {
        String footter =  "\nTotal Amount\t=\t"+decim.format(this.tot)+"\n"+
                        "Given Amount\t=\t"+decim.format(this.giv)+"\n"+
                        "Balance\t\t\t=\t"+decim.format(this.bal)+"\n"+
                        "********************************\n"+
                        " Coconut Oil: Miracle of Nature\n";
        return footter;
    }

    public String formatBill(List<Product> bill){
        String billBody="";
        for(Product p:bill) {
            billBody += p.getName() + "\t" + p.getQuantity() + "\t" + p.getUnit() + "\t" + p.getTotal();
            billBody += "\n";
        }

        String billText=this.setHeader()+billBody+this.setFooter();
        System.out.println(billText);
        return billText;
    }

    public void print(List<Product> bill){
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

        PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
        PrintService service = this.findPrintService("TM-T82-S-A", printService);
        //PrintService service = this.findPrintService("KasunOffice", printService);
        DocPrintJob job=null;
        if(service!=null) job = service.createPrintJob();
        try {
            byte[] bytes = this.formatBill(bill).getBytes();
            Doc doc = new SimpleDoc(bytes, flavor, null);
            if(job!=null) job.print(doc,null );
        } catch (Exception e) {
                System.err.println(e.getMessage());
        }
    }

}
