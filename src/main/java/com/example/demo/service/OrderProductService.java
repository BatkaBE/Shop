package com.example.demo.service;

import com.example.demo.dto.OrderProductDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderProduct;
import com.example.demo.entity.Product;
import com.example.demo.repository.OrderProductRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderProductService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    public OrderProductService(OrderRepository orderRepository, ProductRepository productRepository, OrderProductRepository orderProductRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderProductRepository = orderProductRepository;
    }

    @Transactional
    public void addProductsToOrder(OrderProductDTO orderProductDTO) {
        Order order = orderRepository.findById(orderProductDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order ID " + orderProductDTO.getOrderId() + " not found!"));

        Product product = productRepository.findById(orderProductDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product ID " + orderProductDTO.getProductId() + " not found!"));

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(order);
        orderProduct.setProduct(product);
        orderProductRepository.save(orderProduct);
    }

    public List<ProductDTO> getProductsByOrderId(Long orderId) {
        return orderProductRepository.getProductsByOrderId(orderId).stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }

    public void deleteProductFromOrder(OrderProductDTO orderProductDTO) {
        Order order = orderRepository.findById(orderProductDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order ID " + orderProductDTO.getOrderId() + " not found!"));

        Product product = productRepository.findById(orderProductDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product ID " + orderProductDTO.getProductId() + " not found!"));

        orderProductRepository.deleteByOrderAndProduct(order, product);
    }

    @Transactional
    public void updateProductsInOrder(Long orderId, List<OrderProductDTO> toRemove, List<OrderProductDTO> toAdd) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order ID " + orderId + " not found!"));

        for (OrderProductDTO dto : toRemove) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product ID " + dto.getProductId() + " not found!"));
            orderProductRepository.deleteByOrderAndProduct(order, product);
        }

        for (OrderProductDTO dto : toAdd) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product ID " + dto.getProductId() + " not found!"));
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProductRepository.save(orderProduct);
        }
    }

    private ProductDTO convertToProductDTO(Product product) {
        return new ProductDTO(product.getId(), product.getName(), product.getPrice(), product.getQuantity());
    }
}