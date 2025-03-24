package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderProduct;
import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    @Query("SELECT p FROM Product p WHERE p.id IN (SELECT op.product.id FROM OrderProduct op WHERE op.order.id = :id)")
    List<Product> getProductsByOrderId(Long id);

    Optional<OrderProduct> findByOrderAndProduct(Order order, Product product);

    void deleteByOrderAndProduct(Order order, Product product);


//    @Query("SELECT p FROM Product p WHERE p.id IN (SELECT op.product.id FROM OrderProduct op WHERE op.order.id = :orderId)")
//    List<Product> findProductsByOrderId(Long orderId);
}
