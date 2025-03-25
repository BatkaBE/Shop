package com.example.demo.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
public class ProductDTO {

    private Long id;

    private String name;
    private Double price;
    private int quantity;

}
