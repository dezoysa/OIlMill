package OilMill;

import javax.swing.plaf.synth.SynthRadioButtonMenuItemUI;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataConnection {
    private java.sql.Connection connection ;

    public void DataAccessor(String driverClassName, String dbURL, String user, String password) throws SQLException, ClassNotFoundException {
        Class.forName(driverClassName);
        connection =  DriverManager.getConnection(dbURL, user,password);
    }

    public void shutdown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public HashMap<Integer,Integer> getPriceList() throws SQLException {
        try (
                Statement stmnt = connection.createStatement();
                ResultSet rs = stmnt.executeQuery("select * from price")
        ){
            HashMap<Integer,Integer> pList=new HashMap<>();

            while (rs.next()) {
                int code = Integer.parseInt(rs.getString("code"));
                int price = Integer.parseInt(rs.getString("price"));
                pList.put(code,price);
            }
            return pList;
        }
    }

    public double getTotalQuantity(LocalDate date,int code) throws SQLException {
        String sql="SELECT SUM(quantity) FROM sales WHERE code="+code+" and date=\""+date.toString()+"\"";
        PreparedStatement statement =  connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        result.next();
        String value = result.getString(1);
        if (value!=null)  return Double.parseDouble(value);
        else return 0;
    }
    public double getTotalSale(LocalDate date) throws SQLException {
        String sql="SELECT SUM(total) FROM sales WHERE code!=0 and date=\""+date.toString()+"\"";
        PreparedStatement statement =  connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        result.next();
        String value = result.getString(1);
        if (value!=null) return Double.parseDouble(value);
        else return 0;
    }

    public HashMap<Integer,String> getProductNames() throws SQLException {
        try (
                Statement stmnt = connection.createStatement();
                ResultSet rs = stmnt.executeQuery("select * from product")
        ){
            HashMap<Integer,String> pNames=new HashMap<>();
            while (rs.next()) {
                int code = Integer.parseInt(rs.getString("code"));
                String name = rs.getString("name");
                pNames.put(code,name);
            }
            return pNames;
        }
    }

    public List<Product> getSalesList(LocalDate date) throws SQLException {
        try (
                Statement stmnt = connection.createStatement();
                ResultSet rs = stmnt.executeQuery("select * from sales where date=\""+date.toString()+"\"")
        ){
            List<Product> personList = new ArrayList<>();
            while (rs.next()) {
                int code = Integer.parseInt(rs.getString("code"));
                int unit = Integer.parseInt(rs.getString("unit"));
                double quantity = Double.parseDouble(rs.getString("quantity"));
                double total = Double.parseDouble(rs.getString("total"));
                Product p = new Product(code,Controller.productNames.get(code),unit,quantity,total);
                personList.add(p);
            }
            return personList ;
        }
    }

    public int putSale(LocalDate date,Product p) throws SQLException {

        String sql = "INSERT INTO sales (code,unit,quantity,total,date) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement stmnt = connection.prepareStatement(sql);
        stmnt.setInt(1,Integer.parseInt(p.getCode()));
        stmnt.setInt(2,Integer.parseInt(p.getUnit()));
        stmnt.setDouble(3,Double.parseDouble(p.getQuantity()));
        stmnt.setDouble(4,Double.parseDouble(p.getTotal()));
        stmnt.setString(5,date.toString());

        return stmnt.executeUpdate();
    }


    public void putProduct(int code, String name,int price)throws SQLException{
        String sql = "INSERT INTO product (code,name) VALUES (?,?)";
        PreparedStatement stmnt = connection.prepareStatement(sql);
        stmnt.setInt(1,code);
        stmnt.setString(2,name);
        stmnt.executeUpdate();

        String p = "INSERT INTO price (code,price) VALUES (?,?)";
        PreparedStatement statement = connection.prepareStatement(p);
        statement.setInt(1,code);
        statement.setInt(2,price);
        statement.executeUpdate();

    }

    public void update(int code, String name,int price)throws SQLException{
        String sql = "UPDATE product SET name ='"+name+"' WHERE code ="+code;

        PreparedStatement stmnt = connection.prepareStatement(sql);
        stmnt.executeUpdate();

        String p = "UPDATE price SET price ='"+price+"' WHERE code ="+code;
        PreparedStatement statement = connection.prepareStatement(p);
        statement.executeUpdate();

    }
}
