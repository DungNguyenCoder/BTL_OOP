package com.btl_oop.Controller.Admin.ComponentController;

import com.btl_oop.Model.Data.SalesRepresentative;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class SalesItemController {

    @FXML
    private Circle avatarCircle;

    @FXML
    private Label nameLabel;

    @FXML
    private Label revenueLabel;

    @FXML
    private Label productsLabel;

    @FXML
    private Label premiumLabel;

    @FXML
    private Label statusBadge;

    public void setData(SalesRepresentative salesRep) {
        avatarCircle.setFill(Color.web(salesRep.getAvatarColor()));
        nameLabel.setText(salesRep.getName());
        revenueLabel.setText(String.format("$ %.2f", salesRep.getRevenue()));
        productsLabel.setText(salesRep.getProducts() + " Products");
        premiumLabel.setText(salesRep.getPremium() + " Premium");

        // Set status badge
        statusBadge.setText("+" + salesRep.getStatus());
        if (salesRep.getStatus().equalsIgnoreCase("Gold")) {
            statusBadge.getStyleClass().add("gold");
        } else if (salesRep.getStatus().equalsIgnoreCase("Silver")) {
            statusBadge.getStyleClass().add("silver");
        }
    }

    @FXML
    private void handleMenuClick() {
        System.out.println("Menu clicked for: " + nameLabel.getText());
        // TODO: Show context menu or options
    }
}