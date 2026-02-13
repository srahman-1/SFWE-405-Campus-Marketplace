package edu.sfwe405.campusmarketplace.service;

import edu.sfwe405.campusmarketplace.model.Category;
import edu.sfwe405.campusmarketplace.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    public Category create(Category c) {
        return repo.save(c);
    }

    public List<Category> getAll() {
        return repo.findAll();
    }
}


