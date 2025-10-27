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
   // Lấy tổng số đơn hàng từ VIEW

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

    /**
     * Lấy thông tin hiệu suất nhân viên từ VIEW
     */
    public List<Object[]> getEmployeePerformance() {
        List<Object[]> result = new ArrayList<>();

        String sql = "SELECT FullName, Role, TotalOrders, WorkingDays, TotalRevenue, AverageOrderValue, CompletedOrders " +
                "FROM v_employee_performance " +
                "ORDER BY TotalRevenue DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] row = new Object[]{
                        rs.getString("FullName"),
                        rs.getString("Role"),
                        rs.getInt("TotalOrders"),
                        rs.getInt("WorkingDays"),
                        rs.getDouble("TotalRevenue"),
                        rs.getDouble("AverageOrderValue"),
                        rs.getInt("CompletedOrders")
                };
                result.add(row);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy dữ liệu từ v_employee_performance: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    //Lấy dữ liệu doanh số theo tháng từ VIEW
    public List<Object[]> getMonthlySales() {
        List<Object[]> result = new ArrayList<>();

        String sql = "SELECT YearMonth, OrderCount, TotalRevenue, PaidOrders " +
                "FROM v_monthly_sales " +
                "LIMIT 12";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] row = new Object[]{
                        rs.getString("YearMonth"),
                        rs.getInt("OrderCount"),
                        rs.getDouble("TotalRevenue"),
                        rs.getInt("PaidOrders")
                };
                result.add(row);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy dữ liệu từ v_monthly_sales: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
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
