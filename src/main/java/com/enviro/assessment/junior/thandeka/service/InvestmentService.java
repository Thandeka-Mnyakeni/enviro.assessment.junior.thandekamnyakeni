package com.enviro.assessment.junior.thandeka.service;

import com.enviro.assessment.junior.thandeka.repository.InvestmentRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvestmentService {
    private final InvestmentRepository repository;

    public InvestmentService(InvestmentRepository repository) { this.repository = repository; }

    public Map<String, Object> portfolio() {
        var investor = repository.investor();
        var products = repository.products();
        BigDecimal total = products.stream().map(p -> (BigDecimal) p.get("BALANCE"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int age = Period.between(((java.sql.Date) investor.get("DOB")).toLocalDate(), LocalDate.now()).getYears();
        return Map.of("name", investor.get("NAME"), "age", age, "products", products, "total", total);
    }

    @Transactional
    public Map<String, Object> withdraw(Long productId, BigDecimal amount) {
        if (productId == null || amount == null)
            throw new IllegalArgumentException("Please select a product and enter an amount.");
        if (amount.signum() <= 0 || amount.scale() > 2)
            throw new IllegalArgumentException("Enter a positive amount with a maximum of 2 decimal places.");
        var matches = repository.productForUpdate(productId);
        if (matches.isEmpty()) throw new IllegalArgumentException("Investment product not found.");
        var product = matches.get(0);
        BigDecimal balance = (BigDecimal) product.get("BALANCE");
        if ("RETIREMENT".equals(product.get("TYPE"))) {
            LocalDate dob = ((java.sql.Date) repository.investor().get("DOB")).toLocalDate();
            if (!dob.isBefore(LocalDate.now().minusYears(65)))
                throw new IllegalArgumentException("Retirement withdrawals require an investor older than 65.");
        }
        if (amount.compareTo(balance) > 0)
            throw new IllegalArgumentException("Withdrawal cannot exceed the available balance.");
        BigDecimal maximum = balance.multiply(new BigDecimal("0.90")).setScale(2, RoundingMode.DOWN);
        if (amount.compareTo(maximum) > 0)
            throw new IllegalArgumentException("Maximum withdrawal is 90% of the balance (R " + maximum + ").");
        BigDecimal remaining = balance.subtract(amount);
        String reference = "ENV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        repository.saveWithdrawal(productId, (String) product.get("NAME"), amount, remaining, reference);
        return Map.of("reference", reference, "balanceAfter", remaining, "message", "Withdrawal submitted successfully.");
    }

    public List<Map<String, Object>> history(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to))
            throw new IllegalArgumentException("From date cannot be after to date.");
        return repository.history(from, to);
    }
}
