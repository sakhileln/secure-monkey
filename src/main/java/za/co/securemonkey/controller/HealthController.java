package za.co.securemonkey.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return ResponseEntity.ok("Database connection is healthy");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(503).body("Database connection failed: " + e.getMessage());
        }
        return ResponseEntity.status(503).body("Database connection is unhealthy");
    }
}
