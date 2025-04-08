package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT o FROM Order o JOIN o.user u WHERE u.id = ?1")
    List<Order> getOrdersById(UUID userId);

    @Query("SELECT u FROM User u Where u.name = :name")
    boolean existsByName(String name);
}
