package com.example.demo.repository;
import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT o FROM Order o JOIN o.user u WHERE u.id = ?1")
    List<Order> getOrdersById(Long userId);

}
