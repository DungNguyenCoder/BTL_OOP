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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
    private Label newCustomer;

    public ReportDAO reportDAO;

    @FXML
    public void initialize() throws SQLException {
        reportDAO = new ReportDAO();
        setupOrderChart();
        setupClaimsChart();
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

    // Method để thêm sales rep mới từ bên ngoài
    public void addNewSalesRepresentative(SalesRepresentative salesRep) {
        addSalesItem(salesRep);
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
        productSold.setText(String.valueOf(reportDAO.getTotalOrders()));
        totalProfit.setText(String.valueOf(reportDAO.getTotalRevenue()) + "M");
        totalClaim.setText(String.valueOf(reportDAO.getTotalRevenueClaimTop1()) + "M");
    }
}