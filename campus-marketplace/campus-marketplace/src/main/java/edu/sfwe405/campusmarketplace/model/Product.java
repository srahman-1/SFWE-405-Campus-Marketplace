package edu.sfwe405.campusmarketplace.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private double price;

    @Column(nullable = false)
    private int stock = 0;

    @ManyToOne
    @JsonIgnore
    private UserAccount owner;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Product() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public UserAccount getOwner() {
        return owner;
    }

    public void setOwner(UserAccount owner) {
        this.owner = owner;
    }

    @JsonProperty("ownerId")
    public Long getOwnerId() {
        return owner != null ? owner.getId() : null;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
