package com.example.demo.service.crud;

import com.example.demo.dto.OrderDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.error.NotFoundError;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository) {
        this.orderRepository   = orderRepository;
        this.userRepository    = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderDTO createOrder(String currentUser, OrderDTO dto) {
        dto.setCreatedBy(currentUser);
        // 1) Fetch & validate user
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NotFoundError("Хэрэглэгч олдсонгүй"));

        // 2) Fetch & validate products
        List<UUID> requestedIds = Optional.ofNullable(dto.getProductIds())
                .orElseThrow(() -> new NotFoundError("Барааны мэдээлэл олдсонгүй"));
        List<Product> products = productRepository.findAllById(requestedIds);
        if (products.size() != requestedIds.size()) {
            throw new NotFoundError("Зарим бараа олдсонгүй");
        }

        // 3) Build Order + join rows
        Order order = new Order();
        order.setOrderDate(dto.getOrderDate());
        order.setUser(user);

        for (Product p : products) {
            OrderProduct op = new OrderProduct();
            op.setOrder(order);
            op.setProduct(p);
            order.getOrderProducts().add(op);
        }

        order.setTotalAmount(calculateTotalAmount(order));

        // 4) Save cascade‐style
        Order saved = orderRepository.save(order);
        return OrderMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Захиалга олдсонгүй"));
        return OrderMapper.toDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    @Transactional
    public OrderDTO updateOrder(String currentUser, UUID id, OrderDTO dto) {
        // Fetch existing order
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Захиалга олдсонгүй"));

        // Update audit fields
        order.setUpdatedBy(currentUser);

        // Update basic fields
        order.setOrderDate(dto.getOrderDate());

        // Update user if changed
        if (!order.getUser().getId().equals(dto.getUserId())) {
            User newUser = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new NotFoundError("Хэрэглэгч олдсонгүй"));
            order.setUser(newUser);
        }

        // Handle product updates
        List<UUID> newProductIds = Optional.ofNullable(dto.getProductIds())
                .orElseGet(Collections::emptyList);

        // Get current product IDs in the order
        Set<UUID> currentProductIds = order.getOrderProducts().stream()
                .map(op -> op.getProduct().getId())
                .collect(Collectors.toSet());

        // If no changes to products, skip the update
        if (new HashSet<>(newProductIds).equals(currentProductIds)) {
            Order updated = orderRepository.save(order);
            return OrderMapper.toDto(updated);
        }

        // Fetch all requested products (validate they exist)
        List<Product> requestedProducts = productRepository.findAllById(newProductIds);
        if (requestedProducts.size() != newProductIds.size()) {
            throw new NotFoundError("Зарим бараа олдсонгүй");
        }

        // Create a map for quick lookup
        Map<UUID, Product> productMap = requestedProducts.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // First, remove products that are no longer in the order
        order.getOrderProducts().removeIf(op -> !newProductIds.contains(op.getProduct().getId()));

        // Then add new products that weren't already in the order
        newProductIds.forEach(productId -> {
            if (!currentProductIds.contains(productId)) {
                OrderProduct op = new OrderProduct();
                op.setOrder(order);
                op.setProduct(productMap.get(productId));
                order.getOrderProducts().add(op);
            }
        });

        // Recalculate total and save
        order.setTotalAmount(calculateTotalAmount(order));
        Order updated = orderRepository.save(order);
        return OrderMapper.toDto(updated);
    }

    @Transactional
    public void deleteOrder(UUID id) {
        if (!orderRepository.existsById(id)) {
            throw new NotFoundError("Захиалга олдсонгүй");
        }
        orderRepository.deleteById(id);
    }

    private double calculateTotalAmount(Order order) {
        return order.getOrderProducts()
                .stream()
                .mapToDouble(op -> op.getProduct().getPrice())
                .sum();
    }
}
