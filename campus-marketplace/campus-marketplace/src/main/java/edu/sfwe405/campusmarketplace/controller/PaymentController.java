package edu.sfwe405.campusmarketplace.controller;

import edu.sfwe405.campusmarketplace.model.Payment;
import edu.sfwe405.campusmarketplace.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public Payment create(@RequestBody Payment p) {
        return service.create(p);
    }

    @GetMapping
    public List<Payment> all() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
