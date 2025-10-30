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
    // Slaes Representatives
    public List<SalesRepresentative> getSalesRepresentatives() throws SQLException {
        List<SalesRepresentative> salesReps = new ArrayList<>();
        String sql = "SELECT FullName, TotalRevenue , TotalOrders , PaidOrders, Tier " +
                "From v_sales_representatives";
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
                        fullName,totalRevenue,totalOrders,paidOrders,tier ,color);
                salesReps.add(salesRepresentative);
            }
        }
        catch (SQLException e)
        {
            System.out.println("Không lấy đc dữ liệu");
            e.printStackTrace();
        }

        return salesReps;
    }
    // lay du lieu bieu do hag ngay
    public XYChart.Series<String,Number> getOrderChartDate()
    {
        XYChart.Series<String,Number>series = new XYChart.Series<>();
        series.setName("ASK Order");
        String sql = "SELECT DayName ,  OrderCount from v_daily_orders Order by DayOfWeek";
        try ( Connection connection = DBConnection.getConnection();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql)) {
            while(rs.next())
            {
                String dayName = rs.getString("DayName");
                int orderCount = rs.getInt("OrderCount");
                series.getData().add(new XYChart.Data<>(dayName,orderCount));

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return series;
    }
    // claim year
    public List<XYChart.Series<String, Number>> getClaimsChartData() {
        XYChart.Series<String, Number> approvedSeries = new XYChart.Series<>();
        approvedSeries.setName("Approved");

        XYChart.Series<String, Number> submittedSeries = new XYChart.Series<>();
        submittedSeries.setName("Submitted");

        String sql = "SELECT Year, Approved, Submitted FROM v_yearly_claims ORDER BY Year";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String year = String.valueOf(rs.getInt("Year"));
                int approved = rs.getInt("Approved");
                int submitted = rs.getInt("Submitted");

                approvedSeries.getData().add(new XYChart.Data<>(year, approved));
                submittedSeries.getData().add(new XYChart.Data<>(year, submitted));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy dữ liệu từ v_yearly_claims: " + e.getMessage());
            e.printStackTrace();
        }

        List<XYChart.Series<String, Number>> result = new ArrayList<>();
        result.add(approvedSeries);
        result.add(submittedSeries);
        return result;
    }
    //Lấy tổng doanh thu từ VIEW
    public double getTotalRevenue() {
        String sql = "SELECT TotalRevenue FROM v_revenue_summary";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("TotalRevenue");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy TotalRevenue từ v_revenue_summary: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }

    public int getTotalOrders() {
        String sql = "SELECT TotalOrders FROM v_revenue_summary";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("TotalOrders");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy TotalOrders từ v_revenue_summary: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }


    public double getTotalRevenueClaimTop1() {
        String sql = "SELECT \n" +
                "    MAX(TotalRevenue) AS MaxTotalRevenue\n" +
                "FROM v_yearly_claims;\n";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("MaxTotalRevenue");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }

    public int getDeliveredOrders() {
        String sql = "SELECT CompletedOrders FROM v_revenue_summary";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("CompletedOrders");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy TotalOrders từ v_revenue_summary: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
    public int getCancelledOrders() {
        String sql = "SELECT CancelledOrders FROM v_revenue_summary";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("CancelledOrders");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy TotalOrders từ v_revenue_summary: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
    //biểu đồ tròn 1: Lấy ra tổng hàng được completed so với total
    public double getTotalOrderPercentage() {
        String sql = "SELECT " +
                "CASE WHEN TotalOrders > 0 THEN (CompletedOrders * 100.0 / TotalOrders) ELSE 0 END AS Percentage " +
                "FROM v_revenue_summary";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("Percentage");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy Total Order Percentage: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }
    //biểu đồ tròn 2: so sánh số lượng khách đặt order trong tháng này với thàng trước
    public double getCustomerGrowthPercentage() {
        String sql = "SELECT " +
                "CASE WHEN LastMonthCustomers > 0 " +
                "THEN ((CurrentMonthCustomers - LastMonthCustomers) * 100.0 / LastMonthCustomers) " +
                "ELSE 100.0 END AS GrowthPercentage " +
                "FROM ( " +
                "    SELECT " +
                "        (SELECT COUNT(DISTINCT TableID) FROM `Order` " +
                "         WHERE MONTH(CheckoutTime) = MONTH(CURRENT_DATE) " +
                "         AND YEAR(CheckoutTime) = YEAR(CURRENT_DATE) " +
                "         AND Status = 'Paid') AS CurrentMonthCustomers, " +
                "        (SELECT COUNT(DISTINCT TableID) FROM `Order` " +
                "         WHERE MONTH(CheckoutTime) = MONTH(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH)) " +
                "         AND YEAR(CheckoutTime) = YEAR(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH)) " +
                "         AND Status = 'Paid') AS LastMonthCustomers " +
                ") AS CustomerCounts";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                double growth = rs.getDouble("GrowthPercentage");
                // Giới hạn trong khoảng 0-100% để hiển thị
                return Math.min(Math.max(Math.abs(growth), 0.0), 100.0);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy Customer Growth Percentage: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }
    //biểu đồ tròn 2: so sánh doanh thu đặt order trong tháng này với thàng trước
    public double getTotalRevenuePercentage() {
        String sql = "SELECT " +
                "CASE WHEN LastMonthRevenue > 0 " +
                "THEN (CurrentMonthRevenue * 100.0 / (LastMonthRevenue * 1.2)) " +
                "ELSE 50.0 END AS RevenuePercentage " +
                "FROM ( " +
                "    SELECT " +
                "        (SELECT COALESCE(SUM(Total), 0) FROM `Order` " +
                "         WHERE MONTH(CheckoutTime) = MONTH(CURRENT_DATE) " +
                "         AND YEAR(CheckoutTime) = YEAR(CURRENT_DATE) " +
                "         AND Status = 'Paid') AS CurrentMonthRevenue, " +
                "        (SELECT COALESCE(SUM(Total), 1) FROM `Order` " +
                "         WHERE MONTH(CheckoutTime) = MONTH(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH)) " +
                "         AND YEAR(CheckoutTime) = YEAR(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH)) " +
                "         AND Status = 'Paid') AS LastMonthRevenue " +
                ") AS RevenueData";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                double percentage = rs.getDouble("RevenuePercentage");
                return Math.min(Math.max(percentage, 0.0), 100.0);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy Total Revenue Percentage: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }



    private String getColorByTier(String tier) {
        switch (tier) {
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
