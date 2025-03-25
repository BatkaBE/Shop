package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    @Id
    private Long id;

    @Column(nullable = false)
    private String address;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Shares the same ID as User
    @JoinColumn(name = "id") // Refers to the same PK column
    @ToString.Exclude
    private User user;
}