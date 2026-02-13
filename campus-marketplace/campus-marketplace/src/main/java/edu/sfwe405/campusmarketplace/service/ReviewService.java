package edu.sfwe405.campusmarketplace.service;

import edu.sfwe405.campusmarketplace.model.Review;
import edu.sfwe405.campusmarketplace.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository repo;

    public ReviewService(ReviewRepository repo) {
        this.repo = repo;
    }

    public Review create(Review r) {
        return repo.save(r);
    }

    public List<Review> getAll() {
        return repo.findAll();
    }
}
