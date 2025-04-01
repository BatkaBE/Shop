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
    List<OrderProduct> findByOrderId(Long orderId);
    boolean existsByOrderAndProduct(Order order, Product product);
    void deleteByOrderAndProduct(Order order, Product product);

    @Query("SELECT SUM(p.price) FROM OrderProduct op JOIN op.product p WHERE op.order.id = :orderId")
    Optional<Double> sumProductPricesByOrder(Long orderId);

    @Modifying
    @Query("DELETE FROM OrderProduct op WHERE op.order.user.id = :userId")
    void deleteByOrderUser_Id(Long userId);

    @Query("SELECT op FROM OrderProduct op JOIN FETCH op.product WHERE op.order.user.id = :userId")
    List<OrderProduct> findByOrderUser_Id(Long userId);
}