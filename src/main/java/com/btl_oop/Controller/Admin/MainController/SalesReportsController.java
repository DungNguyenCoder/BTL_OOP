package com.btl_oop.Controller.Admin.MainController;
import com.btl_oop.Controller.Admin.ComponentController.SalesItemController;
import com.btl_oop.Model.DAO.ReportDAO;
import com.btl_oop.Model.Data.SalesRepresentative;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
public class SalesReportsController {
    @FXML
    private VBox salesListContainer;
    @FXML
    private AreaChart<String, Number> orderChart;
    @FXML
    private AreaChart<String, Number> claimsChart;
    @FXML
    private Label productSold;
    @FXML
    private Label totalProfit;
    @FXML
    private Label totalClaim;
    @FXML
    private Circle totalOrderCircle;
    @FXML
    private Label totalOrderPercentage;
    @FXML
    private Circle orderGrowthCircle;
    @FXML
    private Label orderGrowthPercentage;
    @FXML
    private Circle totalRevenueCircle;
    @FXML
    private Label totalRevenuePercentage;
    public ReportDAO reportDAO;
    @FXML
    public void initialize() throws SQLException {
        reportDAO = new ReportDAO();
        setupOrderChart();
        setupClaimsChart();
        setupPieCharts();
        loadSalesRepresentatives();
        changeLabel();
    }
    private void loadSalesRepresentatives() throws SQLException {
        List<SalesRepresentative> salesReps = reportDAO.getSalesRepresentatives();
        for (SalesRepresentative salesRep : salesReps) {
            addSalesItem(salesRep);
        }
    }
    private void addSalesItem(SalesRepresentative salesRep) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/btl_oop/FXML/Admin/components/sales_item.fxml")
            );
            HBox salesItem = loader.load();
            SalesItemController controller = loader.getController();
            controller.setData(salesRep);
            salesListContainer.getChildren().add(salesItem);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading sales item: " + e.getMessage());
        }
    }
    private void setupOrderChart() {
        XYChart.Series<String, Number> series = reportDAO.getOrderChartDate();
        orderChart.getData().add(series);
        orderChart.setCreateSymbols(true);
    }
    private void setupClaimsChart() {
        List<XYChart.Series<String, Number>> seriesList = reportDAO.getClaimsChartData();
        claimsChart.getData().addAll(seriesList);
        claimsChart.setCreateSymbols(true);
    }
    private void changeLabel() {
        int totalOrders = reportDAO.getTotalOrders();
        productSold.setText(String.valueOf(totalOrders));
        double totalRevenue = reportDAO.getTotalRevenue();
        totalProfit.setText(String.format("%.1f$", totalRevenue));
        double highestOrder = reportDAO.getHighestDailyOrder();
        totalClaim.setText(String.format("%.1f$", highestOrder));
    }
    private void setupPieCharts() {
        double totalOrderPercentageValue = reportDAO.getTotalOrderPercentage();
        updateDonutChart(totalOrderCircle, totalOrderPercentage, totalOrderPercentageValue);
        double orderGrowthPercentageValue = reportDAO.getOrderGrowthPercentage();
        updateDonutChart(orderGrowthCircle, orderGrowthPercentage, orderGrowthPercentageValue);
        double totalRevenuePercentageValue = reportDAO.getTotalRevenuePercentage();
        updateDonutChart(totalRevenueCircle, totalRevenuePercentage, totalRevenuePercentageValue);
    }
    private void updateDonutChart(Circle circle, Label label, double percentage) {
        if (circle == null || label == null) {
            return;
        }
        label.setText(String.format("%.1f%%", percentage));
        double circumference = 2 * Math.PI * 60;
        double dashLength = (percentage / 100.0) * circumference;
        double gapLength = circumference - dashLength;
        circle.getStrokeDashArray().clear();
        circle.getStrokeDashArray().addAll(dashLength, gapLength);
        circle.setStrokeDashOffset(circumference / 4);
    }
}