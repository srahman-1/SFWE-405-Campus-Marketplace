package edu.sfwe405.campusmarketplace.service;

import edu.sfwe405.campusmarketplace.model.Payment;
import edu.sfwe405.campusmarketplace.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository repo;

    public PaymentService(PaymentRepository repo) {
        this.repo = repo;
    }

    public Payment create(Payment p) {
        return repo.save(p);
    }

    public List<Payment> getAll() {
        return repo.findAll();
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
