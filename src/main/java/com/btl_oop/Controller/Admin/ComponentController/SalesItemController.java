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
        productsLabel.setText(salesRep.getProducts() + " Orders");
        premiumLabel.setText(salesRep.getPremium() + " Paid Orders");
        statusBadge.setText("+" + salesRep.getStatus());
        if (salesRep.getStatus().equalsIgnoreCase("Gold")) {
            statusBadge.getStyleClass().add("status-badge");
            statusBadge.getStyleClass().add("gold");
        } else if (salesRep.getStatus().equalsIgnoreCase("Silver")) {
            statusBadge.getStyleClass().add("status-badge");
            statusBadge.getStyleClass().add("silver");
        } else if (salesRep.getStatus().equalsIgnoreCase("Bronze")) {
            statusBadge.getStyleClass().add("status-badge");
            statusBadge.getStyleClass().add("bronze");
        }
    }
}