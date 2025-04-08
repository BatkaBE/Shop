package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "order_product")
public class OrderProduct {
    @EmbeddedId
    private OrderProductId id;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    // Helper method to set both sides of relationship
    public void setOrder(Order order) {
        this.order = order;
        this.id.setOrderId(order != null ? order.getId() : null);
    }

    public void setProduct(Product product) {
        this.product = product;
        this.id.setProductId(product != null ? product.getId() : null);
    }
}