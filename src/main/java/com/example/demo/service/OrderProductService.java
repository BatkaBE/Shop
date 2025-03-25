package com.example.demo.service;

import com.example.demo.dto.OrderProductDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderProduct;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.OrderProductRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
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
        Order order = orderRepository.findById(orderProductDTO.getOrderId())
                .orElseGet(() -> createNewOrderForUserByOrderId(orderProductDTO.getOrderId()));

        Product product = productRepository.findById(orderProductDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(orderProductDTO.getProductId()));

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(order);
        orderProduct.setProduct(product);
        orderProductRepository.save(orderProduct);
    }

    private Order createNewOrderForUserByOrderId(Long orderId) {
        // Retrieve user from the orderId
        User user = orderRepository.findById(orderId)
                .map(Order::getUser)
                .orElseThrow(() -> new UserNotFoundException("User not found for orderId: " + orderId));

        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setTotalAmount(0.0);
        return orderRepository.save(newOrder);
    }

    public List<ProductDTO> getProductsByOrderId(Long orderId) {
        return orderProductRepository.getProductsByOrderId(orderId).stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }

    public void deleteProductFromOrder(OrderProductDTO orderProductDTO) {
        Order order = orderRepository.findById(orderProductDTO.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(orderProductDTO.getOrderId()));

        Product product = productRepository.findById(orderProductDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(orderProductDTO.getProductId()));

        orderProductRepository.deleteByOrderAndProduct(order, product);
    }

    private ProductDTO convertToProductDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity()
        );
    }

    public void addProductsToOrder(OrderProductDTO orderProductDTO) {
        Order order = orderRepository.findById(orderProductDTO.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(orderProductDTO.getOrderId()));

        Product product = productRepository.findById(orderProductDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(orderProductDTO.getProductId()));

        boolean productExistsInOrder = order.getOrderProducts().stream()
                .anyMatch(op -> op.getProduct().getId().equals(product.getId()));

        if (!productExistsInOrder) {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProductRepository.save(orderProduct);
        }
    }

    public void updateProductsInOrder(Long orderId, List<OrderProductDTO> toRemove, List<OrderProductDTO> toAdd) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        for (OrderProductDTO removeDTO : toRemove) {
            Product productToRemove = productRepository.findById(removeDTO.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(removeDTO.getProductId()));

            OrderProduct orderProduct = orderProductRepository.findByOrderAndProduct(order, productToRemove)
                    .orElseThrow(() -> new RuntimeException("Product not found in the order"));

            orderProductRepository.delete(orderProduct);
        }

        for (OrderProductDTO addDTO : toAdd) {
            Product productToAdd = productRepository.findById(addDTO.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(addDTO.getProductId()));


            boolean productExistsInOrder = order.getOrderProducts().stream()
                    .anyMatch(op -> op.getProduct().getId().equals(productToAdd.getId()));

            if (!productExistsInOrder) {
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setOrder(order);
                orderProduct.setProduct(productToAdd);
                orderProductRepository.save(orderProduct);

            }
        }
    }
    public List<ProductDTO> findOrderedProductsByUser(Long userId) {
        List<Product> products = orderProductRepository.findOrderedProductsByUser(userId);
        return products.stream()
                .map(p -> new ProductDTO(p.getId(), p.getName(), p.getPrice(), p.getQuantity()))
                .collect(Collectors.toList());
    }
    @Transactional
    public void deleteProductsByUserId(Long userId) {
        orderProductRepository.deleteProductsByUserId(userId);
    }
}