package edu.sfwe405.campusmarketplace.controller;

import edu.sfwe405.campusmarketplace.model.Category;
import edu.sfwe405.campusmarketplace.repository.CategoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository repo;

    public CategoryController(CategoryRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public Category create(@RequestBody Category c) {
        return repo.save(c);
    }

    @GetMapping
    public List<Category> all() {
        return repo.findAll();
    }
}
