package com.enviro.assessment.junior.thandeka.controller;

import com.enviro.assessment.junior.thandeka.service.InvestmentService;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class InvestmentController {
    private final InvestmentService service;

    public InvestmentController(InvestmentService service) { this.service = service; }

    public record WithdrawalRequest(Long productId, BigDecimal amount) {}

    @GetMapping("/portfolio")
    public Map<String, Object> portfolio() { return service.portfolio(); }

    @PostMapping("/withdrawals")
    public Map<String, Object> withdraw(@RequestBody WithdrawalRequest request) {
        if (request == null) throw new IllegalArgumentException("Please enter a withdrawal.");
        return service.withdraw(request.productId(), request.amount());
    }

    @GetMapping("/withdrawals")
    public List<Map<String, Object>> history(@RequestParam(required = false) LocalDate from,
                                              @RequestParam(required = false) LocalDate to) {
        return service.history(from, to);
    }

    @GetMapping("/statement.csv")
    public ResponseEntity<byte[]> statement(@RequestParam(required = false) LocalDate from,
                                             @RequestParam(required = false) LocalDate to) {
        StringBuilder csv = new StringBuilder("Reference,Date,Product,Amount (ZAR),Balance After (ZAR)\r\n");
        for (var row : service.history(from, to)) {
            csv.append(csvCell(row.get("REFERENCE"))).append(',')
               .append(csvCell(row.get("CREATED_AT"))).append(',')
               .append(csvCell(row.get("PRODUCT_NAME"))).append(',')
               .append(csvCell(row.get("AMOUNT"))).append(',')
               .append(csvCell(row.get("BALANCE_AFTER"))).append("\r\n");
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=enviro365-statement.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String csvCell(Object value) {
        String text = String.valueOf(value == null ? "" : value);
        if (!text.isEmpty() && "=+-@".indexOf(text.charAt(0)) >= 0) text = "'" + text;
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> invalid(IllegalArgumentException error) {
        return ResponseEntity.badRequest().body(Map.of("error", error.getMessage()));
    }
}
