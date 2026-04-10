package edu.sfwe405.campusmarketplace.service;

import edu.sfwe405.campusmarketplace.model.Product;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    public boolean hasStock(Product product, int quantity) {
        return product.getStock() >= quantity;
    }

    public void decrementStock(Product product, int quantity) {
        product.setStock(product.getStock() - quantity);
    }
}
