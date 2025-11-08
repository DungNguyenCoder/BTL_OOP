package com.btl_oop.Model.DAO;
import com.btl_oop.Model.Data.SalesRepresentative;
import com.btl_oop.Utils.DBConnection;
import javafx.scene.chart.XYChart;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
public class ReportDAO {
    public List<SalesRepresentative> getSalesRepresentatives() throws SQLException {
        List<SalesRepresentative> salesReps = new ArrayList<>();
        String sql = """
            SELECT FullName, TotalRevenue, TotalOrders, PaidOrders, Tier 
            FROM v_sales_representatives_month 
            ORDER BY TotalRevenue DESC
            """;
        try (Connection conn = DBConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql))
        {
            while(resultSet.next())
            {
                String fullName = resultSet.getString("FullName");
                double totalRevenue = resultSet.getDouble("TotalRevenue");
                int totalOrders = resultSet.getInt("TotalOrders");
                int paidOrders = resultSet.getInt("PaidOrders");
                String tier = resultSet.getString("Tier");
                String color = getColorByTier(tier);
                SalesRepresentative salesRepresentative = new SalesRepresentative(
                        fullName, totalRevenue, totalOrders, paidOrders, tier, color);
                salesReps.add(salesRepresentative);
            }
        }
        catch (SQLException e)
        {
            System.out.println("Unable to retrieve sales representatives data from v_sales_representatives_month.");
            e.printStackTrace();
        }
        return salesReps;
    }
    public XYChart.Series<String,Number> getOrderChartDate()
    {
        XYChart.Series<String,Number> series = new XYChart.Series<>();
        series.setName("Orders");
        String sql = """
            SELECT DayName, OrderCount 
            FROM v_daily_orders 
            ORDER BY OrderDate ASC
            """;
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql))
        {
            while(rs.next())
            {
                String dayName = rs.getString("DayName");
                int orderCount = rs.getInt("OrderCount");
                series.getData().add(new XYChart.Data<>(dayName, orderCount));
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error retrieving order chart data from v_daily_orders: " + e.getMessage());
            e.printStackTrace();
        }
        return series;
    }
    public List<XYChart.Series<String, Number>> getClaimsChartData()
    {
        XYChart.Series<String, Number> approvedSeries = new XYChart.Series<>();
        approvedSeries.setName("Approved");
        XYChart.Series<String, Number> submittedSeries = new XYChart.Series<>();
        submittedSeries.setName("Submitted");
        String sql = """
            SELECT Year, Approved, Submitted 
            FROM v_yearly_claims 
            ORDER BY Year
            """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                String year = String.valueOf(rs.getInt("Year"));
                int approved = rs.getInt("Approved");
                int submitted = rs.getInt("Submitted");
                approvedSeries.getData().add(new XYChart.Data<>(year, approved));
                submittedSeries.getData().add(new XYChart.Data<>(year, submitted));
            }

        }
        catch (SQLException e)
        {
            System.err.println("Error retrieving data from v_yearly_claims: " + e.getMessage());
            e.printStackTrace();
        }
        List<XYChart.Series<String, Number>> result = new ArrayList<>();
        result.add(approvedSeries);
        result.add(submittedSeries);
        return result;
    }
    public int getCompletedOrdersToday()
    {
        String sql = """
        SELECT CompletedOrders 
        FROM v_revenue_summary_today
        """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            if (rs.next())
            {
                return rs.getInt("CompletedOrders");
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error retrieving CompletedOrders from v_revenue_summary_today: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
    public int getTotalOrders()
    {
        String sql = """
            SELECT TotalOrders 
            FROM v_revenue_summary_month
            """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            if (rs.next())
            {
                return rs.getInt("TotalOrders");
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error retrieving TotalOrders from v_revenue_summary_month: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
    public double getTotalRevenue()
    {
        String sql = """
            SELECT TotalRevenue 
            FROM v_revenue_summary_month
            """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            if (rs.next())
            {
                return rs.getDouble("TotalRevenue");
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error retrieving TotalRevenue from v_revenue_summary_month: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }
    public double getHighestDailyOrder()
    {
        String sql = """
            SELECT HighestOrderValue 
            FROM v_revenue_summary_month
            """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            if (rs.next())
            {
                return rs.getDouble("HighestOrderValue");
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error retrieving HighestOrderValue from v_revenue_summary_month: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getTotalOrderPercentage()
    {
        String sql = """
            SELECT 
                CASE WHEN TotalOrders > 0 
                THEN (CompletedOrders * 100.0 / TotalOrders) 
                ELSE 0 
                END AS Percentage 
            FROM v_revenue_summary_today
            """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            if (rs.next())
            {
                return rs.getDouble("Percentage");
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error retrieving Total Order Percentage from v_revenue_summary_today: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }
    public double getOrderGrowthPercentage()
    {
        String sql = """
            SELECT 
                CASE WHEN YesterdayOrders > 0 
                THEN ((TodayOrders - YesterdayOrders) * 100.0 / YesterdayOrders) 
                ELSE CASE WHEN TodayOrders > 0 THEN 100.0 ELSE 0.0 END 
                END AS GrowthPercentage 
            FROM (
                SELECT 
                    (SELECT COUNT(*) 
                     FROM `Order` 
                     WHERE DATE(CheckoutTime) = CURDATE() 
                     AND CheckoutTime IS NOT NULL) AS TodayOrders,
                    (SELECT COUNT(*) 
                     FROM `Order` 
                     WHERE DATE(CheckoutTime) = DATE_SUB(CURDATE(), INTERVAL 1 DAY) 
                     AND CheckoutTime IS NOT NULL) AS YesterdayOrders 
            ) AS OrderCounts
            """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            if (rs.next())
            {
                double growth = rs.getDouble("GrowthPercentage");
                return Math.min(Math.max(growth, 0.0), 100.0);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error retrieving Order Growth Percentage: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }
    public double getTotalRevenuePercentage()
    {
        String sql = """
            SELECT 
                (TodayRevenue * 100.0 / 1000) AS RevenuePercentage 
            FROM (
                SELECT 
                    COALESCE(SUM(t.Total), 0) AS TodayRevenue
                FROM v_order_totals t
                JOIN `Order` o ON t.OrderID = o.OrderID
                WHERE DATE(o.CheckoutTime) = CURDATE() 
                  AND o.Status = 'Paid' 
                  AND o.CheckoutTime IS NOT NULL
            ) AS RevenueData
            """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            if (rs.next())
            {
                double percentage = rs.getDouble("RevenuePercentage");
                return Math.min(Math.max(percentage, 0.0), 100.0);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error retrieving Total Revenue Percentage: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }
    private String getColorByTier(String tier)
    {
        switch (tier)
        {
            case "Gold":
                return "#d4a574";
            case "Silver":
                return "#6b9fc9";
            case "Bronze":
                return "#a8a8a8";
            default:
                return "#8b7fc9";
        }
    }
}