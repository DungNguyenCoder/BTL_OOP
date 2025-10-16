package com.btl_oop.Controller.Admin.MainController;

import com.btl_oop.Controller.Admin.ComponentController.SalesItemController;
import com.btl_oop.Model.Data.SalesRepresentative;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
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
    public void initialize() {
        setupOrderChart();
        setupClaimsChart();
        loadSalesRepresentatives();
    }

    private void loadSalesRepresentatives() {
        // Create sample data
        List<SalesRepresentative> salesReps = new ArrayList<>();

        salesReps.add(new SalesRepresentative(
                "Nicholas Patrick", 2540.58, 150, 105, "Gold", "#8b7fc9"
        ));

        salesReps.add(new SalesRepresentative(
                "Cordell Edwards", 1567.80, 95, 60, "Silver", "#6b9fc9"
        ));

        salesReps.add(new SalesRepresentative(
                "Derrick Spencer", 1640.26, 120, 75, "Silver", "#a8a8a8"
        ));

        salesReps.add(new SalesRepresentative(
                "Larissa Burton", 2340.58, 120, 99, "Gold", "#d4a574"
        ));

        // Load each sales item
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
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("ASK Order");

        series.getData().add(new XYChart.Data<>("Sunday", 380));
        series.getData().add(new XYChart.Data<>("Monday", 420));
        series.getData().add(new XYChart.Data<>("Tuesday", 456));
        series.getData().add(new XYChart.Data<>("Wednesday", 400));
        series.getData().add(new XYChart.Data<>("Thursday", 440));
        series.getData().add(new XYChart.Data<>("Friday", 480));
        series.getData().add(new XYChart.Data<>("Saturday", 460));

        orderChart.getData().add(series);
        orderChart.setCreateSymbols(true);
    }

    private void setupClaimsChart() {
        XYChart.Series<String, Number> approvedSeries = new XYChart.Series<>();
        approvedSeries.setName("Approved");
        approvedSeries.getData().add(new XYChart.Data<>("2015", 20));
        approvedSeries.getData().add(new XYChart.Data<>("2016", 30));
        approvedSeries.getData().add(new XYChart.Data<>("2017", 25));
        approvedSeries.getData().add(new XYChart.Data<>("2018", 45));
        approvedSeries.getData().add(new XYChart.Data<>("2019", 35));
        approvedSeries.getData().add(new XYChart.Data<>("2020", 40));

        XYChart.Series<String, Number> submittedSeries = new XYChart.Series<>();
        submittedSeries.setName("Submitted");
        submittedSeries.getData().add(new XYChart.Data<>("2015", 15));
        submittedSeries.getData().add(new XYChart.Data<>("2016", 22));
        submittedSeries.getData().add(new XYChart.Data<>("2017", 28));
        submittedSeries.getData().add(new XYChart.Data<>("2018", 35));
        submittedSeries.getData().add(new XYChart.Data<>("2019", 30));
        submittedSeries.getData().add(new XYChart.Data<>("2020", 38));

        claimsChart.getData().addAll(approvedSeries, submittedSeries);
        claimsChart.setCreateSymbols(true);
    }
}