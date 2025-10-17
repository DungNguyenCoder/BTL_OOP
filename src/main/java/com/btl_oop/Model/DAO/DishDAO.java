package com.btl_oop.Model.DAO;

import com.almasb.fxgl.physics.CollisionDetectionStrategy;
import com.btl_oop.Model.Entity.Dish;
import com.btl_oop.Utils.DBConnection;


import javax.swing.plaf.basic.BasicListUI;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DishDAO {
    public DishDAO() {
        ensureTable();

    }
    private void ensureTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Dish (" +
                "DishID INT PRIMARY KEY AUTO_INCREMENT, " +
                "Name VARCHAR(100) NOT NULL, " +
                "Price DECIMAL(10,2) NOT NULL CHECK (Price >= 0), " +
                "Description TEXT, " +
                "PrepareTime INT CHECK (PrepareTime >= 0), " +
                "Category VARCHAR(50), " +
                "ImageURL VARCHAR(255))";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'Dish' ensured successfully!");
        } catch (SQLException e) {
            throw new RuntimeException(" Failed to ensure Dish table", e);
        }
    }


    //get all list

    public List<Dish> getAllDish() {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT * FROM Dish ORDER BY DishId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Dish a = new Dish(
                        rs.getInt("DishId"),
                        rs.getString("Name"),
                        rs.getDouble("Price"),
                        rs.getString("Description"),
                        rs.getInt("PreparedTime"),
                        rs.getString("Category"),
                        rs.getString("ImageURL")
                );
                dishes.add(a);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load dishes", e);
        }

        return dishes;
    }
   //get dish by category
    public List<Dish> getDishesByCategory(String category) throws SQLException {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT * FROM Dish WHERE Category = ?  ORDER BY DishId";
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement   ps = conn.prepareStatement(sql))
        {
            ps.setString(1,category);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
                Dish dish = new Dish(
                        rs.getInt("DishId"),
                        rs.getString("Name"),
                        rs.getDouble("Price"),
                        rs.getString("Description"),
                        rs.getInt("PreparedTime"),
                        rs.getString("Category"),
                        rs.getString("ImageURL")
                );
                dishes.add(dish);
            }
        }
        catch(SQLException e)
        {
            throw new RuntimeException("Failed to load dishes", e);
        }
        return dishes;
    }
    //insert
    public boolean insertDish(Dish dish) throws SQLException {
        String sql = "Insert Into Dish(Name,Price,Description,PrepareTime,Category,ImageURL) values (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dish.getName());
            ps.setDouble(2, dish.getPrice());
            ps.setString(3, dish.getDescription());
            ps.setInt(4, dish.getPrepareTime());
            ps.setString(5, dish.getCategory());
            ps.setString(6, dish.getImageURL());

            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet key = ps.getGeneratedKeys()) {
                if (key.next()) {
                    dish.setDishId(key.getInt(1));
                }
            }
            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert Dish", e);
        }
    }
    //UPDATE
   public boolean updateDisk(Dish dish)
   {
       String sql = "Update Dish Set Name = ? , Price = ? , Description = ?,PrepareTime=?,Category =? ,ImageURL = ? where DishId = ? ";
       try(Connection conn = DBConnection.getConnection();
       PreparedStatement ps=  conn.prepareStatement(sql) ) {
           ps.setString(1,dish.getName());
           ps.setDouble(2,dish.getPrice());
           ps.setString(3, dish.getDescription());
           ps.setInt(4,dish.getPrepareTime());
           ps.setString(5, dish.getCategory());
           ps.setString(6,dish.getImageURL());
           ps.setInt(7, dish.getDishId());

           return ps.executeUpdate() >0;
       } catch (SQLException e) {
           throw new RuntimeException("Fail to update Dish",e);

       }
   }
   //DELETE
    public boolean delete(Dish dish)
    {
        String sql = "DELETE FROM Dish WHERE DishId = ?";
        try(
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
                ) {
            ps.setInt(1,dish.getDishId());
            return ps.executeUpdate() >0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

