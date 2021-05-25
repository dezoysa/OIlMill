package OilMill;

import java.util.HashMap;
import java.util.Map;

public class Product {

    private int code = 0;
    private String name = null;
    private int unit = 0;
    private double quntity=0;
    private double total=0;

    public Product() {
    }

    public Product(int code, String name, int unit,double q,double total) {
        this.code = code;
        this.name = name;
        this.unit = unit;
        this.quntity = q;
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

    public String getQuantity() {return Double.toString(quntity);}
    public void setQuantity(double q) {
        this.quntity = q;
    }

    public String getTotal() {return Double.toString(total);}
    public void setTotal(double t) { this.total = t; }
}
