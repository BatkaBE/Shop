package com.example.demo.service;

import com.example.demo.dto.OrderProductDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.NotFoundError;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
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

    private Order createNewOrderForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundError("Хэрэглэгч олдсонгүй"));

        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setTotalAmount(0.0);
        return orderRepository.save(newOrder);
    }

    @Transactional
    public List<ProductDTO> getProductsByOrderId(Long orderId) {
        return orderProductRepository.findByOrderId(orderId).stream()
                .map(OrderProduct::getProduct)
                .map(this::convertToProductDTO)
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
    public void updateProductsInOrder(Long orderId, List<OrderProductDTO> toRemove, List<OrderProductDTO> toAdd) {
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
    public List<ProductDTO> findOrderedProductsByUser(Long userId) {
        return orderProductRepository.findByOrderUser_Id(userId).stream()
                .map(OrderProduct::getProduct)
                .distinct()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProductsByUserId(Long userId) {
        orderProductRepository.deleteByOrderUser_Id(userId);
    }

    // In OrderProductService.java
    private void addProductToOrder(Order order, Product product) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(order);  // Sets both relationship and ID
        orderProduct.setProduct(product);
        orderProductRepository.save(orderProduct);

        // Maintain bi-directional relationship
        order.getOrderProducts().add(orderProduct);
        product.getOrderProducts().add(orderProduct);
    }

    private void updateOrderTotal(Order order) {
        Double total = orderProductRepository.sumProductPricesByOrder(order.getId())
                .orElse(0.0);
        order.setTotalAmount(total);
        orderRepository.save(order);
    }

    private ProductDTO convertToProductDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategoryId()
        );
    }
}