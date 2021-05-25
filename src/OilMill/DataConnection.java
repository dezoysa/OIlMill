package OilMill;

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
                ResultSet rs = stmnt.executeQuery("select * from price");
        ){
            HashMap<Integer,Integer> pList=new HashMap<Integer,Integer>();

            while (rs.next()) {
                int code = Integer.parseInt(rs.getString("code"));
                int price = Integer.parseInt(rs.getString("price"));
                pList.put(code,price);
            }
            return pList;
        }
    }

    public HashMap<Integer,String> getProductNames() throws SQLException {
        try (
                Statement stmnt = connection.createStatement();
                ResultSet rs = stmnt.executeQuery("select * from product");
        ){
            HashMap<Integer,String> pNames=new HashMap<Integer,String>();
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
                ResultSet rs = stmnt.executeQuery("select * from sales where date=\""+date.toString()+"\"");
        ){
            List<Product> personList = new ArrayList<>();
            while (rs.next()) {
                int code = Integer.parseInt(rs.getString("code"));
                int unit = Integer.parseInt(rs.getString("unit"));
                double quntity = Double.parseDouble(rs.getString("quantity"));
                double total = Double.parseDouble(rs.getString("total"));
                Product p = new Product(code,Controller.productNames.get(code),unit,quntity,total);
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


}
