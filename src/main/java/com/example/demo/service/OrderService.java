package com.example.demo.service;

import com.example.demo.dto.OrderDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.NotFoundError;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository,
                        OrderProductRepository orderProductRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderProductRepository = orderProductRepository;
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = convertToEntity(orderDTO);
        Order savedOrder = orderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Захиалга олдсонгүй"));
        return convertToDTO(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Захиалга олдсонгүй"));

        // Update basic fields
        existingOrder.setOrderDate(orderDTO.getOrderDate());

        // Handle user change
        if (!existingOrder.getUser().getId().equals(orderDTO.getUserId())) {
            User newUser = userRepository.findById(orderDTO.getUserId())
                    .orElseThrow(() -> new NotFoundError("Хэрэглэгч олдсонгүй"));
            existingOrder.setUser(newUser);
        }

        // Update products
        updateOrderProducts(existingOrder, orderDTO.getProductIds());

        // Recalculate total amount
        existingOrder.setTotalAmount(calculateTotalAmount(existingOrder));

        Order updatedOrder = orderRepository.save(existingOrder);
        return convertToDTO(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new NotFoundError("Захиалга олдсонгүй");
        }
        orderRepository.deleteById(id);
    }

    private OrderDTO convertToDTO(Order order) {
        List<Long> productIds = order.getOrderProducts().stream()
                .map(op -> op.getProduct().getId())
                .collect(Collectors.toList());

        return new OrderDTO(
                order.getId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getUser().getId(),
                productIds
        );
    }

    private Order convertToEntity(OrderDTO orderDTO) {
        Order order = new Order();
        order.setId(orderDTO.getId());
        order.setOrderDate(orderDTO.getOrderDate());

        // Set user
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new NotFoundError("Хэрэглэгч олдсонгүй"));
        order.setUser(user);

        // Add products
        List<Product> products = productRepository.findAllById(orderDTO.getProductIds());
        if (products.isEmpty()) {
            throw new NotFoundError("Барааны мэдээлэл олдсонгүй");
        }

        products.forEach(product -> {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            order.getOrderProducts().add(orderProduct);
        });

        // Calculate total amount
        order.setTotalAmount(calculateTotalAmount(order));

        return order;
    }

    private void updateOrderProducts(Order order, List<Long> newProductIds) {
        // Remove existing products not in new list
        order.getOrderProducts().removeIf(op ->
                !newProductIds.contains(op.getProduct().getId())
        );

        // Add new products
        List<Long> existingProductIds = order.getOrderProducts().stream()
                .map(op -> op.getProduct().getId())
                .collect(Collectors.toList());

        productRepository.findAllById(newProductIds).forEach(product -> {
            if (!existingProductIds.contains(product.getId())) {
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setOrder(order);
                orderProduct.setProduct(product);
                order.getOrderProducts().add(orderProduct);
            }
        });
    }

    private Double calculateTotalAmount(Order order) {
        return order.getOrderProducts().stream()
                .mapToDouble(op -> op.getProduct().getPrice())
                .sum();
    }
}