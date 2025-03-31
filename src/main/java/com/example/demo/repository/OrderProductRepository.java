package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderProduct;
import com.example.demo.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("SELECT p FROM Product p " +
            "JOIN OrderProduct op ON op.product.id = p.id " +
            "JOIN Order o ON o.id = op.order.id " +
            "JOIN User u ON u.id = o.user.id " +
            "WHERE u.id = :userId")
    List<Product> findOrderedProductsByUser(Long userId);


    @Modifying
    @Query("DELETE FROM OrderProduct op WHERE op.order.user.id = :userId")
    void deleteProductsByUserId(Long userId);
//    @Query("SELECT p FROM Product p WHERE p.id IN (SELECT op.product.id FROM OrderProduct op WHERE op.order.id = :orderId)")
//    List<Product> findProductsByOrderId(Long orderId);
}
