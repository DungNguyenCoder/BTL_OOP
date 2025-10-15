package com.btl_oop.Controller.MainController;

import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

public class InventoryManagementController {

    @FXML
    private FlowPane categoriesPane;

    @FXML
    private FlowPane popularPane;

    @FXML
    private FlowPane allFoodsPane;

    @FXML
    public void initialize() {
        // Có thể thêm logic để load data động từ database
        System.out.println("Inventory Management initialized");
    }

    // Method để thêm sản phẩm mới (gọi khi click nút Add new)
    @FXML
    private void handleAddNew() {
        System.out.println("Add new product clicked");
        // TODO: Mở dialog để thêm sản phẩm mới
    }

    // Method để edit sản phẩm
    @FXML
    private void handleEditProduct() {
        System.out.println("Edit product clicked");
        // TODO: Mở dialog để edit sản phẩm
    }
}