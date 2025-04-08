package com.example.demo.service.crud;

import com.example.demo.dto.OrderProductDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.error.NotFoundError;
import com.example.demo.repository.*;
import com.example.demo.util.mapper.ProductMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderProductService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;
    private final UserRepository userRepository;

    public OrderProductService(OrderRepository orderRepository,
                               ProductRepository productRepository,
                               OrderProductRepository orderProductRepository,
                               UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderProductRepository = orderProductRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addProductsToOrderWithAutoCreate(OrderProductDTO orderProductDTO) {
        // Get or create order
        Order order = orderRepository.findById(orderProductDTO.getOrderId())
                .orElseGet(() -> createNewOrderForUser(orderProductDTO.getUserId()));

        // Get product
        Product product = productRepository.findById(orderProductDTO.getProductId())
                .orElseThrow(() -> new NotFoundError("Бараа олдсонгүй"));

        // Add product to order
        addProductToOrder(order, product);

        // Update order total
        updateOrderTotal(order);
    }

    private Order createNewOrderForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundError("Хэрэглэгч олдсонгүй"));

        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setTotalAmount(0.0);
        return orderRepository.save(newOrder);
    }

    @Transactional
    public List<ProductDTO> getProductsByOrderId(UUID orderId) {
        return orderProductRepository.findByOrderId(orderId).stream()
                .map(OrderProduct::getProduct)
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProductFromOrder(OrderProductDTO orderProductDTO) {
        Order order = orderRepository.findById(orderProductDTO.getOrderId())
                .orElseThrow(() -> new NotFoundError("Захиалга олдсонгүй"));

        Product product = productRepository.findById(orderProductDTO.getProductId())
                .orElseThrow(() -> new NotFoundError("Бараа олдсонгүй"));

        orderProductRepository.deleteByOrderAndProduct(order, product);
        updateOrderTotal(order);
    }

    @Transactional
    public void addProductsToOrder(OrderProductDTO orderProductDTO) {
        Order order = orderRepository.findById(orderProductDTO.getOrderId())
                .orElseThrow(() -> new NotFoundError("Захиалга олдсонгүй"));

        Product product = productRepository.findById(orderProductDTO.getProductId())
                .orElseThrow(() -> new NotFoundError("Бараа олдсонгүй"));

        if (!orderProductRepository.existsByOrderAndProduct(order, product)) {
            addProductToOrder(order, product);
            updateOrderTotal(order);
        }
    }

    @Transactional
    public void updateProductsInOrder(UUID orderId, List<OrderProductDTO> toRemove, List<OrderProductDTO> toAdd) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundError("Захиалга олдсонгүй"));

        // Remove products
        toRemove.forEach(dto -> {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new NotFoundError("Бараа олдсонгүй"));
            orderProductRepository.deleteByOrderAndProduct(order, product);
        });

        // Add new products
        toAdd.forEach(dto -> {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new NotFoundError("Бараа олдсонгүй"));
            if (!orderProductRepository.existsByOrderAndProduct(order, product)) {
                addProductToOrder(order, product);
            }
        });

        updateOrderTotal(order);
    }

    @Transactional
    public List<ProductDTO> findOrderedProductsByUser(UUID userId) {
        return orderProductRepository.findByOrderUser_Id(userId).stream()
                .map(OrderProduct::getProduct)
                .distinct()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProductsByUserId(UUID userId) {
        orderProductRepository.deleteByOrderUser_Id(userId);
    }

    // In OrderProductService.java
    private void addProductToOrder(Order order, Product product) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(order);
        orderProduct.setProduct(product);

        orderProductRepository.save(orderProduct);
    }


    private void updateOrderTotal(Order order) {
        Double total = orderProductRepository.sumProductPricesByOrder(order.getId())
                .orElse(0.0);
        order.setTotalAmount(total);
        orderRepository.save(order);
    }


}