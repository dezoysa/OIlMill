package OilMill;

public class Product {

    private int code = 0;
    private String name = null;
    private int unit = 0;
    private double quantity =0;
    private double total=0;
    private int price = 0;

    public Product(int code,String name,int price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public Product(int code, String name, int unit,double q) {
        this.code = code;
        this.name = name;
        this.unit = unit;
        this.quantity = q;
        this.total = unit*q;
    }

    public Product(int code, String name, int unit,double q,double total) {
        this.code = code;
        this.name = name;
        this.unit = unit;
        this.quantity = q;
        this.total = total;
    }

    public String getCode() {
        return Integer.toString(code);
    }
    public void setCode(int code) {
        this.code = code;
    }

    public String getName() { return name;}
    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {return Integer.toString(unit);}
    public void setUnit(int u) {
        this.unit = u;
    }

    public String getQuantity() {
        if(quantity==0) return "";
        else return String.format("%.2f", quantity);
    }
    public void setQuantity(double q) {
        this.quantity = q;
    }

    public String getTotal() {return String.format("%.2f",total);}
    public void setTotal(double t) { this.total = t; }

    public String getPrice() {return Integer.toString(price);}
    public void setPrice(int t1) { this.price = t1; }

}
